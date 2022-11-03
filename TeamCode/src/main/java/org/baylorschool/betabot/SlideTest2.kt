package org.baylorschool.betabot

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple

@TeleOp(name = "Slide Test",group = "Beta Bot")
class SlideTest2: LinearOpMode() {

    @Throws(InterruptedException::class)
    override fun runOpMode() {

        val slideMotor1 = hardwareMap.get(DcMotorEx::class.java, "Rlift")
        val slideMotor2 = hardwareMap.get(DcMotorEx::class.java, "Llift")
        var slidePower: Double
        var avgPosition: Double

        slideMotor1.direction = DcMotorSimple.Direction.REVERSE
        slideMotor2.direction = DcMotorSimple.Direction.REVERSE
        slideMotor1.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        slideMotor2.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        waitForStart()

        while (opModeIsActive()) {

            slidePower = if (gamepad1.dpad_up)
                .5
            else if (gamepad1.dpad_down)
                -.1
            else
                0.0

            avgPosition =
                ((slideMotor1.currentPosition.toDouble() + slideMotor2.currentPosition.toDouble()) / 2)

            slideMotor1.power = slidePower
            slideMotor2.power = slidePower



            telemetry.addData("Slide Motors Position", avgPosition)
            telemetry.update()
        }
    }
}