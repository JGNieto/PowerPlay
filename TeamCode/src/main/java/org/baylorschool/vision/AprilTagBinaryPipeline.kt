package org.baylorschool.vision

import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.openftc.apriltag.AprilTagDetection
import org.openftc.apriltag.AprilTagDetectorJNI
import org.openftc.easyopencv.OpenCvPipeline

// This class is an EasyOpenCV pipeline to determine which april tags are present (which is why it
// is has "Binary" in the name - the tag is either there or not).
class AprilTagBinaryPipeline: OpenCvPipeline() {

    private lateinit var greyscale: Mat
    private var nativeAprilTagPtr: Long? = null

    private var detections = ArrayList<AprilTagDetection>()

    var decimation: Float = 2f
        set(value) {
            needToUpdateDecimation = true
            field = value
        }

    private val decimationUpdateSync = Object()
    private var needToUpdateDecimation = true

    var visibleAprilTags = ArrayList<AprilTagDetection>()
    private val visibleAprilTagsSync = Object()

    override fun init(input: Mat?) {
        greyscale = Mat()
        nativeAprilTagPtr = AprilTagDetectorJNI.createApriltagDetector(AprilTagDetectorJNI.TagFamily.TAG_36h11.string, 3f, 3)
    }

    override fun processFrame(input: Mat): Mat {
        // If we have failed creating the detector using JNI, don't do anything.
        if (nativeAprilTagPtr == null || nativeAprilTagPtr == 0L) {
            println("Could not find AprilTagDetector pointer (probably AprilTagDetectorJNI.createApriltagDetector failed).")
            return input
        }

        synchronized(decimationUpdateSync) {
            if (needToUpdateDecimation) {
                AprilTagDetectorJNI.setApriltagDetectorDecimation(nativeAprilTagPtr!!, decimation!!)
                needToUpdateDecimation = false
            }
        }

        Imgproc.cvtColor(input, greyscale, Imgproc.COLOR_RGBA2GRAY)

        detections = AprilTagDetectorJNI.runAprilTagDetectorSimple(nativeAprilTagPtr!!, greyscale, 0.166, 578.272, 578.272, 402.145, 221.506)

        synchronized(visibleAprilTagsSync) {
            visibleAprilTags = detections
        }

        for (tag in visibleAprilTags) {
            Imgproc.circle(greyscale, tag.center, 50, Scalar(255.0, 0.0, 0.0, 255.0), 10)
        }

        return greyscale
    }

}