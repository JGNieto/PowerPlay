package org.baylorschool.betabot.lib

//import com.outoftheboxrobotics.photoncore.PhotonCore
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class Mecanum(hardwareMap: HardwareMap) {
    private val i: BNO055IMU
    private val flMotor: DcMotorEx
    private val frMotor: DcMotorEx
    private val blMotor: DcMotorEx
    private val brMotor: DcMotorEx
    private var slowmodeToggle = false
    private var slowmode = 1.0
    private val parameters = BNO055IMU.Parameters()


    init {
        i = hardwareMap.get(BNO055IMU::class.java, "imu")
        flMotor = hardwareMap.get(DcMotorEx::class.java, "flMotor")
        blMotor = hardwareMap.get(DcMotorEx::class.java, "blMotor")
        frMotor = hardwareMap.get(DcMotorEx::class.java, "frMotor")
        brMotor = hardwareMap.get(DcMotorEx::class.java, "brMotor")
        frMotor.direction = DcMotorSimple.Direction.REVERSE
        brMotor.direction = DcMotorSimple.Direction.REVERSE
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS
        i.initialize(parameters)
        //PhotonCore.enable()
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("Front Left Power", flMotor.power)
        telemetry.addData("Front Right Power", frMotor.power)
        telemetry.addData("Back Left Power", blMotor.power)
        telemetry.addData("Back Right Power", brMotor.power)
    }

    fun mecanumLoop(gamepad1: Gamepad){
        val gp1y = gamepad1.left_stick_y.toDouble()// - Range.clip(y* 2.0, -tipAuthority, tipAuthority)
        val gp1x = -(gamepad1.left_stick_x * 1.1) // +  Range.clip(x* 2.0, -tipAuthority, tipAuthority)
        val rx = -gamepad1.right_stick_x.toDouble()

        val botHeading = -i.angularOrientation.firstAngle.toDouble()
        val rotX = gp1x * cos(botHeading) - gp1y * sin(botHeading)
        val rotY = gp1x * sin(botHeading) + gp1y * cos(botHeading)

        val denominator = max(abs(gp1y) + abs(gp1x) + abs(rx), 1.0)
        val frontLeftPower = (((rotY + rotX + rx) / denominator) * slowmode)
        val backLeftPower = (((rotY - rotX + rx) / denominator) * slowmode)
        val frontRightPower = (((rotY - rotX - rx) / denominator) * slowmode)
        val backRightPower = (((rotY + rotX - rx) / denominator) * slowmode)

        if (gamepad1.right_bumper)
            slowmodeToggle = false
        else if (gamepad1.left_bumper)
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
