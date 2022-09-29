package org.baylorschool.util

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.arcrobotics.ftclib.controller.wpilibcontroller.ArmFeedforward
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.baylorschool.util.MotorAngleDeviceConfig.ka
import org.baylorschool.util.MotorAngleDeviceConfig.kcos
import org.baylorschool.util.MotorAngleDeviceConfig.ks
import org.baylorschool.util.MotorAngleDeviceConfig.kv
import kotlin.math.PI

@Config
object MotorAngleDeviceConfig {
    @JvmField var ks = 0.6
    @JvmField var kcos = 0.2
    @JvmField var kv = -0.2
    @JvmField var ka = -0.05

}

class MotorAngleDevice(val motor: DcMotorEx, ticksPerTurn: Double): AngleDevice {
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

    private val pid = ArmFeedforward(ks, kcos, kv, ka)

    private lateinit var thread: Thread

    var motionPower = .5
    var stablePower = .2

    enum class MotorStatus {
        STOP, MOVING, STABLE
    }

    override fun moveToAngle(angle: Double, direction: Int) {
        this.motorStatus = MotorStatus.MOVING
        this.targetAngle = angle
        this.direction = direction
    }

    // This code is based on the built-in PID loop supplied by FTC. We might want to make our own.
    /*private fun iteration() {
        val packet = TelemetryPacket()
        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, PIDFCoefficients(p, i, d, f))

        if (motorStatus == MotorStatus.STOP) {
            motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
            motor.power = 0.0
        } else {
            if (motorStatus == MotorStatus.MOVING) {
                if (wasMoving && !motor.isBusy) {
                    wasMoving = false
                    motorStatus = MotorStatus.STABLE
                } else if (!wasMoving) {
                    motor.targetPosition = (targetAngle * ticksPerRadian).toInt()
                    motor.mode = DcMotor.RunMode.RUN_TO_POSITION
                    motor.power = motionPower
                }
            } else if (motorStatus == MotorStatus.STABLE) {
                motor.targetPosition = (targetAngle * ticksPerRadian).toInt()
                motor.mode = DcMotor.RunMode.RUN_TO_POSITION
                motor.power = stablePower
            }
        }
        packet.put("Encoder value", motor.currentPosition)
        packet.put("Position", getPosition())
        packet.put("Status", motorStatus)
        packet.put("Zero value", encoderValueAtZero)
        packet.put("Target angle", targetAngle)
        packet.put("Target position", motor.targetPosition)
        packet.put("Target tolerance", motor.targetPositionTolerance)
        val coeff = motor.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION)
        packet.put("P", coeff.p)
        packet.put("I", coeff.p)
        packet.put("D", coeff.p)
        packet.put("F", coeff.p)
        FtcDashboard.getInstance().sendTelemetryPacket(packet)
    }*/

    private fun iteration() {
        if (motorStatus == MotorStatus.STOP) {
            motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
            motor.power = 0.0
        } else {
            if (motor.mode != DcMotor.RunMode.RUN_WITHOUT_ENCODER)
                motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

            motor.power = clip(pid.calculate(targetAngle - getPosition(), motor.velocity / ticksPerRadian))
        }
        val packet = TelemetryPacket()
        packet.put("Encoder value", motor.currentPosition)
        packet.put("Power", motor.power)
        packet.put("Position", getPosition())
        packet.put("Status", motorStatus)
        packet.put("Zero value", encoderValueAtZero)
        packet.put("Target angle", targetAngle)
        packet.put("Velocity", motor.velocity)
        FtcDashboard.getInstance().sendTelemetryPacket(packet)
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

    private fun clip(x: Double): Double {
        if (x > 1.0) return 1.0
        if (x < -1.0) return -1.0
        return x
    }
}