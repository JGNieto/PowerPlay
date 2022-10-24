package org.baylorschool.util.angledevice

enum class TargetAngleDirection {
    CLOCKWISE, COUNTERCLOCKWISE, CLOSEST, ABSOLUTE
}

interface AngleDevice {
    /**
     * @param angle Radians
     * @param direction
     */
    fun moveToAngle(angle: Double, direction: TargetAngleDirection = TargetAngleDirection.CLOSEST)

    /**
     * This function is to be used when the angle of the device is known, so that relative calculations
     * can be based on this information. Some devices may be unable to use this information and ignore it.
     * @param angle Radians: Known angle of the device
     */
    fun reset(angle: Double)

    fun getPosition(): Double

    fun init()

    fun stop()

    fun cleanup()
}