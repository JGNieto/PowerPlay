@file:Suppress("SameParameterValue")

package org.baylorschool.betabot.lib

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
//import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.util.ElapsedTime
import org.baylorschool.betabot.lib.SlidePIDConfig.kg
import org.baylorschool.betabot.lib.SlidePIDConfig.p
import org.baylorschool.betabot.lib.SlidePIDConfig.targetPos
import org.firstinspires.ftc.robotcore.external.Telemetry

@Config
object SlidePIDConfig {
    @JvmField var p: Double = 0.016
    @JvmField var kg: Double = 0.15 // gravity ong

    @JvmField var targetPos: Double = 0.0
}

enum class SlidePresets(var poles: Double) {
    RESET(0.0),LOW_POLE(1050.0), MID_POLE(2050.0), HIGH_POLE(3350.0);
}
class Slides(hardwareMap: HardwareMap) {
    private var profileTimer = ElapsedTime()
    private val slideMotor1: DcMotorEx
    private val slideMotor2: DcMotorEx
    var slidePos: Double = 0.0
    private val pControl = PIDCoefficients(p)
    private val controller = PIDFController(pControl /*, kV = 0.0, kA = 0.0, kStatic = 0.0 */)
    private var slidePower = 0.0
    private var offset = 0
    private val high: Int = 3351
    private val low: Int = -1

    init {
        slideMotor1 = hardwareMap.get(DcMotorEx::class.java, "rLift")
        slideMotor2 = hardwareMap.get(DcMotorEx::class.java, "lLift")
        offset = slideMotor2.currentPosition
        slidePos = slideMotor2.currentPosition.toDouble() - offset
        slideMotor1.direction = DcMotorSimple.Direction.FORWARD
        slideMotor2.direction = DcMotorSimple.Direction.FORWARD
        slideMotor1.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        slideMotor2.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        //PhotonCore.enable()
        profileTimer.reset()
        controller.reset()
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Slides Max Velocity", slideMotor2.velocity)
        telemetry.addData("Slide Motor Position", slidePos)
        telemetry.addData("Target Position", targetPos)
    }

    private fun update() {
        slidePos  = slideMotor2.currentPosition.toDouble() - offset
        controller.targetPosition = targetPos
        slidePower = controller.update(slidePos) + kg
    }

    fun slideLoop(gamepad: Gamepad) {
        update()
        targetPos = hardStops(targetPos.toInt(), low, high).toDouble()
        slideMotor1.power = slidePower
        slideMotor2.power = slidePower

        if (gamepad.dpad_up) {
            targetPos += 25.0
        } else if (gamepad.dpad_down) {
            targetPos -= 25.0
        } else if (gamepad.left_stick_button) {
            targetPos = SlidePresets.HIGH_POLE.poles
        } else if (gamepad.dpad_left) {
            targetPos = SlidePresets.RESET.poles
        }
    }
}

private fun hardStops(value: Int, low: Int, high: Int): Int {
    return if (value < low) low + 1
    else if (value > high) high - 1
    else value
}


