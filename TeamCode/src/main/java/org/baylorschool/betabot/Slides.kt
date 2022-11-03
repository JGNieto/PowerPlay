package org.baylorschool.betabot

import com.arcrobotics.ftclib.command.SubsystemBase
import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class Slides(hardwareMap: HardwareMap) : SubsystemBase() {

    enum class SlidesPosition(var position: Int) {
        BOTTOM(32),
        MIDDLE(20),
        TOP(10);
    }

    private val slidesMotor: DcMotorEx

    init {
        slidesMotor = hardwareMap.get(DcMotorEx::class.java, "slidesMotor")
        slidesMotor.direction = DcMotorSimple.Direction.REVERSE
        slidesMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        slidesMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        slidesMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        PhotonCore.enable()
    }

    fun setslidesPower(power: Double) {
        slidesMotor.power = power
    }

    fun stop() {
        slidesMotor.power = 0.0
    }

    fun getslidesPosition(): Double {
        return slidesMotor.currentPosition.toDouble()
    }

    fun resetslidesPosition() {
        slidesMotor.mode =
            DcMotor.RunMode.STOP_AND_RESET_ENCODER
        slidesMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    fun atUpperLimit(): Boolean {
        return getslidesPosition() > 620.0
    }

    fun atLowerLimit(): Boolean {
        return getslidesPosition() < 5.0
    }
}