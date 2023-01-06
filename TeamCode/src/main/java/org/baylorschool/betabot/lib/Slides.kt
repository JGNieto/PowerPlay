@file:Suppress("KotlinConstantConditions")

package org.baylorschool.betabot.lib

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.hardware.*
import org.baylorschool.betabot.lib.SlidePIDConfig.kg
import org.baylorschool.betabot.lib.SlidePIDConfig.p
import org.baylorschool.betabot.lib.SlidePIDConfig.targetPos
import org.firstinspires.ftc.robotcore.external.Telemetry

@Config
object SlidePIDConfig {
    @JvmField var p: Double = 0.115
    @JvmField var kg: Double = 0.11

    @JvmField var targetPos: Double = 0.0
}

class Slides(hardwareMap: HardwareMap) {
    private val slideMotor1: DcMotorEx
    private val slideMotor2: DcMotorEx
    private var slidePosition: Double = 0.0
    private val pControl = PIDCoefficients(p)
    private val controller1 = PIDFController(pControl)
    private var slidePower = 0.0
    private var offset = 0

    init {
        slideMotor1 = hardwareMap.get(DcMotorEx::class.java, "rLift")
        slideMotor2 = hardwareMap.get(DcMotorEx::class.java, "lLift")
        slidePosition = slideMotor1.currentPosition.toDouble()
        offset = slidePosition.toInt()
        slideMotor1.direction = DcMotorSimple.Direction.FORWARD
        slideMotor2.direction = DcMotorSimple.Direction.FORWARD
        slideMotor1.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        slideMotor2.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        targetPos = slidePosition - offset
        PhotonCore.enable()

    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Slide Motors Position", slidePosition)
        telemetry.addData("Target Position", targetPos)
    }

    fun updatePID() {
        slidePosition = slideMotor1.currentPosition.toDouble()
        controller1.targetPosition = targetPos
        slidePower = controller1.update(slidePosition) + kg
    }

    fun slideLoop(gamepad: Gamepad) {
        updatePID()
        slideMotor1.power = slidePower
        slideMotor2.power = slidePower

        if (gamepad.dpad_up) {
            targetPos += 1.0
        } else if (gamepad.dpad_down) {
            targetPos -= 1.0
        }
    }
}

private fun hardStops(value: Int, low: Int, high: Int): Int {
    return if (value < low) low
    else if (value > high) high
    else value
}

