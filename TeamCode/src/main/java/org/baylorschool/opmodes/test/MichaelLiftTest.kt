package org.baylorschool.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.Globals
import org.baylorschool.util.MotorAngleDevice
import kotlin.math.PI

@TeleOp(name = "Michael Lift Test", group = "test")
class MichaelLiftTest: LinearOpMode() {

    override fun runOpMode() {
        val angleMotor = MotorAngleDevice(this, Globals.liftProximalA, Globals.liftProximalATicksPerRotation)

        telemetry.addData("Status", "Ready to start")
        telemetry.update()

        angleMotor.init()

        waitForStart()

        telemetry.addData("Status", "Running")
        telemetry.update()
        angleMotor.moveToAngle(PI / 2)

        while (!isStopRequested && opModeIsActive()) { }

        telemetry.addData("Status", "Stopping...")
        telemetry.update()

        angleMotor.cleanup()
    }

}