package org.baylorschool.opmodes.test

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.drive.Mecanum

@Disabled
@TeleOp(name = "Mecanum Test", group = "test")
class MecanumTest: LinearOpMode() {
    private val POWER_MULTIPLIER = 0.1

    override fun runOpMode() {
        PhotonCore.enable()

        val mecanum = Mecanum(hardwareMap)

        waitForStart()

        while (opModeIsActive()) {
            mecanum.setDrivePower(Pose2d(
                -gamepad1.left_stick_y.toDouble() * POWER_MULTIPLIER,
                -gamepad1.left_stick_x.toDouble() * POWER_MULTIPLIER,
                -gamepad1.right_stick_x.toDouble() * POWER_MULTIPLIER,
            ))

            telemetry.addData("X", gamepad1.left_stick_x)
            telemetry.addData("Y", gamepad1.left_stick_y)
            telemetry.addData("Rot", gamepad1.right_stick_x)
            telemetry.update()
        }
    }
}