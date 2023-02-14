package org.baylorschool.vision

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline
import java.util.*

class YellowJunctionPipeline(private val rotate: Boolean = false, private val telemetry: Telemetry? = null): OpenCvPipeline() {
    private val hierarchyOutput = Mat()
    private val hsvMat = Mat()
    private val thresholdMat = Mat()

    var junctionRect: Rect? = null

    override fun processFrame(input: Mat): Mat {
        val contours: List<MatOfPoint> = LinkedList()

        if (rotate) {
            Core.rotate(input, input, Core.ROTATE_180)
        }

        Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV)
        Core.inRange(hsvMat, Scalar(20.0, 100.0, 100.0), Scalar(30.0, 255.0, 255.0), thresholdMat)

        Imgproc.findContours(thresholdMat, contours, hierarchyOutput, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)

        val boundRects = arrayOfNulls<Rect>(contours.size)
        contours.forEachIndexed{i, contour -> boundRects[i] = Imgproc.boundingRect(contour) }

        Imgproc.drawContours(input, contours, -1, Scalar(255.0, 0.0, 0.0), 2)

        var largestRect: Rect? = null
        var largestRectArea = 0

        for (rect in boundRects) {
            if (rect != null) {
                val area = rect.height * rect.width
                if (area > largestRectArea) {
                    largestRect = rect
                    largestRectArea = area
                }
            }
        }

        if (largestRect != null) {
            junctionRect = largestRect
        }

        if (telemetry != null) {
            telemetry.addData("Sum", Core.sumElems(thresholdMat))
            telemetry.addData("Contours", contours.size)
            if (largestRect != null) {
                telemetry.addData("x", largestRect.x)
                telemetry.addData("y", largestRect.y)
                telemetry.addData("width", largestRect.width)
                telemetry.addData("height", largestRect.height)
            }
            telemetry.update()
        }

        for (rect in boundRects) {
            if (rect != null && rect == largestRect) {
                Imgproc.rectangle(input, rect, Scalar(0.0, 0.0, 255.0), 4)
            } else {
                Imgproc.rectangle(input, rect, Scalar(0.0, 255.0, 0.0), 2)
            }
        }

        return input
    }
}