package org.baylorschool.betabot

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.betabot.lib.*

@TeleOp(name = "Teleop",group = "betatest")
class TeleOp: LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val telemetryMultiple = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        val mecanum = Mecanum(hardwareMap)
       // val lSlides = HorizontalSlides(hardwareMap)
        val slides = Slides(hardwareMap)
        val v4bIntake = V4BIntake(hardwareMap)

        waitForStart()

            while (opModeIsActive()) {
                slides.slideLoop(gamepad2)
                mecanum.mecanumLoop(gamepad1)
              //  lSlides.horizSlideLoop(gamepad1)
                v4bIntake.intakeLoop(gamepad2)

                mecanum.telemetry(telemetryMultiple)
                slides.telemetry(telemetryMultiple)
                v4bIntake.telemetry(telemetryMultiple)
                telemetryMultiple.update()
        }
    }
}