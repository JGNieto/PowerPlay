package org.baylorschool.opmodes.autonomous.encoder

import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.Globals
import org.baylorschool.util.EncoderPosition
import org.baylorschool.util.Mecanum
import org.baylorschool.vision.AprilTagBinaryPipeline
import org.baylorschool.vision.CameraUtil

@Autonomous(name = "RightCameraParkEncoder", group = "BB Encoder Right")
class RightCameraParkEncoder: LinearOpMode() {

    val POWER = 0.5

    val forward = EncoderPosition(-1400, -1400, -1400, -1400)
    val left = EncoderPosition(1051, -1250, -1250, 1051)
    val right = EncoderPosition(-1332, 1500, 1500, -1332)

    override fun runOpMode() {
        PhotonCore.enable()

        telemetry.addData("Status", "Getting ready. Please wait...")
        telemetry.update()

        val pipeline = AprilTagBinaryPipeline(telemetry, Globals.webcamRearRotate)
        val webcam = CameraUtil.openWebcam(this, pipeline, true, Globals.webcamRear)
        val mecanum = Mecanum(hardwareMap)

        telemetry.addData("Status", "I'm looking for tags...")
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