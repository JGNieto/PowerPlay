package org.baylorschool.betabot

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.CRServo

@TeleOp(name = "Intake Test", group = "test")
class IntakeTest: LinearOpMode() {

    override fun runOpMode() {
        var intaking = false
        val intakingServo = hardwareMap.get(CRServo::class.java, "intakeServo")
        val voltage = hardwareMap.get(AnalogInput::class.java, "voltage")
        telemetry.addData("Status", "Ready to start")
        telemetry.update()

        waitForStart()

        while (opModeIsActive() && !isStopRequested) {
            if (gamepad1.y)
                intaking = true
            else if (gamepad1.a)
                intaking = false

            if (intaking)
                intakingServo.power = 1.0
            else
                intakingServo.power = 0.0

            telemetry.addData("voltage" ,voltage.voltage.toString())
            telemetry.update()
        }
    }
}