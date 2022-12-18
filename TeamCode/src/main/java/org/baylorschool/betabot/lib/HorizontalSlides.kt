package org.baylorschool.betabot.lib

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry

class HorizontalSlides(hardwareMap: HardwareMap) {
    private val linkageServo: Servo

    init {
        linkageServo = hardwareMap.get(Servo::class.java, "linkageServo")
        linkageServo.scaleRange(0.0, 0.45)
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Servo Position", linkageServo.position)
    }

    fun horizSlideLoop(gamepad1: Gamepad) {
        if (gamepad1.dpad_left)
            linkageServo.position += 0.0025
        else if (gamepad1.dpad_right)
            linkageServo.position -= 0.0025
    }
}