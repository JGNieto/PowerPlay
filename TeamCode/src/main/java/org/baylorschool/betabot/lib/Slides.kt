@file:Suppress("KotlinConstantConditions")

package org.baylorschool.betabot.lib

import com.acmerobotics.dashboard.config.Config
import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.baylorschool.betabot.lib.SlidePowerConfig.powerDown
import org.baylorschool.betabot.lib.SlidePowerConfig.powerGotoDown
import org.baylorschool.betabot.lib.SlidePowerConfig.powerGotoUp
import org.baylorschool.betabot.lib.SlidePowerConfig.powerStay
import org.baylorschool.betabot.lib.SlidePowerConfig.powerUp
import org.firstinspires.ftc.robotcore.external.Telemetry

@Config
// Motor Power for slide tasks
object SlidePowerConfig {
    @JvmField var powerUp = 0.5
    @JvmField var powerDown = -0.3
    @JvmField var powerGotoUp = 0.9
    @JvmField var powerGotoDown = -0.2
    @JvmField var powerStay = 0.09
}

class Slides(hardwareMap: HardwareMap) {
    private val slideMotor1: DcMotorEx
    private val slideMotor2: DcMotorEx
    private val minEncoder = 0
    private val maxEncoder = 1060
    private var targetPosition = 0
    private var slidePosition: Int = 0
    private var movement = Movement.STAY
    val pos get() = slideMotor1.currentPosition

    enum class Movement {
        UP, DOWN, GOTO, STAY,
    }

    enum class GoalPosition(var slidePositions: Int) {
        HIGH(1000),
        MED(560),
        LOW(0);
    }

   init {
       slideMotor1 = hardwareMap.get(DcMotorEx::class.java, "rLift")
       slideMotor2 = hardwareMap.get(DcMotorEx::class.java, "lLift")
       slideMotor1.direction = DcMotorSimple.Direction.FORWARD
       slideMotor2.direction = DcMotorSimple.Direction.FORWARD
       slideMotor1.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
       slideMotor2.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
       slideMotor1.targetPosition = targetPosition
       slideMotor2.targetPosition = targetPosition
       PhotonCore.enable()
   }

    fun telemetry(telemetry: Telemetry){
        telemetry.addData("Slide Motors Position", slidePosition)
    }

    fun slideLoop(gamepad2: Gamepad) {
            slidePosition = slideMotor1.currentPosition

            if (gamepad2.dpad_up && slidePosition <= maxEncoder) {
                if (movement != Movement.UP) {
                    slideMotor1.mode = DcMotor.RunMode.RUN_USING_ENCODER
                    slideMotor2.mode = DcMotor.RunMode.RUN_USING_ENCODER
                    slideMotor1.power = powerUp
                    slideMotor2.power = powerUp
                    movement = Movement.UP
                }
            } else if (gamepad2.dpad_down && slidePosition >= minEncoder) {
                if (movement != Movement.DOWN) {
                    slideMotor1.mode = DcMotor.RunMode.RUN_USING_ENCODER
                    slideMotor2.mode = DcMotor.RunMode.RUN_USING_ENCODER
                    slideMotor1.power = powerDown
                    slideMotor2.power = powerDown
                    movement = Movement.DOWN
                }
            } else if (gamepad2.left_trigger > .1) {
                movement = Movement.GOTO
                targetPosition = GoalPosition.MED.slidePositions
            } else if (gamepad2.left_stick_button) {
                movement = Movement.GOTO
                targetPosition = GoalPosition.HIGH.slidePositions
            } else if (gamepad2.left_bumper) {
                movement = Movement.GOTO
                targetPosition = GoalPosition.LOW.slidePositions
            } else {
                when (movement) {
                    Movement.STAY -> {
                        // TODO: different power if we are beyond targetPositionDifference
                        moveMotorStay(slideMotor1, powerStay, targetPosition)
                        moveMotorStay(slideMotor2, powerStay, targetPosition)
                    }
                    Movement.GOTO -> {
                        if (slideMotor1.targetPosition == targetPosition && slideMotor2.targetPosition == targetPosition) {
                            if (!slideMotor1.isBusy || !slideMotor2.isBusy) {
                                movement = Movement.STAY
                            }
                        } else {
                            val power: Double = if (targetPosition > slidePosition) powerGotoUp else powerGotoDown
                            moveMotorGoto(slideMotor1, power, targetPosition)
                            moveMotorGoto(slideMotor2, power, targetPosition)
                        }
                    }
                    else -> {
                        targetPosition = hardStops(slidePosition, minEncoder, maxEncoder)
                        movement = Movement.STAY
                    }
                }
            }
        }
    }

    private fun hardStops(value: Int, low: Int, high: Int): Int {
        return if (value < low) low
        else if (value > high) high
        else value
    }

    private fun moveMotorStay(motor: DcMotorEx, power: Double, target: Int) {
        if (!motor.isBusy || motor.mode != DcMotor.RunMode.RUN_TO_POSITION) {
            motor.targetPosition = target
            motor.power = power
            motor.mode = DcMotor.RunMode.RUN_TO_POSITION
        }
    }

    private fun moveMotorGoto(motor: DcMotorEx, power: Double, target: Int) {
        motor.targetPosition = target
        motor.power = power
        motor.mode = DcMotor.RunMode.RUN_TO_POSITION
    }