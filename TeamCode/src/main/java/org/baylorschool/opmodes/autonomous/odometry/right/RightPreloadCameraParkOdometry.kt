package org.baylorschool.opmodes.autonomous.odometry.right

//import com.outoftheboxrobotics.photoncore.PhotonCore
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.hardware.rev.Rev2mDistanceSensor
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.Globals
import org.baylorschool.drive.AdjustJunctionWebcam
import org.baylorschool.drive.DriveConstants
import org.baylorschool.drive.Mecanum
import org.baylorschool.opmodes.teleop.MainTeleOp
import org.baylorschool.util.Claw
import org.baylorschool.util.OhmMotor
import org.baylorschool.util.angledevice.BasicMotorAngleDevice
import org.baylorschool.vision.AprilTagBinaryPipeline
import org.baylorschool.vision.CameraUtil
import org.baylorschool.vision.YellowJunctionPipeline
import kotlin.math.PI

@Autonomous(name = "RightPreloadCameraParkOdometry", group = "AA Odometry Right")
class RightPreloadCameraParkOdometry: LinearOpMode() {

    // POSITIONS ARE DESIGNED FOR RIGHT RED, BUT WORK FOR RIGHT BLUE AS WELL
    private val startPosition = Globals.rightStartPosition

    private val dropPosition = Pose2d(Globals.tileWidth * 0.9, -Globals.tileWidth / 1.98, Math.toRadians(270.0))

    override fun runOpMode() {
        //PhotonCore.enable()

        telemetry.addData("Status", "Getting ready. Please wait...")
        telemetry.update()

        val motorA1 = BasicMotorAngleDevice(this, Globals.liftProximalA, Globals.liftProximalATicksPerRotation, Globals.liftProximalConfig, Globals.liftProximalADirection)
        val motorB = BasicMotorAngleDevice(this, Globals.liftDistal, Globals.liftDistalTicksPerRotation, Globals.liftDistalConfig, Globals.liftDistalDirection)
        val claw = Claw(this)
        claw.close()

        val clawPitch = hardwareMap.get(Servo::class.java, Globals.clawPitch)
        clawPitch.direction = Globals.clawPitchDirection
        clawPitch.position = Globals.startingClawAngle

        val aprilTagPipeline = AprilTagBinaryPipeline(telemetry, Globals.webcamRearRotate)
        val junctionPipeline = YellowJunctionPipeline(Globals.webcamRearRotate, null)
        val webcam = CameraUtil.openWebcam(this, aprilTagPipeline, true, Globals.webcamRear)
        val mecanum = Mecanum(hardwareMap)
        val distance = hardwareMap.get(Rev2mDistanceSensor::class.java, Globals.distanceSensor)

        telemetry.addData("Status", "I'm looking for tags...")
        telemetry.update()

        waitForStart()

        val targetTag = aprilTagPipeline.determineTargetTag()

        webcam.setPipeline(junctionPipeline)

        telemetry.addLine("Tag $targetTag")
        telemetry.update()


        motorA1.init()
        motorA1.reset(Globals.liftProximalStartAngle)
        motorA1.debug = false

        motorB.init()
        motorB.reset(Globals.liftDistalStartAngle)
        motorB.debug = false

        mecanum.poseEstimate = startPosition

        telemetry.addLine("Moving toward position.")
        telemetry.update()

        val trajToDeliver = mecanum.trajectorySequenceBuilder(startPosition)
            .back(2.0)
            .lineToLinearHeading(Pose2d(Globals.tileWidth / 2.0, startPosition.y + 2.0, startPosition.heading))
            .lineToLinearHeading(Pose2d(Globals.tileWidth / 2.0, -Globals.tileWidth / 2.0, startPosition.heading))
            .lineToLinearHeading(dropPosition)
            .build()

        mecanum.followTrajectorySequence(trajToDeliver)

        println("ROBOT POSITION FIRST: ${mecanum.poseEstimate.x}, ${mecanum.poseEstimate.y}, ${mecanum.poseEstimate.heading}º")

        motorA1.moveToAngle(Globals.liftDropHigh.proximal)
        motorB.moveToAngle(Globals.liftDropHigh.distal)
        clawPitch.position = Globals.liftDropHigh.claw

        mecanum.sleep(1500, this)

        val trajAdjust = mecanum.trajectoryBuilder(mecanum.poseEstimate)
            .lineToLinearHeading(dropPosition)
            .build()

        mecanum.followTrajectory(trajAdjust)

        println("ROBOT POSITION UP: ${mecanum.poseEstimate.x}, ${mecanum.poseEstimate.y}, ${mecanum.poseEstimate.heading}º")

        AdjustJunctionWebcam.adjustJunctionWebcam(this, distance, junctionPipeline, mecanum, AdjustJunctionWebcam.Side.RIGHT)

        mecanum.sleep(2000, this)

        claw.open()

        mecanum.sleep(500, this)

        mecanum.updatePoseEstimate()
        claw.close()

        val clearSpaceTraj = mecanum.trajectoryBuilder(mecanum.poseEstimate)
            .forward(3.0)
            .build()

        mecanum.followTrajectory(clearSpaceTraj)

        motorA1.moveToAngle(Globals.liftProximalStartAngle)
        motorA1.motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        clawPitch.position = Globals.liftDropHigh.claw

        sleep(200)

        motorB.moveToAngle(Globals.liftDistalStartAngle)
        motorB.motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        println("ROBOT POSITION DROP: ${mecanum.poseEstimate.x}, ${mecanum.poseEstimate.y}, ${mecanum.poseEstimate.heading}º")

        val retractStartTime = System.currentTimeMillis()

        while ((motorA1.getPosition() - motorA1.targetAngle > Math.toRadians(1.0) || motorB.getPosition() - motorB.targetAngle > Math.toRadians(1.0)) && System.currentTimeMillis() - retractStartTime < 2000) {
            mecanum.updatePoseEstimate()
        }

        val parkingTraj =
            if (targetTag == 1) {
                mecanum.trajectorySequenceBuilder(mecanum.poseEstimate)
                    .lineToConstantHeading(Vector2d(Globals.tileWidth * 1.5, - Globals.tileWidth * 0.5))
                    .forward(9.0)
            } else if (targetTag == 0) {
                mecanum.trajectorySequenceBuilder(mecanum.poseEstimate)
                    .lineToConstantHeading(Vector2d(Globals.tileWidth * 0.5, - Globals.tileWidth * 0.5))
                    .forward(9.0)
            } else {
                mecanum.trajectorySequenceBuilder(mecanum.poseEstimate)
                    .lineToConstantHeading(
                        Vector2d(Globals.tileWidth * 2.5, - Globals.tileWidth * 0.5),
                        Mecanum.getVelocityConstraint(30.0, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        Mecanum.getAccelerationConstraint(DriveConstants.MAX_ACCEL)
                    )
                    //.forward(9.0)
            }

        mecanum.followTrajectorySequence(
            parkingTraj.
                addTemporalMarker(1.0) {
                    motorA1.cleanup()
                    motorB.cleanup()
                }
                .addTemporalMarker(1.1) {
                    motorB.motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
                    motorB.motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
                    motorB.motor.power = 0.0

                    motorA1.motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
                    motorA1.motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
                    motorA1.motor.power = 0.0
                }
                .addTemporalMarker(2.0) {
                    MainTeleOp.proximalAngle = motorA1.getPosition()
                    MainTeleOp.distalAngle = motorB.getPosition()
                }
                .build()
        )

        motorA1.cleanup()
        motorB.cleanup()
        webcam.closeCameraDevice()
    }


}