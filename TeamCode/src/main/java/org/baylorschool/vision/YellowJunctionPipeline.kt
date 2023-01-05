package org.baylorschool.vision

import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline
import java.util.*

class YellowJunctionPipeline: OpenCvPipeline() {
    val hierarchyOutput = Mat()
    val hsvMat = Mat()
    val thresholdMat = Mat()

    override fun processFrame(input: Mat): Mat {
        val contours: List<MatOfPoint> = LinkedList()

        Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_BGR2HSV)
        Core.inRange(hsvMat, Scalar(20.0, 100.0, 100.0), Scalar(30.0, 255.0, 255.0), thresholdMat)

        Imgproc.findContours(input, contours, hierarchyOutput, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)

        val boundRects = arrayOfNulls<Rect>(contours.size)
        contours.forEachIndexed{i, contour -> boundRects[i] = Imgproc.boundingRect(contour) }

        Imgproc.drawContours(input, contours, -1, Scalar(255.0, 0.0, 0.0), 3)

        for (rect in boundRects) {
            Imgproc.rectangle(input, rect, Scalar(0.0, 255.0, 0.0), 4)
        }

        return input
    }
}