package org.baylorschool.opmodes.test

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.Globals
import org.baylorschool.util.Mecanum
import org.baylorschool.util.Claw
import org.baylorschool.util.angledevice.BasicMotorAngleDevice
import kotlin.math.PI


@TeleOp(name = "Jackson DL Test Better", group = "test")
class JacksonDLTestBetter: LinearOpMode() {
    private val PRESET_1_DISTAL = 110.0
    private val PRESET_1_PROXIMAL = 80.0

    override fun runOpMode() {
        PhotonCore.enable()

        val motorA1 = hardwareMap.get(DcMotorEx::class.java, Globals.liftProximalA)
        val motorB = BasicMotorAngleDevice(this, Globals.liftDistal, Globals.liftDistalTicksPerRotation, Globals.liftDistalConfig, Globals.liftDistalDirection)
        val claw = Claw(this)
        val clawPitch = hardwareMap.get(Servo::class.java, Globals.clawPitch)
        val mecanum = Mecanum(hardwareMap)
        val telemetry = MultipleTelemetry(FtcDashboard.getInstance().telemetry, telemetry)

        waitForStart()

        motorA1.direction = Globals.liftProximalADirection
        motorA1.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorA1.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motorA1.mode = DcMotor.RunMode.RUN_USING_ENCODER

        motorB.init()
        motorB.reset(0.0)
        motorB.debug = false

        clawPitch.direction = Globals.clawPitchDirection

        var clawPosition = 0.0

        var wasMoving = false

        var previousTime = System.currentTimeMillis()

        // RIGHT = DISTAL
        // LEFT = PROXIMAL
        while (opModeIsActive()) {
            val currentTime = System.currentTimeMillis()
            val dt = (currentTime - previousTime) / 1000.0

            if (gamepad2.dpad_up) {
                motorA1.targetPosition = (PRESET_1_PROXIMAL * Globals.liftProximalATicksPerRotation / (2 * PI)).toInt()
                motorA1.mode = DcMotor.RunMode.RUN_TO_POSITION
                motorA1.power = Globals.liftProximalConfig.movingSpeed
            } else if (gamepad2.left_stick_y != 0f || motorA1.mode == DcMotor.RunMode.RUN_USING_ENCODER) {
                if (motorA1.mode != DcMotor.RunMode.RUN_USING_ENCODER)
                    motorA1.mode = DcMotor.RunMode.RUN_USING_ENCODER
                motorA1.power = gamepad2.left_stick_y * Globals.liftProximalConfig.teleOpSpeed
            }

            if (gamepad2.right_stick_y != 0f) {
                wasMoving = true
                motorB.moveTeleOp(gamepad2.right_stick_y * Globals.liftDistalConfig.teleOpSpeed)
            } else if (gamepad2.dpad_up) {
                motorB.moveToAngle(PRESET_1_DISTAL)
            } else if (wasMoving) {
                motorB.moveToAngle(motorB.getPosition())
                motorB.motorStatus = BasicMotorAngleDevice.MotorStatus.MAINTAINING
                wasMoving = false
            }

            claw.grabPosition(gamepad2.right_trigger.toDouble())

            mecanum.mecanumLoop(gamepad1)

            if (gamepad2.x) {
                clawPosition = 1.0
            }
            if (gamepad2.b) {
                clawPosition = 0.0
            }

            if (gamepad2.right_bumper) {
                clawPosition += 0.4 * dt
            }

            if (gamepad2.left_bumper) {
                clawPosition -= 0.4 * dt
            }

            clawPosition = clawPosition.coerceIn(0.0, 1.0)

            clawPitch.position = clawPosition

            // mecanum.telemetry(telemetry)
            telemetry.addData("Claw position", clawPosition)
            telemetry.addData("Proximal position", motorA1.currentPosition)
            telemetry.addData("Distal position", motorB.getPosition())
            telemetry.update()

            previousTime = currentTime
        }

        motorB.cleanup()
    }

    private fun map(inputMin: Double, inputMax: Double, outputMin: Double, outputMax: Double, value: Double): Double {
        return (value-inputMin)/(inputMax-inputMin) * (outputMax-outputMin) + outputMin
    }
}