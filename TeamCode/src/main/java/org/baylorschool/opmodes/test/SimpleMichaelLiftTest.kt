package org.baylorschool.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.Globals
import org.baylorschool.util.angledevice.BasicMotorAngleDevice
import org.baylorschool.util.angledevice.TargetAngleDirection
import kotlin.math.PI

@Disabled
@TeleOp(name = "Simple Michael Lift Test", group = "test")
class SimpleMichaelLiftTest: LinearOpMode() {

    override fun runOpMode() {
        val liftProximalA = BasicMotorAngleDevice(this, Globals.liftProximalA, Globals.liftProximalATicksPerRotation, Globals.liftProximalConfig, Globals.liftProximalADirection)
        val liftDistal = BasicMotorAngleDevice(this, Globals.liftDistal, Globals.liftDistalTicksPerRotation, Globals.liftDistalConfig, Globals.liftDistalDirection)

        telemetry.addData("Status", "Ready to start")
        telemetry.update()

        liftDistal.debug = true

        liftProximalA.init()
        liftDistal.init()

        liftProximalA.reset(0.0)
        liftDistal.reset(PI)

        waitForStart()

        telemetry.addData("Status", "Running")
        telemetry.update()
        liftProximalA.moveToAngle(PI / 2, TargetAngleDirection.COUNTERCLOCKWISE)
        liftDistal.moveToAngle(PI / 2, TargetAngleDirection.COUNTERCLOCKWISE)

        while (!isStopRequested && opModeIsActive()) { }

        telemetry.addData("Status", "Stopping...")
        telemetry.update()

        liftProximalA.cleanup()
        liftDistal.cleanup()
    }

}