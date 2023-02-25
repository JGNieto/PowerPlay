package org.baylorschool.betabot

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.exception.RobotCoreException
import org.baylorschool.betabot.lib.*

@TeleOp(name = "Teleop",group = "betatest")
class TeleOp: LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val telemetryMultiple = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        val mecanum = Mecanum(hardwareMap)
        val slides = Slides(hardwareMap)
        val v4bIntake = V4B(hardwareMap)
        val fsm = FSM(hardwareMap)
        val currentGP2 = fsm.currentGamepad2
        val previousGP2 = fsm.previousGamepad2

        waitForStart()

            while (opModeIsActive()) {
                try {
                    previousGP2.copy(currentGP2)
                    currentGP2.copy(gamepad2)
                } catch (e: RobotCoreException) {

                }

                mecanum.mecanumLoop(gamepad1)
                v4bIntake.intakeLoop(gamepad2)
                slides.slideLoop(gamepad2)
                fsm.fsmLoop(gamepad1)

                mecanum.telemetry(telemetryMultiple)
                v4bIntake.telemetry(telemetryMultiple)
                slides.telemetry(telemetryMultiple)
                fsm.telemetry(telemetryMultiple)
                telemetryMultiple.update()
        }
    }
}