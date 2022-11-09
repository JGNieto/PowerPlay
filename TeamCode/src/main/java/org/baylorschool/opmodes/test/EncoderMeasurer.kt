package org.baylorschool.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.baylorschool.Globals

@TeleOp(name = "Encoder Measurer", group = "test")
class EncoderMeasurer: LinearOpMode() {

    override fun runOpMode() {
        val liftProximalA = hardwareMap.get(DcMotorEx::class.java, Globals.liftProximalA)
        val liftDistal = hardwareMap.get(DcMotorEx::class.java, Globals.liftDistal)

        liftProximalA.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        liftDistal.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT

        liftProximalA.power = 0.0
        liftDistal.power = 0.0

        liftProximalA.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        liftDistal.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

        telemetry.addData("Status", "Ready to start")
        telemetry.update()

        liftProximalA.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        liftDistal.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        val liftProximalAZero = liftProximalA.currentPosition
        val liftDistalZero = liftProximalA.currentPosition

        waitForStart()

        while (!isStopRequested && opModeIsActive()) {
            telemetry.addData("Proximal A Zero", liftProximalAZero)
            telemetry.addData("Proximal A Position", liftProximalA.currentPosition)
            telemetry.addData("Distal Zero", liftDistalZero)
            telemetry.addData("Distal Position", liftDistal.currentPosition)
            telemetry.update()
        }
    }

}