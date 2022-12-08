package org.baylorschool.opmodes.test

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.Globals
import org.baylorschool.drive.Mecanum
import org.baylorschool.util.Claw
import org.baylorschool.util.angledevice.BasicMotorAngleDevice


@TeleOp(name = "Jackson DL Test Better", group = "test")
class JacksonDLTestBetter: LinearOpMode() {

    val POWER_FAST = 0.6
    val POWER_SLOW = 0.3


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
        var powerMultiplier = POWER_FAST

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

            if (gamepad1.y) {
                powerMultiplier = POWER_FAST;
            } else if (gamepad1.a) {
                powerMultiplier = POWER_SLOW;
            }

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
            telemetry.addData("Distal power", motorB.teleOpPower)

            telemetry.addData("leftFront", mecanum.leftFront.power)
            telemetry.addData("rightFront", mecanum.rightFront.power)
            telemetry.addData("leftRear", mecanum.leftRear.power)
            telemetry.addData("rightRear", mecanum.rightRear.power)

            telemetry.update()
        }

        motorB.cleanup()
    }

    private fun map(inputMin: Double, inputMax: Double, outputMin: Double, outputMax: Double, value: Double): Double {
        return (value-inputMin)/(inputMax-inputMin) * (outputMax-outputMin) + outputMin
    }
}