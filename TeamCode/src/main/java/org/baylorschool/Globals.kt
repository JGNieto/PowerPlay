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
    const val claw = "claw";

    val rightRearDirection = DcMotorSimple.Direction.FORWARD
    val rightFrontDirection = DcMotorSimple.Direction.FORWARD

    val leftRearDirection = DcMotorSimple.Direction.REVERSE
    val leftFrontDirection = DcMotorSimple.Direction.REVERSE

    // Lift
    const val liftProximalA = "lfProxA"
    const val liftProximalATicksPerRotation = 3373.5
    val liftProximalADirection = DcMotorSimple.Direction.REVERSE
    val liftProximalConfig = BasicMotorAngleConfig(0.0, 0.2, 0.2)

    const val liftProximalB = "lfProxB"

    const val liftDistal = "lfDist"
    const val liftDistalTicksPerRotation = 537.7
    val liftDistalDirection = DcMotorSimple.Direction.REVERSE
    val liftDistalConfig = BasicMotorAngleConfig(0.0, 0.3, 0.3)

    const val liftProximalStartAngle = - 320 * 2 * PI / 3373.5
    const val liftDistalStartAngle = -PI

    const val highProximalAngle = 1.7
    const val highDistalAngle = -3.3

    const val groundProximalAngle = -.1
    const val groundDistalAngle = -1.3

    // Webcam
    const val screenHeight = 480
    const val screenWidth = 640
}