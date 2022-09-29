package org.baylorschool.opmodes.test

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.Globals
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.motionPower
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.stablePower
import org.baylorschool.util.MotorAngleDevice
import kotlin.math.PI

@Config
object MichaelLiftTestConfig {
    @JvmField var motionPower = .05
    @JvmField var stablePower = .05
}

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

        angleMotor.motionPower = motionPower
        angleMotor.stablePower = stablePower

        angleMotor.moveToAngle(PI / 2)

        while (!isStopRequested && opModeIsActive()) {
            angleMotor.motionPower = motionPower
            angleMotor.stablePower = stablePower
        }

        telemetry.addData("Status", "Stopping...")
        telemetry.update()

        angleMotor.cleanup()
    }

}