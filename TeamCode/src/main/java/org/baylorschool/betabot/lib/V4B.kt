package org.baylorschool.betabot.lib

  import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
  import org.firstinspires.ftc.robotcore.external.Telemetry


class V4B(hardwareMap: HardwareMap) {

    enum class IntakeState(var intakePower: Double) {
       INTAKE(-.5), DEPOSIT(.5), REST(0.0)
    }
    val v4bServo1: Servo
    val v4bServo2: Servo

    init {
        v4bServo1 = hardwareMap.get(Servo::class.java, "v4bServo1")
        v4bServo2 = hardwareMap.get(Servo::class.java, "v4bServo2")
        v4bServo1.scaleRange(0.0, 0.9)
        v4bServo2.scaleRange(0.0, 0.9)
    }

    fun telemetry(telemetry: Telemetry) {
        telemetry.addData("V4B Position", v4bServo1.position)
        telemetry.addData("V4B Position", v4bServo2.position)
    }

    fun intakeLoop(gamepad: Gamepad) {
        if (gamepad.x) {
            v4bServo1.position += 0.005
            v4bServo2.position += 0.005
        } else if (gamepad.b) {
            v4bServo1.position -= 0.005
            v4bServo2.position -= 0.005
        }

    }
}
