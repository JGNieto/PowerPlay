package org.baylorschool.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.Globals
import org.baylorschool.vision.AprilTagBinaryPipeline
import org.baylorschool.vision.CameraUtil

@TeleOp(name = "April Tag Test", group = "test")
class AprilTagTest: LinearOpMode() {

    override fun runOpMode() {
        telemetry.addData("Status", "Getting ready. Please wait...")
        telemetry.update()

        val pipeline = AprilTagBinaryPipeline(null, Globals.webcamRearRotate)
        val webcam = CameraUtil.openWebcam(this, pipeline, true)

        telemetry.addData("Status", "Ready to start")
        telemetry.update()
        waitForStart()

        var iterations = 0L

        while (opModeIsActive()) {
            iterations++

            var visibleTagsStr = ""
            var isFirst = true

            for (tag in pipeline.visibleAprilTags) {
                if (isFirst) {
                    visibleTagsStr += "${tag.id}"
                    isFirst = false
                } else {
                    visibleTagsStr += ", ${tag.id}"
                }
            }

            telemetry.addData("Visible tags", visibleTagsStr)
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