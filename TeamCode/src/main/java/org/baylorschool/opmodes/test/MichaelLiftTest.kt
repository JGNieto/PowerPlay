package org.baylorschool.opmodes.test

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.x
import org.baylorschool.opmodes.test.MichaelLiftTestConfig.y
import org.baylorschool.util.MichaelLift
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
        michaelLift.motorB.reset(-PI)

        michaelLift.init()

        waitForStart()

        michaelLift.goToPosition(x, y)

        while (opModeIsActive()) {
            michaelLift.iteration()
        }

        michaelLift.cleanup()
    }
}