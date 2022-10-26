package org.baylorschool.util.angledevice

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import kotlin.math.PI
import kotlin.math.abs

data class BasicMotorAngleConfig(val stopSpeed: Double, val maintainingSpeed: Double, val movingSpeed: Double)

class BasicMotorAngleDevice(val motor: DcMotorEx, ticksPerTurn: Double, val config: BasicMotorAngleConfig):
    AngleDevice {
    constructor(opMode: OpMode, motorName: String, ticksPerTurn: Double, config: BasicMotorAngleConfig, direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD):
            this(opMode.hardwareMap.get(DcMotorEx::class.java, motorName), ticksPerTurn, config) {
        motor.direction = direction
    }

    init {
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    private val ticksPerRadian = ticksPerTurn / (2 * PI)

    private var targetAngle = 0.0
    private var direction = TargetAngleDirection.ABSOLUTE
    private var encoderValueAtZero = 0.0
    private var needToStop = false
    private var motorStatus = MotorStatus.STOP
    var debug = false

    private var wasBusy = false
    private var newPosition = false
    
    private val telemetry = FtcDashboard.getInstance().telemetry

    private var lastEncoderUpdate = 0L
    private var previousEncoderValue = 0

    private lateinit var thread: Thread

    private enum class MotorStatus {
        STOP, MOVING, MAINTAINING
    }

    override fun moveToAngle(angle: Double, direction: TargetAngleDirection) {
        this.motorStatus = MotorStatus.MOVING
        this.targetAngle = angle
        this.direction = direction
        this.newPosition = true
    }

    private fun computeTargetAngle(angle: Double, direction: TargetAngleDirection): Double {
        if (direction == TargetAngleDirection.ABSOLUTE) return angle

        val position = getPosition()
        // TODO: val baseValue = (position % (2 * PI)).toInt()
        val baseValue = 0
        val higherValue = baseValue + angle
        val lowerValue = baseValue + angle - 2 * PI

        return when (direction) {
            TargetAngleDirection.COUNTERCLOCKWISE -> {
                higherValue
            }
            TargetAngleDirection.CLOCKWISE -> {
                lowerValue
            }
            else -> {
                if (abs(position - lowerValue) < abs(position - higherValue)) {
                    lowerValue
                } else {
                    higherValue
                }
            }
        }
    }

    private fun iteration() {
        val currentTime = System.currentTimeMillis()
        when (motorStatus) {
            MotorStatus.STOP -> {
                motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
                motor.power = config.stopSpeed
            }
            else -> {
                if (!motor.isBusy && wasBusy && !newPosition) {
                    motorStatus = MotorStatus.MAINTAINING
                } else if (!wasBusy || newPosition) {
                    val targetAngleComputed = computeTargetAngle(targetAngle, direction)
                    val targetEncoder = targetAngleComputed * ticksPerRadian + encoderValueAtZero

                    motor.targetPosition = targetEncoder.toInt()
                    motor.mode = DcMotor.RunMode.RUN_TO_POSITION
                    motor.power = when (motorStatus) {
                        MotorStatus.MOVING -> config.movingSpeed
                        else -> {
                            config.maintainingSpeed
                        }
                    }

                    this.newPosition = false
                }
            }
        }

        if (motor.currentPosition != previousEncoderValue || motor.power == 0.0 || lastEncoderUpdate == 0L || (motor.mode != DcMotor.RunMode.RUN_TO_POSITION && motor.mode != DcMotor.RunMode.RUN_USING_ENCODER)) {
            previousEncoderValue = motor.currentPosition
            lastEncoderUpdate = currentTime
        } else if (lastEncoderUpdate - currentTime > 500) {
            throw Exception("SAFETY ISSUE DETECTED")
        }

        wasBusy = motor.isBusy

        if (debug) {
            telemetry.addData("Encoder value", motor.currentPosition)
            telemetry.addData("Power", motor.power)
            telemetry.addData("Position", getPosition())
            telemetry.addData("Status", motorStatus)
            telemetry.addData("Zero value", encoderValueAtZero)
            telemetry.addData("Target angle", targetAngle)
            telemetry.addData("Target position", motor.targetPosition)
            telemetry.addData("Current position", motor.currentPosition)
            telemetry.addData("Velocity", motor.getVelocity(AngleUnit.RADIANS))
            telemetry.update()
        }
    }

    override fun reset(angle: Double) {
        encoderValueAtZero = motor.currentPosition - angle * ticksPerRadian
    }

    override fun getPosition(): Double {
        return (motor.currentPosition - encoderValueAtZero) / ticksPerRadian
    }

    override fun init() {
        if (::thread.isInitialized) {
            return
        }
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        thread = Thread {
            while (!needToStop)
                iteration()
        }
        thread.start()
    }

    override fun stop() {
        motorStatus = MotorStatus.STOP
        motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        motor.power = 0.0
    }

    override fun cleanup() {
        stop()
        needToStop = true
    }
}