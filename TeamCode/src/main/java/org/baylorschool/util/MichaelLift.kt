package org.baylorschool.util

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.baylorschool.Globals
import org.baylorschool.util.angledevice.AngleDevice
import org.baylorschool.util.angledevice.BasicMotorAngleDevice
import org.baylorschool.util.angledevice.EmptyAngleDevice
import org.baylorschool.util.angledevice.TargetAngleDirection
import kotlin.math.*

// Bar lengths
const val a = 15.25 // in
const val b = 8.0 // in

object LiftPresets {
    val hell = LiftPosition(17.0, -5.0)
    val heavenLow = LiftPosition(-13.0, 3.5)
    val heavenMid = LiftPosition(-13.0, 9.5)
    val heavenHigh = LiftPosition(-13.0, 16.0)
}

data class LiftPosition(val x: Double, val y: Double)


class MichaelLift(opMode: OpMode) {
    /*
    Warning for the reader: many variables in this file are not meaningfully named because it is
    based on Michael's model on Desmos. Please refer to that for better understanding:
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

    var x = 0.0
        private set

    var y = 0.0
        private set

    private var needToUpdate = false

    private var previousDistalAngle = 0.0
    private var previousProximalAngle = 0.0

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

        val angleDistalRelative = (angleDistal - angleProximal)// % (2 * PI)

        motorA1.moveToAngle(clamp(angleProximal, 0.0, PI), TargetAngleDirection.ABSOLUTE)
        motorA2.moveToAngle(clamp(angleProximal, 0.0, PI), TargetAngleDirection.ABSOLUTE)
        motorB.moveToAngle(angleDistalRelative, TargetAngleDirection.ABSOLUTE)

        previousProximalAngle = angleProximal
        previousDistalAngle = angleDistal

        telemetry.addData("Angle proximal", angleProximal)
        telemetry.addData("Angle distal", angleDistal)
        telemetry.addData("Angle distal relative", angleDistalRelative)
        telemetry.update()
        needToUpdate = false
    }

    fun moveToMode(mode: LiftMode, pos: LiftPosition? = null) {
        this.liftMode = mode

        when (mode) {
            LiftMode.HIGH -> {
                previousDistalAngle = 0.0
                previousProximalAngle = 3.0 * PI / 4.0
                if (pos != null) {
                    goToPosition(pos)
                } else {
                    goToPosition(LiftPresets.heavenHigh)
                }
            }
            LiftMode.GROUND -> {
                previousDistalAngle = 0.0
                previousProximalAngle = 0.0
                if (pos != null) {
                    goToPosition(pos)
                } else {
                    goToPosition(LiftPresets.hell)
                }
            }
        }
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

    fun goToPosition(x: Double, y: Double): Boolean {
        if ((a - b).pow(2) <= x.pow(2) + y.pow(2) && x.pow(2) + y.pow(2) <= (a + b).pow(2)) {
            this.x = x
            this.y = y

            this.needToUpdate = true

            return true
        } else {
            return false
        }
    }

    fun goToPosition(pos: LiftPosition): Boolean {
        return goToPosition(pos.x, pos.y)
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

        val arctan =  atan(numerator / denominator)
        val arctan1 = arctan + PI
        val arctan2 = arctan - PI

        val dArctan = abs(arctan - previousProximalAngle)
        val dArctan1 = abs(arctan1 - previousProximalAngle)
        val dArctan2 = abs(arctan2 - previousProximalAngle)

        return if (dArctan < dArctan1 && dArctan < dArctan2) {
            arctan
        } else if (dArctan1 < dArctan && dArctan1 < dArctan2) {
            arctan1
        } else {
            arctan2
        }
    }

    private fun angleGroundDistal(): Double {
        val numerator = y * kb() - x * k()
        val denominator = x * kb() + y * k()

        val arctan = atan(numerator / denominator)
        val arctan1 = arctan + PI
        val arctan2 = arctan - PI

        val dArctan = abs(arctan - previousDistalAngle)
        val dArctan1 = abs(arctan1 - previousDistalAngle)
        val dArctan2 = abs(arctan2 - previousDistalAngle)

        return if (dArctan < dArctan1 && dArctan < dArctan2) {
            arctan
        } else if (dArctan1 < dArctan && dArctan1 < dArctan2) {
            arctan1
        } else {
            arctan2
        }
    }

    // Mode high
    private fun angleHighProximal(): Double {
        val numerator = y * kb() - x * k()
        val denominator = x * kb() + y * k()

        val arctan =  atan(numerator / denominator)
        val arctan1 = arctan + PI
        val arctan2 = arctan - PI

        val dArctan = abs(arctan - previousProximalAngle)
        val dArctan1 = abs(arctan1 - previousProximalAngle)
        val dArctan2 = abs(arctan2 - previousProximalAngle)

        return if (dArctan < dArctan1 && dArctan < dArctan2) {
            arctan
        } else if (dArctan1 < dArctan && dArctan1 < dArctan2) {
            arctan1
        } else {
            arctan2
        }
    }

    private fun angleHighDistal(): Double {
        val numerator = y * ka() - x * k()
        val denominator = x * ka() + y * k()


        val arctan = atan(numerator / denominator)
        val arctan1 = arctan + PI
        val arctan2 = arctan - PI

        val dArctan = abs(arctan - previousDistalAngle)
        val dArctan1 = abs(arctan1 - previousDistalAngle)
        val dArctan2 = abs(arctan2 - previousDistalAngle)

        return if (dArctan < dArctan1 && dArctan < dArctan2) {
            arctan
        } else if (dArctan1 < dArctan && dArctan1 < dArctan2) {
            arctan1
        } else {
            arctan2
        }
    }

    private fun clamp(value: Double, low: Double, high: Double): Double {
        return if (value < low) low
        else if (value > high) high
        else value
    }

}