package org.baylorschool.util

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.baylorschool.util.BasicMotorAngleDeviceConfig.stopSpeed
import org.baylorschool.util.BasicMotorAngleDeviceConfig.maintainingSpeed
import org.baylorschool.util.BasicMotorAngleDeviceConfig.movingSpeed
import kotlin.math.PI
import kotlin.math.abs

@Config
object BasicMotorAngleDeviceConfig {
    @JvmField var stopSpeed = 0.0
    @JvmField var maintainingSpeed = 0.0
    @JvmField var movingSpeed = 0.8
}

class BasicMotorAngleDevice(val motor: DcMotorEx, ticksPerTurn: Double): AngleDevice {
    constructor(opMode: OpMode, motorName: String, ticksPerTurn: Double, direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD):
            this(opMode.hardwareMap.get(DcMotorEx::class.java, motorName), ticksPerTurn) {
        motor.direction = direction
    }

    private val ticksPerRadian = ticksPerTurn / (2 * PI)

    private var targetAngle = 0.0
    private var direction = 0
    private var encoderValueAtZero = 0.0
    private var needToStop = false
    private var motorStatus = MotorStatus.STOP

    private var wasBusy = false
    private var previousPosition: Double? = null

    private lateinit var thread: Thread

    private enum class MotorStatus {
        STOP, MOVING, MAINTAINING
    }

    override fun moveToAngle(angle: Double, direction: Int) {
        this.motorStatus = MotorStatus.MOVING
        this.targetAngle = angle
        this.direction = direction
    }

    private fun computeTargetAngle(angle: Double, direction: Int): Double {
        val position = getPosition()
        val baseValue = (position / (2 * PI)).toInt()
        val lowerValue = baseValue + angle
        val higherValue = baseValue + angle - 2 * PI

        return when (direction) {
            1 -> {
                higherValue
            }
            -1 -> {
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
        if (motorStatus == MotorStatus.STOP) {
            motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            motor.power = stopSpeed
        } else {
            if (!motor.isBusy && wasBusy) {
                motorStatus = MotorStatus.STOP
                motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
                motor.power = stopSpeed
            } else if (!wasBusy) {
                val targetAngle = computeTargetAngle(targetAngle, direction)
                val targetEncoder = targetAngle * ticksPerRadian + encoderValueAtZero

                motor.targetPosition = targetEncoder.toInt()
                motor.mode = DcMotor.RunMode.RUN_TO_POSITION
                motor.power = movingSpeed
            }
        }

        wasBusy = motor.isBusy
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