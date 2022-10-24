package org.baylorschool.util.angledevice

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.arcrobotics.ftclib.controller.PIDController
import com.arcrobotics.ftclib.controller.wpilibcontroller.ArmFeedforward
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.baylorschool.util.angledevice.MotorAngleDeviceConfig.armKa
import org.baylorschool.util.angledevice.MotorAngleDeviceConfig.armKcos
import org.baylorschool.util.angledevice.MotorAngleDeviceConfig.armKs
import org.baylorschool.util.angledevice.MotorAngleDeviceConfig.armKv
import org.baylorschool.util.angledevice.MotorAngleDeviceConfig.kd
import org.baylorschool.util.angledevice.MotorAngleDeviceConfig.ki
import org.baylorschool.util.angledevice.MotorAngleDeviceConfig.kp
import org.baylorschool.util.angledevice.MotorAngleDeviceConfig.tolerance
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import kotlin.math.PI

@Config
object MotorAngleDeviceConfig {
    @JvmField var armKs = 0.2
    @JvmField var armKcos = 0.2
    @JvmField var armKv = 0.4
    @JvmField var armKa = 0.0

    @JvmField var kp = 0.6
    @JvmField var ki = 0.6
    @JvmField var kd = 0.1
    @JvmField var tolerance = 0.2
}

class MotorAngleDevice(val motor: DcMotorEx, ticksPerTurn: Double): AngleDevice {
    constructor(opMode: OpMode, motorName: String, ticksPerTurn: Double, direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD):
            this(opMode.hardwareMap.get(DcMotorEx::class.java, motorName), ticksPerTurn) {
                motor.direction = direction
            }

    private val ticksPerRadian = ticksPerTurn / (2 * PI)

    private var targetAngle = 0.0
    private var direction = TargetAngleDirection.ABSOLUTE
    private var encoderValueAtZero = 0.0
    private var needToStop = false
    private var motorStatus = MotorStatus.STOP

    private var previousPosition: Double? = null

    private val armFeedForward = ArmFeedforward(armKs, armKcos, armKv, armKa)
    private val pid = PIDController(kp, ki, kd)

    private lateinit var thread: Thread

    private enum class MotorStatus {
        STOP, MOVING
    }

    override fun moveToAngle(angle: Double, direction: TargetAngleDirection) {
        this.motorStatus = MotorStatus.MOVING
        this.targetAngle = angle
        this.direction = direction
    }

    private fun iteration() {
        val packet = TelemetryPacket()
        if (motorStatus == MotorStatus.STOP) {
            previousPosition = null
            motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
            motor.power = 0.0
            packet.put("Calculation PID", 0)
            packet.put("Calculation Feedforward", 0)
        } else {
            if (motor.mode != DcMotor.RunMode.RUN_WITHOUT_ENCODER)
                motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

            val position = getPosition()

            val calculationPID = pid.calculate(position, targetAngle)

            var acceleration = 0.0
            if (previousPosition != null && pid.period > 0) {
                acceleration = pid.period * (previousPosition!! - position)
            }

            val calculationFeedforward = armFeedForward.calculate(position, calculationPID, acceleration)

            packet.put("Calculation PID", calculationPID)
            packet.put("Calculation Feedforward", calculationFeedforward)

            previousPosition = position
            motor.power = clip(calculationFeedforward)
        }
        packet.put("Encoder value", motor.currentPosition)
        packet.put("Power", motor.power)
        packet.put("Position", getPosition())
        packet.put("Status", motorStatus)
        packet.put("Zero value", encoderValueAtZero)
        packet.put("Target angle", targetAngle)
        packet.put("Velocity", motor.getVelocity(AngleUnit.RADIANS))
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
        pid.setTolerance(tolerance)
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