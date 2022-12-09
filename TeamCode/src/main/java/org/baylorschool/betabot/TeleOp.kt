package org.baylorschool.betabot

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.betabot.lib.*

@TeleOp(name = "Teleop",group = "Test")
class TeleOp: LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val mecanum = Mecanum(hardwareMap)
        val lSlides = HorizontalSlides(hardwareMap)
        val slides = Slides(hardwareMap)
        val v4bIntake = V4B(hardwareMap)

        waitForStart()

            while (opModeIsActive()) {
                slides.slideLoop(gamepad1)
                mecanum.mecanumLoop(gamepad1)
                lSlides.horizSlideLoop(gamepad1)
                v4bIntake.v4bLoop(gamepad1)

                slides.telemetry(telemetry)
                mecanum.telemetry(telemetry)
                lSlides.telemetry(telemetry)
                v4bIntake.telemetry(telemetry)
        }
    }
}