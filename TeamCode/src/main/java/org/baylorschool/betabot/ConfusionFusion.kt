package org.baylorschool.betabot

import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.betabot.SlidePowerConfig.powerDown
import org.baylorschool.betabot.SlidePowerConfig.powerGotoDown
import org.baylorschool.betabot.SlidePowerConfig.powerGotoUp
import org.baylorschool.betabot.SlidePowerConfig.powerStay
import org.baylorschool.betabot.SlidePowerConfig.powerUp
import kotlin.math.abs
import kotlin.math.max

@TeleOp (name = "Confusion",group = "Test")
class ConfusionFusion: LinearOpMode() {
    // Slide Movement
    enum class Movement {
        UP,
        DOWN,
        GOTO,
        STAY,
    }

    //Slide Encoder Values
    enum class GoalPosition(var slidePositions: Int) {
        HIGH(1000),
        MED(560),
        LOW(0);
    }

    @Throws(InterruptedException::class)
    override fun runOpMode() {

        val linkageServo = hardwareMap.get(Servo::class.java, "linkageServo")
        val slideMotor1 = hardwareMap.get(DcMotorEx::class.java, "rLift")
        val slideMotor2 = hardwareMap.get(DcMotorEx::class.java, "lLift")
        val flMotor = hardwareMap.dcMotor["flMotor"]
        val blMotor = hardwareMap.dcMotor["blMotor"]
        val frMotor = hardwareMap.dcMotor["frMotor"]
        val brMotor = hardwareMap.dcMotor["brMotor"]
        val minEncoder = 0
        val maxEncoder = 1060
        var targetPosition = 0
        var slidePosition: Int
        var movement = Movement.STAY
        PhotonCore.enable()

        var slowmodeToggle = false
        var slowmode = 0.4

        linkageServo.scaleRange(0.0, 0.2)
        slideMotor1.direction = DcMotorSimple.Direction.FORWARD
        slideMotor2.direction = DcMotorSimple.Direction.FORWARD
        frMotor.direction = DcMotorSimple.Direction.REVERSE
        brMotor.direction = DcMotorSimple.Direction.REVERSE

        slideMotor1.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        slideMotor2.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

        telemetry.addData("Status", "Ready to start")
        telemetry.update()

        waitForStart()

        slideMotor1.targetPosition = targetPosition
        slideMotor2.targetPosition = targetPosition

        var previousTime = System.currentTimeMillis()

        while (opModeIsActive()) {
            val currentTime = System.currentTimeMillis()

            slidePosition = slideMotor1.currentPosition

            val y = -gamepad1.left_stick_y.toDouble()
            val x = gamepad1.left_stick_x * 1.1
            val rx = -gamepad1.right_stick_x.toDouble()

            val denominator = max(abs(y) + abs(x) + abs(rx), 1.0)
            val frontLeftPower = (((y + x + rx) / denominator) * slowmode)
            val backLeftPower = (((y - x + rx) / denominator) * slowmode)
            val frontRightPower =(((y - x - rx) / denominator) * slowmode)
            val backRightPower = (((y + x - rx) / denominator) * slowmode)

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

            if (gamepad1.dpad_up && slidePosition <= maxEncoder) {
                if (movement != Movement.UP) {
                    slideMotor1.mode = DcMotor.RunMode.RUN_USING_ENCODER
                    slideMotor2.mode = DcMotor.RunMode.RUN_USING_ENCODER
                    slideMotor1.power = powerUp
                    slideMotor2.power = powerUp
                    movement = Movement.UP
                }
            } else if (gamepad1.dpad_down && slidePosition >= minEncoder) {
                if (movement != Movement.DOWN) {
                    slideMotor1.mode = DcMotor.RunMode.RUN_USING_ENCODER
                    slideMotor2.mode = DcMotor.RunMode.RUN_USING_ENCODER
                    slideMotor1.power = powerDown
                    slideMotor2.power = powerDown
                    movement = Movement.DOWN
                }
            } else if (gamepad1.left_trigger > .1) {
                movement = Movement.GOTO
                targetPosition = GoalPosition.MED.slidePositions
            } else if (gamepad1.left_stick_button) {
                movement = Movement.GOTO
                targetPosition = GoalPosition.HIGH.slidePositions
            } else if (gamepad1.left_bumper) {
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

            if (gamepad1.dpad_right)
                linkageServo.position += 0.005
            else if (gamepad1.dpad_left)
                linkageServo.position -= 0.005

            previousTime = currentTime

            telemetry.addData("Servo Position", linkageServo.position)
            telemetry.addData("Slide Motors Position", slidePosition)
            telemetry.update()
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
}