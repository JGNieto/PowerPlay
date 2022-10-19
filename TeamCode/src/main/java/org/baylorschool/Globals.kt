package org.baylorschool

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.baylorschool.util.angledevice.BasicMotorAngleConfig

@Config
object Globals {
    // Motors
    const val rearLeft = "rearLeft"
    const val rearRight = "rearRight"
    const val frontLeft = "frontLeft"
    const val frontRight = "frontRight"

    // Lift
    const val liftProximalA = "lfPrxA"
    const val liftProximalATicksPerRotation = 2772.0
    val liftProximalADirection = DcMotorSimple.Direction.REVERSE
    val liftProximalConfig = BasicMotorAngleConfig(0.0, 0.3, 1.0)

    const val liftProximalB = "lfProxB"

    const val liftDistal = "lfDist"
    const val liftDistalTicksPerRotation = 537.6
    val liftDistalDirection = DcMotorSimple.Direction.REVERSE
    val liftDistalConfig = BasicMotorAngleConfig(0.0, 0.4, 1.0)

    const val highProximalAngle = 1.7
    const val highDistalAngle = -3.3

    const val groundProximalAngle = -.1
    const val groundDistalAngle = -1.3

    // Webcam
    const val screenHeight = 480
    const val screenWidth = 640
}