package org.baylorschool.opmodes.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.util.EncoderPosition
import org.baylorschool.util.Mecanum
import org.baylorschool.vision.AprilTagBinaryPipeline
import org.baylorschool.vision.CameraUtil

@Autonomous(name = "RRCameraParkEncoder", group = "RedRight")
class RRCameraParkEncoder: LinearOpMode() {

    val POWER = 0.5

    val forward = EncoderPosition(-1400, -1400, -1400, -1400)
    val left = EncoderPosition(1051, -1250, -1250, 1051)
    val right = EncoderPosition(-1019, 1320, 1320, -1057)

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
        telemetry.addLine("Moving sideways")
        telemetry.update()

        if (targetTag == 0) {
            mecanum.moveToPositionIncremental(left)
            mecanum.setPower(POWER)
            mecanum.waitForNotBusy(this, true)
        } else if (targetTag == 2) {
            mecanum.moveToPositionIncremental(right)
            mecanum.setPower(POWER)
            mecanum.waitForNotBusy(this, true)
        }


        telemetry.addLine("Moving forward")
        telemetry.update()

        mecanum.moveToPositionIncremental(forward)
        mecanum.setPower(POWER)
        mecanum.waitForNotBusy(this, true)


        webcam.closeCameraDevice()
    }

}