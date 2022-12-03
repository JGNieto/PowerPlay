package org.baylorschool.betabot

import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import kotlin.math.abs
import kotlin.math.max

@TeleOp
class Mecanum : LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {

        val i = hardwareMap.get(BNO055IMU::class.java, "imu")
        val flMotor = hardwareMap.get(DcMotorEx::class.java, "flMotor")
        val blMotor = hardwareMap.get(DcMotorEx::class.java, "blMotor")
        val frMotor = hardwareMap.get(DcMotorEx::class.java, "frMotor")
        val brMotor = hardwareMap.get(DcMotorEx::class.java, "brMotor")
        val tipTolerance = Math.toRadians(10.0)
        val tipAuthority = 0.9
        var x = 0f
        var y = 0f

        frMotor.direction = DcMotorSimple.Direction.REVERSE
        brMotor.direction = DcMotorSimple.Direction.REVERSE
        PhotonCore.enable()

        var slowmodeToggle = false
        var slowmode = 1.0

        waitForStart()

        while(opModeIsActive()) {
            val i: Orientation = i.angularOrientation
            val xOffset = i.secondAngle
            val yOffset = i.thirdAngle
            val adjX: Float = xOffset - i.secondAngle
            val adjY: Float = i.thirdAngle - yOffset
            if (abs(adjY) > tipTolerance)
                y = adjY
            if (abs(adjX) > tipTolerance)
                x = adjX

            val gp1y = -gamepad1.left_stick_y.toDouble() - Range.clip(y* 2.0, -tipAuthority, tipAuthority)
            val gp1x = (gamepad1.left_stick_x * 1.1) +  Range.clip(x* 2.0, -tipAuthority, tipAuthority)
            val rx = -gamepad1.right_stick_x.toDouble()

            val denominator = max(abs(gp1y) + abs(gp1x) + abs(rx), 1.0)
            val frontLeftPower = (((gp1y + gp1x + rx) / denominator) * slowmode)
            val backLeftPower = (((gp1y - gp1x + rx) / denominator) * slowmode)
            val frontRightPower = (((gp1y - gp1x - rx) / denominator) * slowmode)
            val backRightPower = (((gp1y + gp1x - rx) / denominator) * slowmode)

            if (gamepad1.y)
                slowmodeToggle = false
            else if (gamepad1.a)
                slowmodeToggle = true

            slowmode = if (slowmodeToggle)
                0.4
            else
                1.0

            flMotor.power = frontLeftPower
            blMotor.power = backLeftPower
            frMotor.power = frontRightPower
            brMotor.power = backRightPower

        }
    }
}