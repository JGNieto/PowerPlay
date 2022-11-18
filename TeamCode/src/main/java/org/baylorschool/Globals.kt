package org.baylorschool

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.baylorschool.util.angledevice.BasicMotorAngleConfig
import kotlin.math.PI

@Config
object Globals {
    // Motors
    const val rightFront = "rightFront"
    const val rightRear = "rightRear"
    const val leftFront = "leftFront"
    const val leftRear = "leftRear"

    // Claw
    const val clawGrab = "clawGrab"
    const val clawGrabOpen = -1.0
    const val clawGrabClosed = 1.0

    const val clawPitch = "clawPitch"
    const val clawPitchMax = PI / 4.0 // True angle of claw when position is -1.0
    const val clawPitchMin = 5.0 * PI / 4.0 // True angle of claw when position is 1.0

    const val clawYaw = "clawYaw"

    val rightRearDirection = DcMotorSimple.Direction.FORWARD
    val rightFrontDirection = DcMotorSimple.Direction.FORWARD

    val leftRearDirection = DcMotorSimple.Direction.REVERSE
    val leftFrontDirection = DcMotorSimple.Direction.REVERSE

    // Lift
    const val liftProximalA = "lfProxA"
    const val liftProximalATicksPerRotation = 3373.5
    val liftProximalADirection = DcMotorSimple.Direction.REVERSE
    val liftProximalConfig = BasicMotorAngleConfig(0.0, 0.3, 0.6)

    const val liftProximalB = "lfProxB"

    const val liftDistal = "lfDist"
    const val liftDistalTicksPerRotation = 537.7
    val liftDistalDirection = DcMotorSimple.Direction.REVERSE
    val liftDistalConfig = BasicMotorAngleConfig(0.0, 0.3, 0.3)

    const val liftProximalStartAngle = - 317 * 2 * PI / 3373.5
    const val liftDistalStartAngle = - 321 * 2 * PI / 537.7

    const val highProximalAngle = 1.7
    const val highDistalAngle = -3.3

    const val groundProximalAngle = -.1
    const val groundDistalAngle = -1.3

    // Webcam
    const val screenHeight = 480
    const val screenWidth = 640
}