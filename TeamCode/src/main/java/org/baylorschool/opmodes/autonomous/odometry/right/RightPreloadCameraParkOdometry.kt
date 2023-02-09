package org.baylorschool.opmodes.autonomous.odometry.right

import com.acmerobotics.roadrunner.geometry.Pose2d
//import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.Globals
import org.baylorschool.drive.Mecanum
import org.baylorschool.util.Claw
import org.baylorschool.util.angledevice.BasicMotorAngleDevice
import org.baylorschool.vision.AprilTagBinaryPipeline
import org.baylorschool.vision.CameraUtil
import kotlin.math.PI

@Autonomous(name = "RightPreloadCameraParkOdometry", group = "AA Odometry Right")
class RightPreloadCameraParkOdometry: LinearOpMode() {

    // POSITIONS ARE DESIGNED FOR RIGHT RED, BUT WORK FOR RIGHT BLUE AS WELL
    private val startPosition = Globals.rightStartPosition

    private val dropPosition = Pose2d(16.5786, -7.31907, 3.9475779608)

    override fun runOpMode() {
        //PhotonCore.enable()

        telemetry.addData("Status", "Getting ready. Please wait...")
        telemetry.update()

        val motorA1 = hardwareMap.get(DcMotorEx::class.java, Globals.liftProximalA)
        val motorB = BasicMotorAngleDevice(this, Globals.liftDistal, Globals.liftDistalTicksPerRotation, Globals.liftDistalConfig, Globals.liftDistalDirection)
        val claw = Claw(this)
        claw.close()

        val clawPitch = hardwareMap.get(Servo::class.java, Globals.clawPitch)
        clawPitch.direction = Globals.clawPitchDirection
        clawPitch.position = 0.0

        val pipeline = AprilTagBinaryPipeline(telemetry, Globals.webcamRearRotate)
        val webcam = CameraUtil.openWebcam(this, pipeline, true, Globals.webcamRear)
        val mecanum = Mecanum(hardwareMap)

        telemetry.addData("Status", "I'm looking for tags...")
        telemetry.update()

        waitForStart()

        val targetTag = pipeline.determineTargetTag()

        webcam.closeCameraDeviceAsync {  }

        telemetry.addLine("Tag $targetTag")
        telemetry.update()

        motorA1.direction = Globals.liftProximalADirection
        motorA1.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorA1.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motorA1.mode = DcMotor.RunMode.RUN_USING_ENCODER

        motorB.init()
        motorB.reset(Globals.liftDistalStartAngle)
        motorB.debug = false

        mecanum.poseEstimate = startPosition

        telemetry.addLine("Moving sideways")
        telemetry.update()

        val trajSideways = mecanum.trajectoryBuilder(startPosition)
            .strafeRight(23.25)
            .build()
        mecanum.followTrajectory(trajSideways)

        val trajForward = mecanum.trajectoryBuilder(mecanum.poseEstimate)
            .back(42.0)
            .build()
        mecanum.followTrajectory(trajForward)

        motorA1.targetPosition =
            ((Globals.liftDropHigh.proximal - Globals.liftProximalStartAngle) * Globals.liftProximalATicksPerRotation / (2 * PI)).toInt()
        motorA1.mode = DcMotor.RunMode.RUN_TO_POSITION
        motorA1.power = 0.5

        motorB.moveToAngle(Globals.liftDropHigh.distal)
        clawPitch.position = Globals.liftDropHigh.claw

        sleep(2000)

        val trajSpline = mecanum.trajectoryBuilder(mecanum.poseEstimate)
            .strafeTo(dropPosition.vec())
            .build()
        mecanum.followTrajectory(trajSpline)

        mecanum.turn(dropPosition.heading - trajSpline.end().heading)

        motorB.cleanup()
        webcam.closeCameraDevice()
    }


}