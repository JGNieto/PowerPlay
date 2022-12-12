package org.baylorschool.util

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.Globals
import org.firstinspires.ftc.robotcore.external.Telemetry

class Mecanum(hardwareMap: HardwareMap) {
    val flMotor: DcMotorEx
    val frMotor: DcMotorEx
    val blMotor: DcMotorEx
    val brMotor: DcMotorEx
    private var slowmodeToggle = false
    private var slowmode = 1.0

    init {
        flMotor = hardwareMap.get(DcMotorEx::class.java, Globals.leftFront)
        blMotor = hardwareMap.get(DcMotorEx::class.java, Globals.leftRear)
        frMotor = hardwareMap.get(DcMotorEx::class.java, Globals.rightFront)
        brMotor = hardwareMap.get(DcMotorEx::class.java, Globals.rightRear)

        flMotor.direction = Globals.leftFrontDirection
        blMotor.direction = Globals.leftRearDirection
        frMotor.direction = Globals.rightFrontDirection
        brMotor.direction = Globals.rightRearDirection

        flMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        blMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        frMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        brMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        flMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        blMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        frMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        brMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Front Left Power", flMotor.power)
        telemetry.addData("Front Right Power", frMotor.power)
        telemetry.addData("Back Left Power", blMotor.power)
        telemetry.addData("Back Right Power", brMotor.power)
        telemetry.update()
    }

    fun mecanumLoop(gamepad: Gamepad){
        val gpy = -gamepad.left_stick_y.toDouble()
        val gpx = (gamepad.left_stick_x * 1.1).coerceIn(-1.0, 1.0)
        val rx = -gamepad.right_stick_x.toDouble()

        slowmode = if (slowmodeToggle)
            0.4
        else
            0.8

        moveValues(gpy, gpx, rx, slowmode)
    }

    fun moveValues(y: Double, x: Double, rotation: Double, motorCoefficient: Double) {
        val rot = -rotation
        var flPowerRaw = y + rot - x
        var blPowerRaw = y + rot + x
        var frPowerRaw = y - rot + x
        var brPowerRaw = y - rot - x

        //find the maximum of the powers
        var maxRawPower = Math.abs(flPowerRaw)
        if (Math.abs(blPowerRaw) > maxRawPower) maxRawPower = Math.abs(blPowerRaw)
        if (Math.abs(brPowerRaw) > maxRawPower) maxRawPower = Math.abs(brPowerRaw)
        if (Math.abs(frPowerRaw) > maxRawPower) maxRawPower = Math.abs(frPowerRaw)
        var scaleDownAmount = 1.0
        if (maxRawPower > 1.0) {
            //when max power is multiplied by this ratio, it will be 1.0, and others less
            scaleDownAmount = 1.0 / maxRawPower
        }
        flPowerRaw *= scaleDownAmount * motorCoefficient
        blPowerRaw *= scaleDownAmount * motorCoefficient
        frPowerRaw *= scaleDownAmount * motorCoefficient
        brPowerRaw *= scaleDownAmount * motorCoefficient
        flMotor.power = flPowerRaw
        blMotor.power = blPowerRaw
        frMotor.power = frPowerRaw
        brMotor.power = brPowerRaw
    }

}
