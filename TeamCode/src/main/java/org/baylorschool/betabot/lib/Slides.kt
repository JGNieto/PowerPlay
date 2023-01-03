@file:Suppress("KotlinConstantConditions")

package org.baylorschool.betabot.lib

import com.acmerobotics.dashboard.config.Config
import com.arcrobotics.ftclib.controller.PIDController
import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.betabot.lib.SlidePIDConfig.p
import org.baylorschool.betabot.lib.SlidePIDConfig.i
import org.baylorschool.betabot.lib.SlidePIDConfig.d
import org.baylorschool.betabot.lib.SlidePIDConfig.f
import org.baylorschool.betabot.lib.SlidePIDConfig.targetPosition
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.cos

@Config
object SlidePIDConfig {
    @JvmField var p: Double = 0.0
    @JvmField var i: Double = 0.0
    @JvmField var d: Double = 0.0
    @JvmField var f: Double = 0.0

    @JvmField var targetPosition = 0
}

class Slides(hardwareMap: HardwareMap) {
    private val slideMotor1: DcMotorEx
    private val slideMotor2: DcMotorEx
    private var controller: PIDController
    private val ticks: Double = 537.7
    private var slidePosition: Int = 0
    private var liftPos: Double = 0.0
    var pid: Double = 0.0
    private var ff: Double = 0.0
    private var slidePower: Double = 0.0

    init {
        slideMotor1 = hardwareMap.get(DcMotorEx::class.java, "rLift")
        slideMotor2 = hardwareMap.get(DcMotorEx::class.java, "lLift")
        controller = PIDController(p, i, d)
        slideMotor1.direction = DcMotorSimple.Direction.FORWARD
        slideMotor2.direction = DcMotorSimple.Direction.FORWARD
        slideMotor1.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        slideMotor2.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        slideMotor1.targetPosition = targetPosition
        slideMotor2.targetPosition = targetPosition
        PhotonCore.enable()
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Slide Motors Position", slidePosition)
        telemetry.addData("Target Position", targetPosition)
    }

    fun slideLoop(gamepad: Gamepad) {
        controller.setPID(p, i, d)
        slidePosition = slideMotor1.currentPosition
        liftPos = slideMotor1.currentPosition.toDouble()

        pid = controller.calculate(liftPos, targetPosition.toDouble())
        ff = cos(Math.toRadians(targetPosition / ticks)) * f

        slidePower = pid * ff

        slideMotor1.power - slidePower
        slideMotor2.power = slidePower

        if (gamepad.dpad_up) {
            targetPosition += 1
        } else if (gamepad.dpad_down) {
            targetPosition -= 1
        }
    }
}
