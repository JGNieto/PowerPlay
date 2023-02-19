package org.baylorschool.opmodes.autonomous.odometry.right

//import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.Globals
import org.baylorschool.drive.Mecanum
import org.baylorschool.util.Claw
import org.baylorschool.vision.AprilTagBinaryPipeline
import org.baylorschool.vision.CameraUtil

@Autonomous(name = "RightParkOdometry", group = "AA Odometry Right")
class RightCameraParkOdometry: LinearOpMode() {

    // POSITIONS ARE DESIGNED FOR RIGHT RED, BUT WORK FOR RIGHT BLUE AS WELL
    private val startPosition = Globals.rightStartPosition.copy()

    override fun runOpMode() {
        //PhotonCore.enable()

        telemetry.addData("Status", "Getting ready. Please wait...")
        telemetry.update()

        val pipeline = AprilTagBinaryPipeline(telemetry, Globals.webcamRearRotate)
        val webcam = CameraUtil.openWebcam(this, pipeline, true, Globals.webcamRear)
        val mecanum = Mecanum(hardwareMap)
        val claw = Claw(this)

        claw.close()

        telemetry.addData("Status", "I'm looking for tags...")
        telemetry.update()

        waitForStart()

        val targetTag = pipeline.determineTargetTag()

        telemetry.addLine("Tag $targetTag")
        telemetry.update()

        mecanum.poseEstimate = startPosition

        var endPosition = startPosition

        telemetry.addLine("Moving sideways")
        telemetry.update()

        if (targetTag == 0) {
            val trajSideways = mecanum.trajectoryBuilder(startPosition)
                .strafeRight(20.0)
                .build()
            endPosition = trajSideways.end()
            mecanum.followTrajectory(trajSideways)
        } else if (targetTag == 1) {
            val trajSideways = mecanum.trajectoryBuilder(startPosition)
                .strafeLeft(2.0)
                .build()
            endPosition = trajSideways.end()
            mecanum.followTrajectory(trajSideways)
        } else if (targetTag == 2) {
            val trajSideways = mecanum.trajectoryBuilder(startPosition)
                .strafeLeft(27.0)
                .build()
            endPosition = trajSideways.end()
            mecanum.followTrajectory(trajSideways)
        }

        val trajForward = mecanum.trajectoryBuilder(endPosition)
            .back(35.0)
            .build()

        telemetry.addLine("Moving forward")
        telemetry.update()

        mecanum.followTrajectory(trajForward)

        webcam.closeCameraDevice()
    }


}