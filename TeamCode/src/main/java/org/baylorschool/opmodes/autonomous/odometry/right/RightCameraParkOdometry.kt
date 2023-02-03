package org.baylorschool.opmodes.autonomous.odometry.right

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.Globals
import org.baylorschool.drive.Mecanum
import org.baylorschool.vision.AprilTagBinaryPipeline
import org.baylorschool.vision.CameraUtil

@Autonomous(name = "RightParkOdometry", group = "AA Odometry Right")
class RightCameraParkOdometry: LinearOpMode() {

    // POSITIONS ARE DESIGNED FOR RIGHT RED, BUT WORK FOR RIGHT BLUE AS WELL
    private val startPosition = Pose2d(24.0 + 12.5, -24.0 * 2.0 - 9.5, Math.toRadians(270.0))

    override fun runOpMode() {
        PhotonCore.enable()

        telemetry.addData("Status", "Getting ready. Please wait...")
        telemetry.update()

        val pipeline = AprilTagBinaryPipeline(telemetry, Globals.webcamRearRotate)
        val webcam = CameraUtil.openWebcam(this, pipeline, true, Globals.webcamRear)
        val mecanum = Mecanum(hardwareMap)

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
                .strafeRight(23.25)
                .build()
            endPosition = trajSideways.end()
            mecanum.followTrajectory(trajSideways)
        } else if (targetTag == 2) {
            val trajSideways = mecanum.trajectoryBuilder(startPosition)
                .strafeLeft(26.0)
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