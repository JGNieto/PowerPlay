package org.baylorschool.opmodes.test

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.PIDCoefficients
import org.baylorschool.Globals
import org.baylorschool.drive.Mecanum
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.d
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.i
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.p
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.x
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.y
import org.baylorschool.util.Claw
import org.baylorschool.util.LiftPresets
import org.baylorschool.util.MichaelLift
import org.baylorschool.util.angledevice.BasicMotorAngleDevice

@Config
object MichaelLiftTestConfig {
    @JvmField var x = 20.0
    @JvmField var y = 7.0

    @JvmField var p = 10.0
    @JvmField var i = 0.5
    @JvmField var d = 0.0
    @JvmField var f = 0.0
}

@TeleOp(name = "Michael Lift Test", group = "test")
class MichaelLiftTest: LinearOpMode() {
    val maxSpeed = 8.0

    override fun runOpMode() {
        PhotonCore.enable();

        var powerMultiplier = 0.1

        val michaelLift = MichaelLift(this)
        val claw = Claw(this)
        val mecanum = Mecanum(hardwareMap)

        michaelLift.motorA1.reset(Globals.liftProximalStartAngle)
        michaelLift.motorA2.reset(Globals.liftProximalStartAngle)
        michaelLift.motorB.reset(Globals.liftDistalStartAngle)

        val proximalCoefficients = PIDCoefficients(p, i, d)
        // (michaelLift.motorA1 as BasicMotorAngleDevice).setPIDCoefficients(proximalCoefficients)

         michaelLift.motorA1.debug = true
        // michaelLift.motorA2.debug = true
        // michaelLift.motorB.debug = true
        // michaelLift.debug = true

        michaelLift.init()

        waitForStart()

        var xPos = x
        var yPos = y

        michaelLift.movingClaw = false
        michaelLift.goToPosition(xPos, yPos, MichaelLift.SyncMode.PROXIMAL_FIRST)
        claw.close()

        val startTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - startTime < 3000) {
            michaelLift.iteration()
        }

        var previousTime = System.currentTimeMillis()
        michaelLift.movingClaw = true

        while (opModeIsActive()) {
            val currentTime = System.currentTimeMillis()

            if (gamepad2.a) { // HELL
                michaelLift.moveToMode(MichaelLift.LiftMode.GROUND, LiftPresets.hell)
                xPos = michaelLift.x
                yPos = michaelLift.y
            } else if (gamepad2.b) { // HEAVEN HIGH
                michaelLift.moveToMode(MichaelLift.LiftMode.HIGH, LiftPresets.heavenHigh)
                xPos = michaelLift.x
                yPos = michaelLift.y
            } else if (gamepad2.y) { // HEAVEN MID
                michaelLift.moveToMode(MichaelLift.LiftMode.HIGH, LiftPresets.heavenMid)
                xPos = michaelLift.x
                yPos = michaelLift.y
            } else if (gamepad2.x) { // HEAVEN LOW
                michaelLift.moveToMode(MichaelLift.LiftMode.HIGH, LiftPresets.heavenLow)
                xPos = michaelLift.x
                yPos = michaelLift.y
            } else {
                val timeDiff = (currentTime - previousTime) / 1000.0

                /*if (gamepad2.right_stick_x != 0f || gamepad2.right_stick_y != 0f) {
                    val xPosTemp = xPos
                    val yPosTemp = yPos

                    xPos += gamepad2.right_stick_x * timeDiff * maxSpeed
                    yPos += gamepad2.right_stick_y * timeDiff * maxSpeed

                    val validNewPosition = michaelLift.goToPosition(xPos, yPos)

                    if (!validNewPosition) {
                        xPos = xPosTemp
                        yPos = yPosTemp
                    }
                }*/
            }

            michaelLift.iteration()
            mecanum.setDrivePower(
                Pose2d(
                -gamepad1.left_stick_y.toDouble() * powerMultiplier,
                -gamepad1.left_stick_x.toDouble() * powerMultiplier,
                -gamepad1.right_stick_x.toDouble() * powerMultiplier,
            ))

            claw.grabPosition(gamepad2.right_trigger.toDouble())

            previousTime = currentTime

            telemetry.addData("X", xPos)
            telemetry.addData("Y", yPos)
            telemetry.addData("Gamepad X", gamepad2.right_stick_x)
            telemetry.addData("Gamepad Y", gamepad2.right_stick_y)
            telemetry.update()
        }

        michaelLift.cleanup()
    }
}