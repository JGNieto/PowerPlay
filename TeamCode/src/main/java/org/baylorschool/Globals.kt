package org.baylorschool

import com.acmerobotics.dashboard.config.Config

@Config
object Globals {
    // Motors
    const val rearLeft = "rearLeft"
    const val rearRight = "rearRight"
    const val frontLeft = "frontLeft"
    const val frontRight = "frontRight"

    // Lift
    const val liftProximalA = "lfProxA"
    const val liftProximalATicksPerRotation = 537.7

    const val liftProximalB = "lfProxB"
    const val liftDistal = "lfDist"

    const val highProximalAngle = 1.7
    const val highDistalAngle = -3.3

    const val groundProximalAngle = -.1
    const val groundDistalAngle = -1.3

    // Webcam
    const val screenHeight = 480
    const val screenWidth = 640
}