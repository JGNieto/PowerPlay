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


@TeleOp(name = "Jackson DL Test Better", group = "test")
class JacksonDLTestBetter: LinearOpMode() {

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
        motorA1.mode = DcMotor.RunMode.RUN_USING_ENCODER

        motorB.init()
        motorB.debug = false

        clawPitch.direction = Globals.clawPitchDirection

        var wasMoving = false
        var heavenClaw = false

        // RIGHT = DISTAL
        // LEFT = PROXIMAL
        while (opModeIsActive()) {
            motorA1.power = - gamepad2.left_stick_y * Globals.liftProximalConfig.teleOpSpeed

            if (gamepad2.right_stick_y != 0f) {
                wasMoving = true
                motorB.moveTeleOp(- gamepad2.right_stick_y * Globals.liftDistalConfig.teleOpSpeed)
            } else if (wasMoving) {
                motorB.moveToAngle(motorB.getPosition())
                motorB.motorStatus = BasicMotorAngleDevice.MotorStatus.MAINTAINING
                wasMoving = false
            }

            claw.grabPosition(gamepad2.right_trigger.toDouble())

            mecanum.mecanumLoop(gamepad1)

            if (gamepad2.right_bumper) {
                heavenClaw = true
            }
            if (gamepad2.left_bumper) {
                heavenClaw = false
            }

            if (heavenClaw && clawPitch.position != 1.0) {
                clawPitch.position = 1.0
            } else if (!heavenClaw && clawPitch.position != -1.0) {
                clawPitch.position = -1.0
            }

            mecanum.telemetry(telemetry)
            telemetry.addData("Distal position", motorB.getPosition())
            telemetry.update()
        }

        motorB.cleanup()
    }

    private fun map(inputMin: Double, inputMax: Double, outputMin: Double, outputMax: Double, value: Double): Double {
        return (value-inputMin)/(inputMax-inputMin) * (outputMax-outputMin) + outputMin
    }
}