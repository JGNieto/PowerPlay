package org.baylorschool.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.baylorschool.Globals

@TeleOp(name = "Single Motor Test", group = "test")
class SingleMotorTest: LinearOpMode() {
    override fun runOpMode() {
        val liftProximalA = hardwareMap.get(DcMotorEx::class.java, Globals.liftProximalA)
        liftProximalA.direction = Globals.liftProximalADirection

        telemetry.addData("Status", "Waiting for start...")
        telemetry.update()

        waitForStart()

        liftProximalA.mode = DcMotor.RunMode.RUN_USING_ENCODER

        val startTime = System.currentTimeMillis()
        while (opModeIsActive() && System.currentTimeMillis() - startTime < 3000) {
            liftProximalA.power = 0.6

            telemetry.addData("Encoder position", liftProximalA.currentPosition)
            telemetry.addData("Status", "Moving")
            telemetry.update()
        }

        telemetry.addData("Status", "Stop")
        telemetry.update()
    }


}