package org.baylorschool.util

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import kotlin.math.PI

class OhmMotor(val motor: DcMotorEx, private val ticksPerRotation: Double) {
    var zeroPositionTicks = 0
    var zeroPositionAngle = 0.0

    fun motorToPosition(angle: Double) {
        motor.targetPosition = ((angle - zeroPositionAngle) * ticksPerRotation / (2 * PI)).toInt() + zeroPositionTicks
        motor.mode = DcMotor.RunMode.RUN_TO_POSITION
    }

    fun getAngle(): Double = (motor.currentPosition - zeroPositionTicks) * 2 * PI / ticksPerRotation + zeroPositionAngle
}