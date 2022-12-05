package org.baylorschool.betabot

import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import kotlin.math.abs
import kotlin.math.max

class Mecanum(hardwareMap: HardwareMap) {
    private val i: BNO055IMU
    private val flMotor: DcMotorEx
    private val frMotor: DcMotorEx
    private val blMotor: DcMotorEx
    private val brMotor: DcMotorEx
    private val tipTolerance = Math.toRadians(10.0)
    private val tipAuthority = 0.9
    private var x = 0f
    private var y = 0f
    private var slowmodeToggle = false
    private var slowmode = 1.0

    init {
        i = hardwareMap.get(BNO055IMU::class.java, "imu")
        flMotor = hardwareMap.get(DcMotorEx::class.java, "flMotor")
        blMotor = hardwareMap.get(DcMotorEx::class.java, "blMotor")
        frMotor = hardwareMap.get(DcMotorEx::class.java, "frMotor")
        brMotor = hardwareMap.get(DcMotorEx::class.java, "brMotor")
        frMotor.direction = DcMotorSimple.Direction.REVERSE
        brMotor.direction = DcMotorSimple.Direction.REVERSE
        PhotonCore.enable()
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Front Left Power", flMotor.power)
        telemetry.addData("Front Right Power", frMotor.power)
        telemetry.addData("Back Left Power", blMotor.power)
        telemetry.addData("Back Right Power", brMotor.power)
        telemetry.update()
    }

    fun mecanumLoop(gamepad1: Gamepad){
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
