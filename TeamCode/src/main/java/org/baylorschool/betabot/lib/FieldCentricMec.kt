package org.baylorschool.betabot.lib

//import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Disabled
@TeleOp (name = "Field Centric Mec",group = "Test")
class FieldCentricMec : LinearOpMode() {
    @Throws(InterruptedException::class)
    override fun runOpMode() {

        val flMotor = hardwareMap.dcMotor["flMotor"]
        val blMotor = hardwareMap.dcMotor["blMotor"]
        val frMotor = hardwareMap.dcMotor["frMotor"]
        val brMotor = hardwareMap.dcMotor["brMotor"]
        val imu = hardwareMap.get(BNO055IMU::class.java, "imu")

        frMotor.direction = DcMotorSimple.Direction.REVERSE
        brMotor.direction = DcMotorSimple.Direction.REVERSE
        val parameters = BNO055IMU.Parameters()
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS
        imu.initialize(parameters)
        //PhotonCore.enable()

        var slowmodeToggle = false
        var slowmode = 1.0

        waitForStart()

        while (opModeIsActive()) {
            val y = -gamepad1.left_stick_y.toDouble() // Remember, this is reversed!
            val x = gamepad1.left_stick_x * 1.1 // Counteract imperfect strafing
            val rx = gamepad1.right_stick_x.toDouble()

            val botHeading = -imu.angularOrientation.firstAngle.toDouble()
            val rotX = x * cos(botHeading) - y * sin(botHeading)
            val rotY = x * sin(botHeading) + y * cos(botHeading)

            val denominator = max(abs(y) + abs(x) + abs(rx), 1.0)
            val frontLeftPower = (((rotY + rotX + rx) / denominator) * slowmode)
            val backLeftPower = (((rotY - rotX + rx) / denominator) * slowmode)
            val frontRightPower = (((rotY - rotX - rx) / denominator) * slowmode)
            val backRightPower = (((rotY + rotX - rx) / denominator) * slowmode)

            if (gamepad1.y)
                slowmodeToggle = false
            else if (gamepad1.a)
                slowmodeToggle = true

            slowmode = if (slowmodeToggle)
                0.4
            else
                1.0

            flMotor.power = frontLeftPower
            blMotor.power = backLeftPower
            frMotor.power = frontRightPower
            brMotor.power = backRightPower

        }
    }
}