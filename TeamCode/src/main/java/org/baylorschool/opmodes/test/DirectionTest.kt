package org.baylorschool.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.baylorschool.Globals

@TeleOp(name = "Direction Test", group = "test")
class DirectionTest: LinearOpMode() {

    override fun runOpMode() {
        val leftRear = hardwareMap.get(DcMotorEx::class.java, Globals.leftRear)
        val leftFront = hardwareMap.get(DcMotorEx::class.java, Globals.leftFront)
        val rightRear = hardwareMap.get(DcMotorEx::class.java, Globals.rightRear)
        val rightFront = hardwareMap.get(DcMotorEx::class.java, Globals.rightFront)

        leftRear.direction = DcMotorSimple.Direction.FORWARD
        leftFront.direction = DcMotorSimple.Direction.FORWARD
        rightRear.direction = DcMotorSimple.Direction.FORWARD
        rightFront.direction = DcMotorSimple.Direction.FORWARD

        waitForStart()

        leftRear.power = 0.1
        leftFront.power = 0.1
        rightRear.power = 0.1
        rightFront.power = 0.1

        while (opModeIsActive()) {}

        leftRear.power = 0.0
        leftFront.power = 0.0
        rightRear.power = 0.0
        rightFront.power = 0.0
    }
}
