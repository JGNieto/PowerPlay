package org.baylorschool.betabot

import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.cos
import kotlin.math.sin

@TeleOp
class Mecanum2 : LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {

        val flMotor = hardwareMap.dcMotor["flMotor"]
        val blMotor = hardwareMap.dcMotor["blMotor"]
        val frMotor = hardwareMap.dcMotor["frMotor"]
        val brMotor = hardwareMap.dcMotor["brMotor"]

        frMotor.direction = DcMotorSimple.Direction.REVERSE
        brMotor.direction = DcMotorSimple.Direction.REVERSE
        PhotonCore.enable()

        var slowmodeToggle = false
        var slowmode = 1.0

        waitForStart()

        while(opModeIsActive()) {
            val y = -gamepad1.left_stick_y.toDouble()
            val x = gamepad1.left_stick_x * 1.1
            val rx = -gamepad1.right_stick_x.toDouble()

            val denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0)
            val frontLeftPower = (((y + x + rx) / denominator) * slowmode)
            val backLeftPower = (((y - x + rx) / denominator) * slowmode)
            val frontRightPower =(((y - x - rx) / denominator) * slowmode)
            val backRightPower = (((y + x - rx) / denominator) * slowmode)

            if (gamepad1.y)
                slowmodeToggle = false
            else if (gamepad1.a)
                slowmodeToggle = true

            if (slowmodeToggle)
                slowmode = 0.4
            else
                slowmode = 1.0

            flMotor.power = frontLeftPower
            blMotor.power = backLeftPower
            frMotor.power = frontRightPower
            brMotor.power = backRightPower

        }
    }
}