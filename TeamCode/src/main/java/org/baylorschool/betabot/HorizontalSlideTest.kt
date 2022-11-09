package org.baylorschool.betabot

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo

@TeleOp(name = "Horizontal Slide Test", group = "Beta Bot")
class HorizontalSlideTest: LinearOpMode() {

    override fun runOpMode() {
        val linkageServo = hardwareMap.get(Servo::class.java, "linkageServo")

        linkageServo.scaleRange(0.0, 1.0)

        telemetry.addData("Status", "Ready to start")
        telemetry.update()

        waitForStart()

        while (opModeIsActive() && !isStopRequested) {

            if (gamepad1.dpad_right)
                linkageServo.position += 0.0001
            else if (gamepad1.dpad_left)
                linkageServo.position -= 0.0001


            telemetry.addData("Servo Position", linkageServo.position)
            telemetry.update()
        }

    }
}