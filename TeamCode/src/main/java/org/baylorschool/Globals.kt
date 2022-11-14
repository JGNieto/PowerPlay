package org.baylorschool

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.baylorschool.util.angledevice.BasicMotorAngleConfig
import kotlin.math.PI

@Config
object Globals {
    // Motors
    const val rearLeft = "rearLeft"
    const val rearRight = "rearRight"
    const val frontLeft = "frontLeft"
    const val frontRight = "frontRight"

    // Lift
    const val liftProximalA = "lfProxA"
    const val liftProximalATicksPerRotation = 5190.0
    val liftProximalADirection = DcMotorSimple.Direction.REVERSE
    val liftProximalConfig = BasicMotorAngleConfig(0.0, 0.2, 0.2)
    const val liftProximalStartAngle = - 2 * PI * 78 / 672.13

    const val liftProximalB = "lfProxB"

    const val liftDistal = "lfDist"
    const val liftDistalTicksPerRotation = 537.7
    const val liftDistalStartAngle = PI + 2 * PI * 5 / 537.7
    val liftDistalDirection = DcMotorSimple.Direction.FORWARD
    val liftDistalConfig = BasicMotorAngleConfig(0.0, 0.3, 0.3)

    const val highProximalAngle = 1.7
    const val highDistalAngle = -3.3

    const val groundProximalAngle = -.1
    const val groundDistalAngle = -1.3

    // Webcam
    const val screenHeight = 480
    const val screenWidth = 640
}