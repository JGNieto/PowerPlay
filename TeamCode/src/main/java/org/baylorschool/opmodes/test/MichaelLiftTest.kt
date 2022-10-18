package org.baylorschool.opmodes.test

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.x
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.y
import org.baylorschool.util.MichaelLift
import org.baylorschool.util.angledevice.BasicMotorAngleDevice
import org.baylorschool.util.angledevice.MotorAngleDevice
import kotlin.math.PI

@Config
object MichaelLiftTestConfig {
    @JvmField var x = 20.0
    @JvmField var y = 7.0
}

@TeleOp(name = "Michael Lift Test", group = "test")
class MichaelLiftTest: LinearOpMode() {
    override fun runOpMode() {
        val michaelLift = MichaelLift(this)

        michaelLift.motorA1.reset(0.0)
        michaelLift.motorA2.reset(0.0)
        michaelLift.motorB.reset(PI)

        (michaelLift.motorB as BasicMotorAngleDevice).debug = true

        michaelLift.init()

        waitForStart()

        var xPos = x
        var yPos = y

        var previousTime = System.currentTimeMillis()

        michaelLift.goToPosition(xPos, yPos)

        while (opModeIsActive()) {
            val currentTime = System.currentTimeMillis()
            val timeDiff = (currentTime - previousTime) / 1000.0
            val posDelta = timeDiff * 1.0

            if (gamepad1.b) xPos += posDelta
            if (gamepad1.x) xPos -= posDelta
            if (gamepad1.y) yPos += posDelta
            if (gamepad1.a) yPos -= posDelta

            if (gamepad1.b || gamepad1.x || gamepad1.y || gamepad1.a) {
                michaelLift.goToPosition(xPos, yPos)
            }

            michaelLift.iteration()

            previousTime = currentTime

            telemetry.addData("X", xPos)
            telemetry.addData("Y", yPos)
            telemetry.update()
        }

        michaelLift.cleanup()
    }
}