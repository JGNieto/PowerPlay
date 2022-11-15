package org.baylorschool.opmodes.test

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.drive.Mecanum


@TeleOp(name = "Mecanum Test", group = "test")
class MecanumTest: LinearOpMode() {
    private val POWER_MULTIPLIER = 0.4

    override fun runOpMode() {
        PhotonCore.enable()

        val mecanum = Mecanum(hardwareMap)

        waitForStart()

        while (opModeIsActive()) {
            mecanum.setDrivePower(Pose2d(
                gamepad1.left_stick_x.toDouble() * POWER_MULTIPLIER,
                -gamepad1.left_stick_y.toDouble() * POWER_MULTIPLIER,
                gamepad1.right_stick_x.toDouble() * POWER_MULTIPLIER,
            ))
        }
    }
}