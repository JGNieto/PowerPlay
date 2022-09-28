package org.baylorschool.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "April Tag Test", group = "test")
class MichaelLiftTest: LinearOpMode() {

    override fun runOpMode() {
        telemetry.addData("Status", "Getting ready. Please wait...")
        telemetry.update()


    }

}