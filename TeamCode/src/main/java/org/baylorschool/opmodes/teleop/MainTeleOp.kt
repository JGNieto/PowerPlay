package org.baylorschool.opmodes.teleop

//import com.outoftheboxrobotics.photoncore.Neutrino.Rev2MSensor.Rev2mDistanceSensorEx
//import com.outoftheboxrobotics.photoncore.PhotonCore
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.hardware.rev.Rev2mDistanceSensor
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.baylorschool.Globals
import org.baylorschool.util.Claw
import org.baylorschool.util.Mecanum
import org.baylorschool.util.angledevice.BasicMotorAngleDevice
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import kotlin.math.abs

@TeleOp(name = "MainTeleOp", group = "AA Main")
class MainTeleOp: LinearOpMode() {

    companion object {
        var proximalPosition = 0
        var distalAngle = Globals.liftDistalStartAngle
    }

    override fun runOpMode() {
        //PhotonCore.enable()

        //val motorA1 = hardwareMap.get(DcMotorEx::class.java, Globals.liftProximalA)
        //val ohmMotorA1 = OhmMotor(motorA1, Globals.liftProximalATicksPerRotation)
        val motorA1 = BasicMotorAngleDevice(this, Globals.liftProximalA, Globals.liftProximalATicksPerRotation, Globals.liftProximalConfig, Globals.liftProximalADirection)
        val motorB = BasicMotorAngleDevice(this, Globals.liftDistal, Globals.liftDistalTicksPerRotation, Globals.liftDistalConfig, Globals.liftDistalDirection)
        val claw = Claw(this)
        val clawPitch = hardwareMap.get(Servo::class.java, Globals.clawPitch)
        val mecanum = Mecanum(hardwareMap)
        val telemetry = MultipleTelemetry(FtcDashboard.getInstance().telemetry, telemetry)
        val distance = hardwareMap.get(Rev2mDistanceSensor::class.java, Globals.distanceSensor)

        waitForStart()

        mecanum.resetEncoders()

        motorA1.init()
        motorA1.reset(Globals.liftProximalStartAngle)
        // motorA1.debug = true

        motorB.init()
        motorB.reset(distalAngle)
        //motorB.reset(Globals.liftDistalStartAngle)
        // motorB.debug = true
        motorB.telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        clawPitch.direction = Globals.clawPitchDirection

        var clawPosition = 0.0

        var wasMovingDistal = false
        var wasMovingProximal = false

        var previousTime = System.currentTimeMillis()

        // RIGHT = DISTAL
        // LEFT = PROXIMAL
        while (opModeIsActive()) {
            val currentTime = System.currentTimeMillis()
            val dt = (currentTime - previousTime) / 1000.0

            if (gamepad2.dpad_up) {
                motorA1.moveToAngle(Globals.liftDropHigh.proximal)
            } else if (gamepad2.dpad_left && false) {
                motorA1.moveToAngle(Globals.liftDropMid.proximal)
            } else if (gamepad2.dpad_right && false) {
                motorA1.moveToAngle(Globals.liftDropLow.proximal)
            } else if (gamepad2.dpad_down) {
                motorA1.moveToAngle(Globals.liftGrab1.proximal)
            } else if (gamepad2.a) {
                motorA1.moveToAngle(Globals.liftGrab2.proximal)
            } else if (gamepad2.x) {
                motorA1.moveToAngle(Globals.liftGrab3.proximal)
            } else if (gamepad2.b) {
                motorA1.moveToAngle(Globals.liftGrab4.proximal)
            } else if (gamepad2.y) {
                motorA1.moveToAngle(Globals.liftGrab5.proximal)
            } else if (abs(gamepad2.left_stick_y) > 0.3f || (motorA1.motorStatus == BasicMotorAngleDevice.MotorStatus.TELEOP_POWER && gamepad2.left_stick_y != 0f)) {
                wasMovingProximal = true
                motorA1.moveTeleOp(- gamepad2.left_stick_y * Globals.liftProximalConfig.teleOpSpeed)
            } else if (wasMovingProximal) {
                motorA1.moveToAngle(motorA1.getPosition())
                motorA1.motorStatus = BasicMotorAngleDevice.MotorStatus.MAINTAINING
                wasMovingProximal = false
            }

            if (gamepad2.dpad_up) {
                motorB.moveToAngle(Globals.liftDropHigh.distal)
            } else if (gamepad2.dpad_left && false) {
                motorB.moveToAngle(Globals.liftDropMid.distal)
            } else if (gamepad2.dpad_right && false) {
                motorB.moveToAngle(Globals.liftDropLow.distal)
            } else if (gamepad2.dpad_down) {
                motorB.moveToAngle(Globals.liftGrab1.distal)
            } else if (gamepad2.a) {
                motorB.moveToAngle(Globals.liftGrab2.distal)
            } else if (gamepad2.x) {
                motorB.moveToAngle(Globals.liftGrab3.distal)
            } else if (gamepad2.b) {
                motorB.moveToAngle(Globals.liftGrab4.distal)
            } else if (gamepad2.y) {
                motorB.moveToAngle(Globals.liftGrab5.distal)
            } else if (abs(gamepad2.right_stick_y) > 0.3f || (motorB.motorStatus == BasicMotorAngleDevice.MotorStatus.TELEOP_POWER && gamepad2.right_stick_y != 0f)) {
                wasMovingDistal = true
                motorB.moveTeleOp(- gamepad2.right_stick_y * Globals.liftDistalConfig.teleOpSpeed)
            } else if (wasMovingDistal) {
                motorB.moveToAngle(motorB.getPosition())
                motorB.motorStatus = BasicMotorAngleDevice.MotorStatus.MAINTAINING
                wasMovingDistal = false
            }

            if (gamepad2.right_bumper) {
                clawPosition += 0.4 * dt
            } else if (gamepad2.left_bumper) {
                clawPosition -= 0.4 * dt
            } else if (gamepad2.dpad_up) {
                clawPosition = Globals.liftDropHigh.claw
            } else if (gamepad2.dpad_left && false) {
                clawPosition = Globals.liftDropMid.claw
            } else if (gamepad2.dpad_right && false) {
                clawPosition = Globals.liftDropLow.claw
            } else if (gamepad2.dpad_down) {
                clawPosition = Globals.liftGrab1.claw
            } else if (gamepad2.a) {
                clawPosition = Globals.liftGrab2.claw
            } else if (gamepad2.x) {
                clawPosition = Globals.liftGrab3.claw
            } else if (gamepad2.b) {
                clawPosition = Globals.liftGrab4.claw
            } else if (gamepad2.y) {
                clawPosition = Globals.liftGrab5.claw
            }
            clawPosition = clawPosition.coerceIn(0.0, 1.0)
            clawPitch.position = clawPosition

            claw.grabPosition(gamepad2.right_trigger.toDouble())
            mecanum.mecanumLoop(gamepad1)

            mecanum.telemetry(telemetry)
            //telemetry.addData("Proximal position", motorA1.currentPosition)
            telemetry.addData("Proximal angle", motorA1.getPosition())
            telemetry.addData("Target proximal angle", motorA1.targetAngle)
            telemetry.addData("Distal angle", motorB.getPosition())
            telemetry.addData("Target distal angle", motorB.targetAngle)
            telemetry.addData("Target Claw pos", clawPosition)
            telemetry.addData("Claw pos", clawPitch.position)
            //telemetry.addData("Claw grab", claw.getPosition())
            //telemetry.addData("Distal status", motorB.motorStatus.toString())
            //telemetry.addData("Distal motor mode", motorB.motor.mode)
            //telemetry.addData("Distal motor busy", motorB.motor.isBusy)
            telemetry.addData("Distance (in)", distance.getDistance(DistanceUnit.INCH))

            mecanum.positionTelemetry(telemetry)
            telemetry.update()

            previousTime = currentTime
        }

        motorA1.cleanup()
        motorB.cleanup()
    }

    private fun map(inputMin: Double, inputMax: Double, outputMin: Double, outputMax: Double, value: Double): Double {
        return (value-inputMin)/(inputMax-inputMin) * (outputMax-outputMin) + outputMin
    }

}