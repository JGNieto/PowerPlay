package org.baylorschool.util

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.Globals

class Claw(opMode: OpMode) {

    private val grabServo = opMode.hardwareMap.get(Servo::class.java, Globals.clawGrab)

    fun open() {
        grabServo.position = Globals.clawGrabOpen
    }

    fun close() {
        grabServo.position = Globals.clawGrabClosed
    }

    /**
     * Go to relative openness position.
     * @param position 0.0 is open and 1.0 is closed
     */
    fun grabPosition(position: Double) {
        grabServo.position = map(0.0, 1.0, Globals.clawGrabOpen, Globals.clawGrabClosed, position)
    }

    private fun map(inputMin: Double, inputMax: Double, outputMin: Double, outputMax: Double, value: Double): Double {
        return (value-inputMin)/(inputMax-inputMin) * (outputMax-outputMin) + outputMin
    }
}