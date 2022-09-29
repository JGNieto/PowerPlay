package org.baylorschool.candypult

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo

@TeleOp(name = "Candypult", group = "candypult")
class Candypult: LinearOpMode() {

    override fun runOpMode() {
        val rearLeft = hardwareMap.get(DcMotor::class.java, "rearLeft")
        val rearRight = hardwareMap.get(DcMotor::class.java, "rearRight")
        val frontLeft = hardwareMap.get(DcMotor::class.java, "frontLeft")
        val frontRight = hardwareMap.get(DcMotor::class.java, "frontRight")

        val trigger = hardwareMap.get(Servo::class.java, "trigger")
        val reload = hardwareMap.get(DcMotor::class.java, "reload")

        rearLeft.direction = DcMotorSimple.Direction.REVERSE
        frontLeft.direction = DcMotorSimple.Direction.REVERSE

        reload.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        waitForStart()

        while (opModeIsActive() && !isStopRequested) {
            frontLeft.power = gamepad1.left_stick_y.toDouble()
            rearLeft.power = gamepad1.left_stick_y.toDouble()
            frontRight.power = gamepad1.right_stick_y.toDouble()
            rearRight.power = gamepad1.right_stick_y.toDouble()

            reload.power = (gamepad1.right_trigger - gamepad1.left_trigger).toDouble()

            if (gamepad1.left_bumper && gamepad1.right_bumper) {
                trigger.position = 0.0
            } else {
                trigger.position = 0.7
            }
        }

    }
}