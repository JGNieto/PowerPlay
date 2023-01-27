package org.baylorschool.opmodes.autonomous.encoder

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.Globals
import org.baylorschool.util.EncoderPosition
import org.baylorschool.util.Mecanum
import org.baylorschool.vision.AprilTagBinaryPipeline
import org.baylorschool.vision.CameraUtil

@Autonomous(name = "LeftCameraParkEncoder", group = "BB Encoder Left")
class LeftCameraParkEncoder: LinearOpMode() {

    val POWER = 0.5

    val forward = EncoderPosition(-1400, -1400, -1400, -1400)
    val right = EncoderPosition(-1051, 1250, 1250, -1051)
    val left = EncoderPosition(1332, -1500, -1500, 1332)

    override fun runOpMode() {
        telemetry.addData("Status", "Getting ready. Please wait...")
        telemetry.update()

        val pipeline = AprilTagBinaryPipeline()
        val webcam = CameraUtil.openWebcam(this, pipeline, true, Globals.webcamRear)
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