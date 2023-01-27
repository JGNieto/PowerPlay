package org.baylorschool.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.vision.CameraUtil
import org.baylorschool.vision.YellowJunctionPipeline

@TeleOp(name = "Junction CV Test", group = "test")
class YellowJunctionCVTest: LinearOpMode() {

    override fun runOpMode() {
        telemetry.addData("Status", "Getting ready. Please wait...")
        telemetry.update()

        val pipeline = YellowJunctionPipeline()
        val webcam = CameraUtil.openWebcam(this, pipeline, true)

        telemetry.addData("Status", "Ready to start")
        telemetry.update()
        waitForStart()

        var iterations = 0L

        while (opModeIsActive()) {
            telemetry.addData("Status", "Started!")
            telemetry.addData("Iterations", iterations)
            telemetry.update()
        }

        telemetry.addData("Status", "Closing webcam...")
        telemetry.update()
        CameraUtil.stop(webcam)

        telemetry.addData("Status", "Done!")
        telemetry.update()
    }
}