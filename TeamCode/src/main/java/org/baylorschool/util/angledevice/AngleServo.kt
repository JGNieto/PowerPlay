package org.baylorschool.util.angledevice

import com.qualcomm.robotcore.hardware.Servo

class AngleServo(private val servo: Servo, private val minAngle: Double, private val maxAngle: Double): AngleDevice {

    override fun moveToAngle(angle: Double, direction: TargetAngleDirection) {
        servo.position = clamp(map(minAngle, maxAngle, -1.0, 1.0, angle), -1.0, 1.0)
    }

    override var debug: Boolean = false

    private fun map(inputMin: Double, inputMax: Double, outputMin: Double, outputMax: Double, value: Double): Double {
        return (value-inputMin)/(inputMax-inputMin) * (outputMax-outputMin) + outputMin
    }

    private fun clamp(value: Double, low: Double, high: Double): Double {
        return if (value < low) low
        else if (value > high) high
        else value
    }

    override fun reset(angle: Double) {
        throw NotImplementedError("Reset not available for servo.")
    }

    override fun getPosition(): Double {
        return servo.position
    }

    override fun init() {

    }

    override fun stop() {

    }

    override fun cleanup() {

    }
}