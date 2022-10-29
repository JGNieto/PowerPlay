package org.baylorschool.util

import com.acmerobotics.dashboard.FtcDashboard
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

    enum class SyncMode {
        NONE, DISTAL_FIRST, PROXIMAL_FIRST
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

    private var syncInitA = 0.0
    private var syncInitA1 = 0.0
    private var syncInitA2 = 0.0
    private var syncInitB = 0.0
    private var syncMode = SyncMode.NONE

    private var angleProximal = 0.0
    private var angleDistal = 0.0

    private var previousDistalAngle = 0.0
    private var previousProximalAngle = 0.0

    private var liftMode: LiftMode = LiftMode.GROUND

    fun iteration() {
        val telemetry = FtcDashboard.getInstance().telemetry
        if (!needToUpdate && syncMode == SyncMode.NONE) return

        var targetAngleProximal = clamp(angleProximal, 0.0, PI)
        var targetAngleDistal = angleDistal - targetAngleProximal

        if (syncMode != SyncMode.NONE) {
            val dPosA1 = motorA1.getPosition() - syncInitA1
            val dPosA2 = if (motorA2 !is EmptyAngleDevice) motorA2.getPosition() - syncInitA2 else dPosA1

            val dPosA = (dPosA1 + dPosA2) / 2.0
            val dPosB = motorB.getPosition()

            val progressA = dPosA / (targetAngleProximal - syncInitA)
            val progressB = dPosB / targetAngleDistal

            when (syncMode) {
                SyncMode.DISTAL_FIRST -> {
                    telemetry.addData("Progress B", progressB)
                    targetAngleProximal = scaleMovement(0.4, 0.8, syncInitA, targetAngleProximal, progressB)
                }
                SyncMode.PROXIMAL_FIRST -> {
                    telemetry.addData("Progress A", progressA)
                    targetAngleDistal = scaleMovement(0.4, 0.8, syncInitB, targetAngleDistal, progressA)
                }
                else -> {}
            }

        }

        motorA1.moveToAngle(targetAngleProximal, TargetAngleDirection.ABSOLUTE)
        motorA2.moveToAngle(targetAngleProximal, TargetAngleDirection.ABSOLUTE)
        motorB.moveToAngle(targetAngleDistal, TargetAngleDirection.ABSOLUTE)

        telemetry.addData("Angle proximal", angleProximal)
        telemetry.addData("Angle distal", angleDistal)
        telemetry.addData("Target angle distal", targetAngleDistal)
        telemetry.addData("Target angle proximal", targetAngleProximal)
        telemetry.addData("Actual proximal", motorA1.getPosition())
        telemetry.addData("Actual distal", motorB.getPosition())
        telemetry.addData("Sync mode", syncMode)
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
                    goToPosition(pos, SyncMode.PROXIMAL_FIRST)
                } else {
                    goToPosition(LiftPresets.heavenHigh, SyncMode.PROXIMAL_FIRST)
                }
            }
            LiftMode.GROUND -> {
                previousDistalAngle = 0.0
                previousProximalAngle = 0.0
                if (pos != null) {
                    goToPosition(pos, SyncMode.DISTAL_FIRST)
                } else {
                    goToPosition(LiftPresets.hell, SyncMode.DISTAL_FIRST)
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

    fun goToPosition(x: Double, y: Double, syncMove: SyncMode = SyncMode.NONE): Boolean {
        if ((a - b).pow(2) <= x.pow(2) + y.pow(2) && x.pow(2) + y.pow(2) <= (a + b).pow(2)) {
            this.x = x
            this.y = y
            this.syncMode = syncMove

            angleProximal = when (liftMode) {
                LiftMode.HIGH -> angleHighProximal()
                LiftMode.GROUND -> angleGroundProximal()
            }

            angleDistal = when (liftMode) {
                LiftMode.HIGH -> angleHighDistal()
                LiftMode.GROUND -> angleGroundDistal()
            }

            previousProximalAngle = angleProximal
            previousDistalAngle = angleDistal

            if (syncMove != SyncMode.NONE) {
                this.syncInitA1 = this.motorA1.getPosition()
                this.syncInitA2 = this.motorA2.getPosition()
                this.syncInitB = this.motorB.getPosition()

                if (this.motorA2 is EmptyAngleDevice)
                    this.syncInitA = this.syncInitA1
                else
                    this.syncInitA = (this.syncInitA1 + this.syncInitA2) / 2.0
            }

            this.needToUpdate = true

            return true
        } else {
            return false
        }
    }

    fun goToPosition(pos: LiftPosition, syncMove: SyncMode = SyncMode.NONE): Boolean {
        return goToPosition(pos.x, pos.y, syncMove)
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
        val numerator = y * ka() - x * k()
        val denominator = x * ka() + y * k()

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
        val numerator = y * kb() + x * k()
        val denominator = x * kb() - y * k()

        val arctan = atan(numerator / denominator)
        val arctan1 = arctan + PI
        val arctan2 = arctan - PI

        val dArctan = abs(arctan - previousDistalAngle)
        val dArctan1 = abs(arctan1 - previousDistalAngle)
        val dArctan2 = abs(arctan2 - previousDistalAngle)

        return if (dArctan < dArctan1 && dArctan < dArctan2) {
            arctan - PI
        } else if (dArctan1 < dArctan && dArctan1 < dArctan2) {
            arctan1 - PI
        } else {
            arctan2 - PI
        }
    }

    private fun clamp(value: Double, low: Double, high: Double): Double {
        return if (value < low) low
        else if (value > high) high
        else value
    }

    /**
     * Scales the instructions given to one motor to be in function of the other's actual movement,
     * lagging behind initially and, once a threshold is reached, going to the final position.
     * @param lag Minimum value of progress before this method returns values other than zero.
     * @param threshold Maximum value of progress after which this method will return target.
     * @param initial Initial position of the motor.
     * @param target Target position of the motor.
     * @param progress Progress of the other motor (between 0 and 1).
     */
    private fun scaleMovement(lag: Double, threshold: Double, initial: Double, target: Double, progress: Double): Double {
        if (progress > threshold) return target
        if (progress < lag) return initial

        /*
        Use function y = mx + a, where:
         y is the progress between 0 and 1
         x is progress
         a = -lag
         m = (1 - a) / threshold
         */
        val m = (1.0 + lag) / threshold
        val y = m * progress - lag

        return y * (target - initial) + initial
    }

}