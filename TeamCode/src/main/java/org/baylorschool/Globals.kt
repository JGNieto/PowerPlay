package org.baylorschool

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.util.LiftPositionAngle
import org.baylorschool.util.angledevice.BasicMotorAngleConfig
import kotlin.math.PI

@Config
object Globals {
    // Motors
    const val rightFront = "rightFront"
    const val rightRear = "rightRear"
    const val leftFront = "leftFront"
    const val leftRear = "leftRear"

    // Dead wheel encoder
    const val leftEncoder = leftRear
    const val rightEncoder = rightFront
    const val frontEncoder = rightRear // In our case, this encoder is at the back, not the front lmao.

    // Claw
    const val clawGrab = "clawGrab"
    const val clawGrabOpen = 0.85
    const val clawGrabClosed = - clawGrabOpen

    const val clawPitch = "clawPitch"
    const val clawPitchMax = PI / 4.0 // True angle of claw when position is -1.0
    const val clawPitchMin = 5.0 * PI / 4.0 // True angle of claw when position is 1.0
    val clawPitchDirection = Servo.Direction.REVERSE

    // Webcams
    const val webcamFront = "Webcam Front" // Currently disconnected.
    const val webcamRear = "Webcam Rear"

    const val webcamFrontRotate = false
    const val webcamRearRotate = true

    // I2C
    const val distanceSensor = "distanceSensor"

    const val optimalReleaseDistance = 3.0
    const val optimalReleaseDistanceTolerance = 0.5
    const val seeingPoleThreshold = 15.0 // If distance is higher than this, assume we are not pointing at the pole.

    const val junctionYPosition = 210 // Lower values mean too far to the left.
    const val junctionYPositionTolerance = 50
    const val junctionWidthMinimum = 500

    // Lift presets
    val startingClawAngle = 0.071

    //val liftDropHigh = LiftPositionAngle(1.86, 0.933, 0.071)
    val liftDropHigh = LiftPositionAngle(1.86, 1.044, 0.071)
    val liftGrabTeleOp = LiftPositionAngle(-0.007, -1.042, 0.892)

    // Positioning measurements
    const val driveTrainWidth = 16.75 // Includes the widths of the mecanum wheels.
    const val frontPlaneDistance = 9.08375 // Distance between front plane (including arm) and center of rotation. For initialization.

    const val tileWidth = 24.0

    val rightStartPosition = Pose2d(tileWidth + driveTrainWidth / 2.0 + 0.4, - 3 * tileWidth + frontPlaneDistance, Math.toRadians(270.0))
    val leftStartPosition = Pose2d(-(tileWidth + driveTrainWidth / 2.0 + 0.4), - 3 * tileWidth + frontPlaneDistance, Math.toRadians(270.0))

    const val clawYaw = "clawYaw"

    val rightRearDirection = DcMotorSimple.Direction.FORWARD
    val rightFrontDirection = DcMotorSimple.Direction.FORWARD

    val leftRearDirection = DcMotorSimple.Direction.REVERSE
    val leftFrontDirection = DcMotorSimple.Direction.REVERSE

    // Lift
    const val liftProximalA = "lfProxA"
    const val liftProximalATicksPerRotation = 2698.8
    val liftProximalADirection = DcMotorSimple.Direction.FORWARD
    val liftProximalConfig = BasicMotorAngleConfig(0.0, 0.3, 0.8, 0.45)

    const val liftProximalB = "lfProxB"

    const val liftDistal = "lfDist"
    const val liftDistalTicksPerRotation = 3243.75
    val liftDistalDirection = DcMotorSimple.Direction.FORWARD
    val liftDistalConfig = BasicMotorAngleConfig(0.0, 0.3, 0.5, 0.5)

    // const val liftProximalStartAngle = - 317 * 2 * PI / 3373.5
    // const val liftDistalStartAngle = - 321 * 2 * PI / 537.7
    const val liftProximalStartAngle = - 255 * 2 * PI / 2698.8
    const val liftDistalStartAngle = 1169 * 2 * PI / 3243.75

    const val highProximalAngle = 1.7
    const val highDistalAngle = -3.3

    const val groundProximalAngle = -.1
    const val groundDistalAngle = -1.3

    // Webcam
    const val screenHeight = 480
    const val screenWidth = 640
}