package org.baylorschool.util

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.PI

class MotorAngleDevice(val motor: DcMotorEx, ticksPerTurn: Int): AngleDevice {
    constructor(opMode: OpMode, motorName: String, ticksPerTurn: Int, direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD):
            this(opMode.hardwareMap.get(DcMotorEx::class.java, motorName), ticksPerTurn) {
                motor.direction = direction
            }

    private val ticksPerRadian = ticksPerTurn / (2 * PI)

    private var targetAngle = 0.0
    private var direction = 0
    private var encoderValueAtZero = 0.0
    private var needToStop = false
    private var motorStatus = MotorStatus.STOP
    private var wasMoving = false

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
    private fun iteration() {
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
    }

    override fun cleanup() {
        needToStop = true
    }
}