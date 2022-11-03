package org.baylorschool.betabot

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.abs

@TeleOp (name = "Slide Test",group = "Beta Bot")
class SlideTest: LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {

        val slideMotor1 = hardwareMap.get(DcMotorEx::class.java, "Rlift")
        val slideMotor2 = hardwareMap.get(DcMotorEx::class.java, "Llift")
        var slidePower: Double
        var targetPosition = 0.0
        val targetPositionDifference = 3.0
        var avgPosition = 0.0

        slideMotor1.direction = DcMotorSimple.Direction.REVERSE
        slideMotor2.direction = DcMotorSimple.Direction.REVERSE
        slideMotor1.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        slideMotor2.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        telemetry.addData("Status", "Ready to start")
        telemetry.update()

        waitForStart()

        while (opModeIsActive()){
            slidePower = if (abs(avgPosition - targetPosition) > targetPositionDifference)
                if (avgPosition > targetPosition)
                    .8
                else
                    -.6
            else
                .2

            if (gamepad1.dpad_up)
                targetPosition += 5
            if (gamepad1.dpad_down)
                targetPosition -= 5

            slideMotor1.power = slidePower
            slideMotor2.power = slidePower

            avgPosition = ((slideMotor1.currentPosition.toDouble() + slideMotor2.currentPosition.toDouble()) / 2)

            telemetry.addData("Slide Motors Position", avgPosition)
            telemetry.update()
        }
    }
}