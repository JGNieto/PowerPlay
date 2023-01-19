package org.baylorschool.opmodes.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.util.Mecanum
import org.baylorschool.vision.AprilTagBinaryPipeline
import org.baylorschool.vision.CameraUtil

@Autonomous(name = "RRConeParkOdometry", group = "RedRight")
class RRConeParkOdometry: LinearOpMode() {

    override fun runOpMode() {
        telemetry.addData("Status", "Getting ready. Please wait...")
        telemetry.update()

        val pipeline = AprilTagBinaryPipeline()
        val webcam = CameraUtil.openWebcam(this, pipeline, true)
        val mecanum = Mecanum(hardwareMap)

        telemetry.addData("Status", "Ready to start")
        telemetry.update()

        mecanum.resetEncoders()

        waitForStart()

        val targetTag = pipeline.determineTargetTag()

        telemetry.addLine("Tag $targetTag")
        telemetry.update()


    }

}