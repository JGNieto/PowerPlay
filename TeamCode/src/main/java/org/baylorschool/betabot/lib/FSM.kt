package org.baylorschool.betabot.lib

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.baylorschool.betabot.lib.SlidePIDConfig.targetPos
import org.firstinspires.ftc.robotcore.external.Telemetry


class FSM(hardwareMap: HardwareMap) {

    enum class V4BState {
        V4B_START, V4B_INTAKE, V4B_DEPOSIT, V4B_RETRACT
    }

    var currentGamepad2 = Gamepad()
    var previousGamepad2 = Gamepad()
    private val slides = Slides(hardwareMap)
    private val v4b = V4B (hardwareMap)
    private var v4bTimer = ElapsedTime()
    private var intakeTimer = ElapsedTime()
    private var v4bState: V4BState = V4BState.V4B_START
    private val intakingServo: CRServo
    private var intakeState: V4B.IntakeState = V4B.IntakeState.REST
    private val depositDelay = 3
    private var intakeToggle = false


    init {
        v4bTimer.reset()
        intakeTimer.reset()
        intakingServo = hardwareMap.get(CRServo::class.java, "intakeServo")
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Intake Power", intakingServo.power)
        telemetry.addData("Boolean Shiz", intakeToggle)
    }

    fun fsmLoop(gamepad: Gamepad) {
        intakingServo.power = intakeState.intakePower
        if (currentGamepad2.a && !previousGamepad2.a) {
            intakeToggle = !intakeToggle
        }

        intakeState = if (intakeToggle) {
            V4B.IntakeState.INTAKE
        } else if (gamepad.y) {
            V4B.IntakeState.DEPOSIT
        } else {
            V4B.IntakeState.REST
        }

        when(v4bState) {
            V4BState.V4B_START -> {
                targetPos = SlidePresets.RESET.poles
                v4b.v4bServo1.position = 0.0
                v4b.v4bServo2.position = 0.0
                if (gamepad.a) {
                    V4BState.V4B_INTAKE
                    v4b.v4bServo1.position = 0.2
                    v4b.v4bServo2.position = 0.2
                }
            }
            V4BState.V4B_INTAKE -> {
                targetPos = SlidePresets.RESET.poles
                if (gamepad.a && slides.slidePos <= 15)  {
                    intakeToggle = true
                }
                if (gamepad.y) {
                    V4BState.V4B_DEPOSIT
                }
                // V4B adjustment code is in V4B Class
            }
            V4BState.V4B_DEPOSIT -> {
                targetPos = SlidePresets.HIGH_POLE.poles
                if (slides.slidePos < 2500) {
                    v4b.v4bServo1.position = 0.8
                    v4b.v4bServo2.position = 0.8
                    if (gamepad.a) {
                        intakeState = V4B.IntakeState.DEPOSIT
                        intakeTimer.reset()
                        V4BState.V4B_RETRACT
                    }
                }
            }
            V4BState.V4B_RETRACT -> {
                if (intakeTimer.seconds() >= depositDelay) {
                    v4b.v4bServo1.position = 0.5
                    v4b.v4bServo2.position = 0.5
                    intakeState = V4B.IntakeState.REST
                    targetPos = SlidePresets.RESET.poles
                    V4BState.V4B_START
                }
            }
        }

        if (gamepad.a && v4bState != V4BState.V4B_START) {
            v4bState = V4BState.V4B_START
        }

    }
}
