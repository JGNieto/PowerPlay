package org.baylorschool.vision

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.baylorschool.Globals
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline
import org.openftc.easyopencv.OpenCvWebcam

object CameraUtil {

    fun openWebcam(opMode: OpMode, pipeline: OpenCvPipeline, stream: Boolean = false): OpenCvWebcam {
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
                    WebcamName::class.java, "Webcam 1"
                ), cameraMonitorViewId
            )
        } else {
            webcam = OpenCvCameraFactory.getInstance().createWebcam(
                opMode.hardwareMap.get(
                    WebcamName::class.java, "Webcam 1"
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