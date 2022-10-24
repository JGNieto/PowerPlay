package org.baylorschool.util.angledevice

class EmptyAngleDevice: AngleDevice {
    override fun moveToAngle(angle: Double, direction: TargetAngleDirection) {
    }

    override fun reset(angle: Double) {
    }

    override fun getPosition(): Double {
        return 0.0
    }

    override fun init() {
    }

    override fun stop() {
    }

    override fun cleanup() {
    }
}