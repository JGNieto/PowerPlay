package org.baylorschool.util

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.Globals

class Claw(opMode: OpMode) {

    private val servo = opMode.hardwareMap.get(Servo::class.java, Globals.claw)

    fun open() {
        servo.position = 1.0
    }

    fun close() {
        servo.position = -1.0
    }

    fun goTo(position: Double) {
        servo.position = position
    }
}