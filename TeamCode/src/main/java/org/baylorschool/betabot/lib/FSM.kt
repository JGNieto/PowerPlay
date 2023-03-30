package org.baylorschool.betabot.lib

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.baylorschool.betabot.lib.SlidePIDConfig.targetPos
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs


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
    private val depositDelay = 2.5
    private var intakeToggle = false
    private var depoToggle = false



    init {
        v4bTimer.reset()
        intakeTimer.seconds()
        intakingServo = hardwareMap.get(CRServo::class.java, "intakeServo")
        v4b.v4bServo1.scaleRange(0.27, 1.0)
        v4b.v4bServo2.scaleRange(0.27, 1.0)
        v4b.v4bServo1.position = 0.0
        v4b.v4bServo2.position = 0.0
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Intake Power", intakingServo.power)
        telemetry.addData("Boolean Shiz", intakeToggle)
        telemetry.addData("FSM", v4bState)
        telemetry.addData("FSM timer", intakeTimer.seconds())
    }

    fun fsmLoop(gamepad: Gamepad) {
        intakingServo.power = intakeState.intakePower
        intakeState = if (intakeToggle) {
            V4B.IntakeState.INTAKE
        } else if (depoToggle) {
            V4B.IntakeState.DEPOSIT
        } else {
            V4B.IntakeState.REST
        }

        when(v4bState) {
            V4BState.V4B_START -> {

                v4b.v4bServo1.position = 0.4
                v4b.v4bServo2.position = 0.4
                targetPos = -1.0
                if (gamepad.a) {
                    v4bState = V4BState.V4B_INTAKE
                }
                if (gamepad.y)
                    v4bState = V4BState.V4B_DEPOSIT
            } V4BState.V4B_INTAKE -> {
                if (currentGamepad2.a && !previousGamepad2.a)
                    intakeToggle = !intakeToggle
                if (gamepad.y) {
                    v4bState = V4BState.V4B_DEPOSIT
                    targetPos = SlidePresets.HIGH_POLE.poles
                    // V4B adjustment code is in V4B Class
                }
                if (gamepad.x) {
                    v4b.v4bServo1.position -= 0.01
                    v4b.v4bServo2.position -= 0.01
                }
            if (gamepad.b) {
                v4b.v4bServo1.position = 0.4
                v4b.v4bServo2.position = 0.4
            }
            } V4BState.V4B_DEPOSIT -> {
                if (abs(SlidePresets.HIGH_POLE.poles - slides.slidePos) > 100) {
                    v4b.v4bServo1.position = 0.8
                    v4b.v4bServo2.position = 0.8
                }
                if (gamepad.dpad_left)
                    targetPos = SlidePresets.MID_POLE.poles
                if (gamepad.dpad_down)
                    targetPos = SlidePresets.RESET.poles
                if (gamepad.dpad_right)
                    targetPos = SlidePresets.LOW_POLE.poles
                if (gamepad.dpad_up)
                    targetPos = SlidePresets.HIGH_POLE.poles
                if (gamepad.a) {
                    intakeToggle = false
                    depoToggle = true
                    intakeTimer.reset()
                    v4bState = V4BState.V4B_RETRACT
                }
            } V4BState.V4B_RETRACT -> {
                if (intakeTimer.seconds() >= depositDelay) {
                    v4b.v4bServo1.position = 0.4
                    v4b.v4bServo2.position = 0.4
                    depoToggle = false
                    targetPos = SlidePresets.RESET.poles
                    if (slides.slidePos < 10) {
                        v4bState = V4BState.V4B_START
                    }
                }
            }
        }

        if (gamepad.back && v4bState != V4BState.V4B_START)
            v4bState = V4BState.V4B_START
    }
}
