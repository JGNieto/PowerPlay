package org.baylorschool.opmodes.test

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.Globals
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.x
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.y
import org.baylorschool.util.LiftPresets
import org.baylorschool.util.MichaelLift
import kotlin.math.PI

@Config
object MichaelLiftTestConfig {
    @JvmField var x = 20.0
    @JvmField var y = 7.0
}

@TeleOp(name = "Michael Lift Test", group = "test")
class MichaelLiftTest: LinearOpMode() {
    val maxSpeed = 8.0

    override fun runOpMode() {
        val michaelLift = MichaelLift(this)

        michaelLift.motorA1.reset(Globals.liftProximalStartAngle)
        michaelLift.motorA2.reset(Globals.liftProximalStartAngle)
        michaelLift.motorB.reset(Globals.liftDistalStartAngle)

        //(michaelLift.motorA1 as BasicMotorAngleDevice).debug = true
        //(michaelLift.motorA2 as BasicMotorAngleDevice).debug = true
        //(michaelLift.motorB as BasicMotorAngleDevice).debug = true

        michaelLift.init()

        waitForStart()

        var xPos = x
        var yPos = y

        var previousTime = System.currentTimeMillis()

        michaelLift.goToPosition(xPos, yPos, MichaelLift.SyncMode.PROXIMAL_FIRST)

        while (opModeIsActive()) {
            val currentTime = System.currentTimeMillis()

            if (gamepad1.a) { // HELL
                michaelLift.moveToMode(MichaelLift.LiftMode.GROUND, LiftPresets.hell)
                xPos = michaelLift.x
                yPos = michaelLift.y
            } else if (gamepad1.b) { // HEAVEN HIGH
                michaelLift.moveToMode(MichaelLift.LiftMode.HIGH, LiftPresets.heavenHigh)
                xPos = michaelLift.x
                yPos = michaelLift.y
            } else if (gamepad1.y) { // HEAVEN MID
                michaelLift.moveToMode(MichaelLift.LiftMode.HIGH, LiftPresets.heavenMid)
                xPos = michaelLift.x
                yPos = michaelLift.y
            } else if (gamepad1.x) { // HEAVEN LOW
                michaelLift.moveToMode(MichaelLift.LiftMode.HIGH, LiftPresets.heavenLow)
                xPos = michaelLift.x
                yPos = michaelLift.y
            } else {
                val timeDiff = (currentTime - previousTime) / 1000.0

                if (gamepad1.right_stick_x != 0f || gamepad1.right_stick_y != 0f) {
                    val xPosTemp = xPos
                    val yPosTemp = yPos

                    xPos += -gamepad1.right_stick_x * timeDiff * maxSpeed
                    yPos += -gamepad1.right_stick_y * timeDiff * maxSpeed

                    val validNewPosition = michaelLift.goToPosition(xPos, yPos)

                    if (!validNewPosition) {
                        xPos = xPosTemp
                        yPos = yPosTemp
                    }
                }
            }

            michaelLift.iteration()

            previousTime = currentTime

            telemetry.addData("X", xPos)
            telemetry.addData("Y", yPos)
            telemetry.addData("Gamepad X", gamepad1.right_stick_x)
            telemetry.addData("Gamepad Y", gamepad1.right_stick_y)
            telemetry.update()
        }

        michaelLift.cleanup()
    }
}