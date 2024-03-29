package org.baylorschool.vision

import com.acmerobotics.dashboard.FtcDashboard
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.baylorschool.Globals
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline
import org.openftc.easyopencv.OpenCvWebcam

object CameraUtil {

    fun openWebcam(opMode: OpMode, pipeline: OpenCvPipeline, stream: Boolean = false, name: String = Globals.webcamRear): OpenCvWebcam {
        val webcam: OpenCvWebcam?
        if (stream) {
            val cameraMonitorViewId =
                opMode.hardwareMap.appContext.resources.getIdentifier(
                    "cameraMonitorViewId",
                    "id",
                    opMode.hardwareMap.appContext.packageName
                )
            webcam = OpenCvCameraFactory.getInstance().createWebcam(
                opMode.hardwareMap.get(
                    WebcamName::class.java, name
                ), cameraMonitorViewId
            )
        } else {
            webcam = OpenCvCameraFactory.getInstance().createWebcam(
                opMode.hardwareMap.get(
                    WebcamName::class.java, name
                )
            )
        }


        webcam.setMillisecondsPermissionTimeout(2500)
        webcam.openCameraDeviceAsync(object : AsyncCameraOpenListener {
            override fun onOpened() {
                webcam.setPipeline(pipeline)
                webcam.startStreaming(
                    Globals.screenWidth,
                    Globals.screenHeight,
                    OpenCvCameraRotation.UPRIGHT
                )
            }

            override fun onError(errorCode: Int) {
                println("There was an error opening the camera. Error code $errorCode")
            }
        })

        //FtcDashboard.getInstance().startCameraStream(webcam, 0.0)

        return webcam
    }

    fun stop(webcam: OpenCvWebcam) {
        try {
            webcam.stopStreaming()
            webcam.closeCameraDevice()
        } catch (_: Exception) {
        }
    }

}