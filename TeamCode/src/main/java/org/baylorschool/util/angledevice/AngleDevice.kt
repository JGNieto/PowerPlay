package org.baylorschool.util.angledevice

interface AngleDevice {

    /**
     * @param angle Radians
     * @param direction Clockwise, -1. Counterclockwise: +1, Determine automatically: 0
     */
    fun moveToAngle(angle: Double, direction: Int = 0)

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