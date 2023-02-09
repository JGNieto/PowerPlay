package org.baylorschool.opmodes.test

import com.acmerobotics.roadrunner.geometry.Pose2d
//import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.Globals
import org.baylorschool.drive.Mecanum
import org.baylorschool.util.Claw

@Disabled
@TeleOp(name = "Jackson DL Test", group = "test")
class JacksonDLTest: LinearOpMode() {
    private var powerMultiplier = 0.1

    override fun runOpMode() {
        //PhotonCore.enable()

        val motorA1 = hardwareMap.get(DcMotorEx::class.java, Globals.liftProximalA)
        val motorB = hardwareMap.get(DcMotorEx::class.java, Globals.liftDistal)
        val claw = Claw(this)
        val clawPitch = hardwareMap.get(Servo::class.java, Globals.clawPitch)
        val mecanum = Mecanum(hardwareMap)

        waitForStart()

        motorA1.direction = Globals.liftProximalADirection
        motorB.direction = Globals.liftDistalDirection

        motorA1.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorB.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        motorA1.mode = DcMotor.RunMode.RUN_USING_ENCODER
        motorB.mode = DcMotor.RunMode.RUN_USING_ENCODER

        clawPitch.direction = Globals.clawPitchDirection

        var heavenClaw = false

        // RIGHT = DISTAL
        // LEFT = PROXIMAL
        while (opModeIsActive()) {
            motorA1.power = - gamepad2.left_stick_y * Globals.liftProximalConfig.teleOpSpeed
            motorB.power = - gamepad2.right_stick_y * Globals.liftDistalConfig.teleOpSpeed

            claw.grabPosition(gamepad2.right_trigger.toDouble())

            mecanum.setDrivePower(
                Pose2d(
                    -gamepad1.left_stick_y.toDouble() * powerMultiplier,
                    -gamepad1.left_stick_x.toDouble() * powerMultiplier,
                    -gamepad1.right_stick_x.toDouble() * powerMultiplier,
                )
            )

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

            telemetry.addData("Left stick Y", gamepad2.left_stick_y)
            telemetry.addData("Proximal power", motorA1.power)
            telemetry.addData("Right stick Y", gamepad2.right_stick_y)
            telemetry.addData("Distal power", motorB.power)
            telemetry.update()
        }
    }

    private fun map(inputMin: Double, inputMax: Double, outputMin: Double, outputMax: Double, value: Double): Double {
        return (value-inputMin)/(inputMax-inputMin) * (outputMax-outputMin) + outputMin
    }
}