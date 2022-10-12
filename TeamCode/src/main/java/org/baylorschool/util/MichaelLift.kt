package org.baylorschool.util

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.baylorschool.Globals
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
    based on Michael's model on Desmos. Please refer to that for better understanding:
    https://www.desmos.com/calculator/3p3siwof5a
     */

    enum class LiftMode {
        HIGH, GROUND
    }

    private var motorA1: DcMotor // Proximal 1
    private var motorA2: DcMotor // Proximal 2
    private var motorB: DcMotor  // Distal

    init {
        motorA1 = opMode.hardwareMap.get(DcMotorEx::class.java, Globals.liftProximalA)
        motorA2 = opMode.hardwareMap.get(DcMotorEx::class.java, Globals.liftProximalB)
        motorB = opMode.hardwareMap.get(DcMotorEx::class.java, Globals.liftDistal)
    }

    private var x = 0.0
    private var y = 0.0


    fun changeMode() {

    }

    fun stop() {

    }

    // Michael's chicken scratch translated into pristine code below
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
    private fun angleGroundA(): Double {
        val numerator = y * ka() + x * k()
        val denominator = x * ka() - y * k()
        return atan(numerator / denominator)
    }

    private fun angleGroundB(): Double {
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
    private fun angleHighA(): Double {
        val numerator = y * ka() - x * k()
        val denominator = x * ka() + y * k()
        return atan(numerator / denominator)
    }

    private fun angleHighB(): Double {
        val numerator = y * kb() - x * k()
        val denominator = x * kb() + y * k()

        val arctan = atan(numerator / denominator)

        return if (arctan <= 0) {
            arctan + PI
        } else {
            arctan
        }
    }

}