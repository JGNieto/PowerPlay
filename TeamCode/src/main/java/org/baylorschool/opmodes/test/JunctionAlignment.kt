package org.baylorschool.opmodes.test

import com.qualcomm.hardware.rev.Rev2mDistanceSensor
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.Globals
import org.baylorschool.drive.AdjustJunctionWebcam
import org.baylorschool.drive.Mecanum
import org.baylorschool.vision.CameraUtil
import org.baylorschool.vision.YellowJunctionPipeline

@Autonomous(name = "JunctionAligmentTest", group = "Test")
class JunctionAlignment: LinearOpMode() {

    override fun runOpMode() {
        val junctionPipeline = YellowJunctionPipeline(Globals.webcamRearRotate, telemetry)
        val webcam = CameraUtil.openWebcam(this, junctionPipeline, true, Globals.webcamRear)
        val mecanum = Mecanum(hardwareMap)
        val distance = hardwareMap.get(Rev2mDistanceSensor::class.java, Globals.distanceSensor)

        waitForStart()

        AdjustJunctionWebcam.adjustJunctionWebcam(this, distance, junctionPipeline, mecanum, AdjustJunctionWebcam.Side.RIGHT)
    }

}