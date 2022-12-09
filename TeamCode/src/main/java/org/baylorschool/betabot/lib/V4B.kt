package org.baylorschool.betabot.lib

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry

class V4B(hardwareMap: HardwareMap) {
    private val v4bServo: Servo

    init {
        v4bServo = hardwareMap.get(Servo::class.java, "v4bServo")
        v4bServo.scaleRange(0.0, 1.0)
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Servo Position", v4bServo.position)
        telemetry.update()
    }

    fun v4bLoop(gamepad1: Gamepad) {
        if (gamepad1.y)
            v4bServo.position += 0.001
        else if (gamepad1.a)
            v4bServo.position -= 0.001
    }
}