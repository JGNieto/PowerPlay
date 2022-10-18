package org.baylorschool.util

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.baylorschool.Globals
import org.baylorschool.util.angledevice.AngleDevice
import org.baylorschool.util.angledevice.BasicMotorAngleDevice
import org.baylorschool.util.angledevice.EmptyAngleDevice
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

// Bar lengths
const val a = 15.25 // in
const val b = 8.0 // in

class MichaelLift(opMode: OpMode) {
    /*
    Warning for the reader: many variables in this file are not meaningfully named because it is
    based on Michael's model on Desmos. Please refer to the masterpiece for better understanding:
    https://www.desmos.com/calculator/3p3siwof5a
     */

    enum class LiftMode {
        HIGH, GROUND
    }

    val motorA1: AngleDevice // Proximal 1
    val motorA2: AngleDevice // Proximal 2
    val motorB: AngleDevice  // Distal

    init {
        motorA1 = BasicMotorAngleDevice(opMode, Globals.liftProximalA, Globals.liftProximalATicksPerRotation, Globals.liftProximalConfig, Globals.liftProximalADirection)
        motorA2 = EmptyAngleDevice()
        motorB = BasicMotorAngleDevice(opMode, Globals.liftDistal, Globals.liftDistalTicksPerRotation, Globals.liftDistalConfig, Globals.liftDistalDirection)
    }

    private var x = 0.0
    private var y = 0.0
    private var needToUpdate = false

    private var liftMode: LiftMode = LiftMode.GROUND

    fun iteration() {
        val telemetry = FtcDashboard.getInstance().telemetry
        if (!needToUpdate) return

        val angleProximal = when (liftMode) {
            LiftMode.HIGH -> angleHighProximal()
            LiftMode.GROUND -> angleGroundProximal()
        }


        val angleDistal = when (liftMode) {
            LiftMode.HIGH -> angleHighDistal()
            LiftMode.GROUND -> angleGroundDistal()
        }

        val angleDistalRelative = angleDistal - angleProximal

        motorA1.moveToAngle(clamp(angleProximal, 0.0, PI))
        motorA2.moveToAngle(clamp(angleProximal, 0.0, PI))
        motorB.moveToAngle(angleDistalRelative)


        telemetry.addLine("Angle proximal: $angleProximal")
        telemetry.addLine("Angle distal: $angleDistal")
        telemetry.addLine("Angle distal relative: $angleDistalRelative")
        telemetry.update()

        needToUpdate = false
    }

    fun changeMode() {

    }

    fun init() {
        motorA1.init()
        motorA2.init()
        motorB.init()
    }

    fun stop() {
        motorA1.stop()
        motorA2.stop()
        motorB.stop()
    }

    fun cleanup() {
        motorA1.cleanup()
        motorA2.cleanup()
        motorB.cleanup()
    }

    fun goToPosition(x: Double, y: Double) {
        if ((a - b).pow(2) <= x.pow(2) + y.pow(2) && x.pow(2) + y.pow(2) <= (a + b).pow(2)) {
            this.x = x
            this.y = y

            this.needToUpdate = true
        }
    }

    // Michael's pristine manuscript translated into barely understandable code below
    private fun k(): Double {
        return sqrt(- ((a + b).pow(2) - x.pow(2) - y.pow(2)) * ((a - b).pow(2) - x.pow(2) - y.pow(2)))
    }

    private fun ka(): Double {
        return a.pow(2) - b.pow(2) + x.pow(2) + y.pow(2)
    }

    private fun kb(): Double {
        return b.pow(2) - a.pow(2) + x.pow(2) + y.pow(2)
    }

    // Mode ground
    private fun angleGroundProximal(): Double {
        val numerator = y * ka() + x * k()
        val denominator = x * ka() - y * k()
        return atan(numerator / denominator)
    }

    private fun angleGroundDistal(): Double {
        val numerator = y * kb() - x * k()
        val denominator = x * kb() + y * k()

        val arctan = atan(numerator / denominator)

        return if (arctan <= 0) {
            arctan
        } else {
            arctan - PI
        }
    }

    // Mode high
    private fun angleHighProximal(): Double {
        val numerator = y * ka() - x * k()
        val denominator = x * ka() + y * k()
        return atan(numerator / denominator)
    }

    private fun angleHighDistal(): Double {
        val numerator = y * kb() - x * k()
        val denominator = x * kb() + y * k()

        val arctan = atan(numerator / denominator)

        return if (arctan <= 0) {
            arctan + PI
        } else {
            arctan
        }
    }

    private fun clamp(value: Double, low: Double, high: Double): Double {
        if (value < low) return low
        else if (value > high) return high
        else return value
    }

}