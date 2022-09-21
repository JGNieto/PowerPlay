package org.baylorschool.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "April Tag Test", group = "test")
class AprilTagTest: LinearOpMode() {

    override fun runOpMode() {
        telemetry.addData("Status", "Ready to start")
        telemetry.update()

        waitForStart()

        var iterations = 0L

        while (opModeIsActive()) {
            iterations++

            telemetry.addData("Status", "Started!")
            telemetry.addData("Iterations", iterations)
            telemetry.update()
        }
    }
}