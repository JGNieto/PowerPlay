package org.baylorschool.opmodes.autonomous.odometry.right

import android.provider.Settings.Global
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.hardware.rev.Rev2mDistanceSensor
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
import org.baylorschool.vision.YellowJunctionPipeline
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import kotlin.math.PI

@Autonomous(name = "RightPreloadCameraParkOdometry", group = "AA Odometry Right")
class RightPreloadCameraParkOdometry: LinearOpMode() {

    // POSITIONS ARE DESIGNED FOR RIGHT RED, BUT WORK FOR RIGHT BLUE AS WELL
    private val startPosition = Globals.rightStartPosition

    private val dropPosition = Pose2d(Globals.tileWidth, -Globals.tileWidth / 2.0, Math.toRadians(270.0))

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
        clawPitch.position = 0.071

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

        motorA1.direction = Globals.liftProximalADirection
        motorA1.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorA1.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motorA1.mode = DcMotor.RunMode.RUN_USING_ENCODER

        motorB.init()
        motorB.reset(Globals.liftDistalStartAngle)
        motorB.debug = false

        mecanum.poseEstimate = startPosition

        telemetry.addLine("Moving toward position.")
        telemetry.update()

        val trajToDeliver = mecanum.trajectorySequenceBuilder(startPosition)
            .back(2.0)
            .lineToConstantHeading(Vector2d(Globals.tileWidth / 2.0, startPosition.y + 2.0))
            .lineToConstantHeading(Vector2d(Globals.tileWidth / 2.0, -Globals.tileWidth / 2.0))
            .lineToLinearHeading(dropPosition)
            .build()

        mecanum.followTrajectorySequence(trajToDeliver)

        motorA1.targetPosition =
            ((Globals.liftDropHigh.proximal - Globals.liftProximalStartAngle) * Globals.liftProximalATicksPerRotation / (2 * PI)).toInt()
        motorA1.mode = DcMotor.RunMode.RUN_TO_POSITION
        motorA1.power = 0.5

        motorB.moveToAngle(Globals.liftDropHigh.distal)
        clawPitch.position = Globals.liftDropHigh.claw

        var correctionStartTime = System.currentTimeMillis()

        var speed = 0.2
        var direction = 0

        while (System.currentTimeMillis() - correctionStartTime < 3000 && opModeIsActive()) {
            val rect = junctionPipeline.junctionRect
            if (rect == null) break

            if (distance.getDistance(DistanceUnit.INCH) < Globals.seeingPoleThreshold && System.currentTimeMillis() - correctionStartTime > 1500) break

            if (rect.width < Globals.junctionWidthMinimum || rect.y < Globals.junctionYPosition - Globals.junctionYPositionTolerance) {
                if (direction == -1) speed *= 0.7
                direction = 1

                mecanum.setWeightedDrivePower(Pose2d(0.0, -speed, 0.0))

                telemetry.addData("Direction", "Right")
            } else if (rect.y > Globals.junctionYPosition + Globals.junctionYPositionTolerance) {
                if (direction == 1) speed *= 0.7
                direction = -1

                mecanum.setWeightedDrivePower(Pose2d(0.0, speed, 0.0))

                telemetry.addData("Direction", "Left")
            } else {
                break
            }

            telemetry.addData("Rect Y", rect.y)
            telemetry.addData("Speed", speed)
            telemetry.update()
        }

        webcam.closeCameraDeviceAsync {  }

        speed = 0.2
        direction = 0
        correctionStartTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - correctionStartTime < 3000 && opModeIsActive()) {
            val dist = distance.getDistance(DistanceUnit.INCH)

            if (dist > Globals.optimalReleaseDistance + Globals.optimalReleaseDistanceTolerance) {
                if (direction == -1) speed *= 0.7
                direction = 1
            } else if (dist < Globals.optimalReleaseDistance - Globals.optimalReleaseDistanceTolerance) {
                if (direction == 1) speed *= 0.7
                direction = -1
            } else {
                break
            }

            mecanum.setWeightedDrivePower(Pose2d(speed * direction * -1, 0.0, 0.0))
        }

        mecanum.setDrivePower(Pose2d(0.0, 0.0, 0.0))

        sleep(2000)

        claw.open()

        motorB.cleanup()
        webcam.closeCameraDevice()
    }


}