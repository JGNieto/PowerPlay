package org.baylorschool.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.baylorschool.Globals

@TeleOp(name = "Wheel Encoders Test", group = "test")
class WheelEncodersTest: LinearOpMode() {

    override fun runOpMode() {
        val leftRear = hardwareMap.get(DcMotorEx::class.java, Globals.leftRear)
        val leftFront = hardwareMap.get(DcMotorEx::class.java, Globals.leftFront)
        val rightRear = hardwareMap.get(DcMotorEx::class.java, Globals.rightRear)
        val rightFront = hardwareMap.get(DcMotorEx::class.java, Globals.rightFront)

        leftRear.direction = DcMotorSimple.Direction.FORWARD
        leftFront.direction = DcMotorSimple.Direction.FORWARD
        rightRear.direction = DcMotorSimple.Direction.FORWARD
        rightFront.direction = DcMotorSimple.Direction.FORWARD

        leftRear.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        leftFront.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        rightRear.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        rightFront.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        waitForStart()

        while (opModeIsActive()) {
            telemetry.addData("Left rear", leftRear.currentPosition)
            telemetry.addData("Left front", leftFront.currentPosition);
            telemetry.addData("Right rear", rightRear.currentPosition);
            telemetry.addData("Right front", rightFront.currentPosition);
            telemetry.update()
        }
    }
}
