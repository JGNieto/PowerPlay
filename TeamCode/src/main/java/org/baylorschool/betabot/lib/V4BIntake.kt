package org.baylorschool.betabot.lib

import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry


class V4BIntake(hardwareMap: HardwareMap) {

    enum class IntakeState(var intakePower: Double) {
        HOLD(0.1), INTAKE(1.0), DEPOSIT(-0.1), REST(0.0)
    }

    enum class V4BState(var v4bPosition: Double) {
        V4B_START(0.8), V4B_INTAKE(0.0 /* place holder */), V4B_DEPOSIT(0.0), V4B_RETRACT(.8)
    }

    private val v4bServo: Servo
    private val intakingServo: CRServo
    private var hasFreight = false
    private var v4bState: V4BState = V4BState.V4B_START
    private var intakeState: IntakeState = IntakeState.HOLD
    private var v4bTimer = ElapsedTime()
    private var intakeTimer = ElapsedTime()
    private val slides = Slides(hardwareMap)
    private val depositDelay = 3

    init {
        v4bTimer.reset()
        intakeTimer.reset()
        intakingServo = hardwareMap.get(CRServo::class.java, "intakeServo")
        v4bServo = hardwareMap.get(Servo::class.java, "v4bServo")
        v4bServo.scaleRange(0.0, 1.0)
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Servo Position", v4bServo.position)
        telemetry.addData("Intake Power", intakingServo.power)
        telemetry.update()
    }

    fun intakeLoop(gamepad1: Gamepad) {
        intakingServo.power = intakeState.intakePower
        when(v4bState) {
            V4BState.V4B_START -> {
                Slides.GoalPosition.LOW.slidePositions
                intakeState = IntakeState.HOLD
                if (gamepad1.a && slides.pos <= 15)  {
                    v4bServo.position = 0.8
                    V4BState.V4B_INTAKE
                    intakeState = IntakeState.INTAKE
                }
                if (gamepad1.y) {
                    V4BState.V4B_DEPOSIT
                }
            }
            V4BState.V4B_INTAKE -> when {
                gamepad1.x -> {
                    v4bServo.position += 0.001
                }
                gamepad1.y -> {
                    intakeState = IntakeState.HOLD
                    V4BState.V4B_DEPOSIT
                }
            }
            V4BState.V4B_DEPOSIT -> {
                Slides.GoalPosition.HIGH.slidePositions
                if (slides.pos < (Slides.GoalPosition.HIGH.slidePositions - 20.0)) {
                    v4bServo.position = v4bState.v4bPosition
                    if (gamepad1.a) {
                        intakeState = IntakeState.DEPOSIT
                        intakeTimer.reset()
                        V4BState.V4B_RETRACT
                    }
                }
            }
            V4BState.V4B_RETRACT -> {
                if (intakeTimer.seconds() >= depositDelay) {
                    v4bServo.position = v4bState.v4bPosition
                    intakeState = IntakeState.REST
                    Slides.GoalPosition.LOW.slidePositions
                    V4BState.V4B_START
                }
            }
        }

        if (gamepad1.a && v4bState != V4BState.V4B_START) {
            v4bState = V4BState.V4B_START
        }
    }
}
