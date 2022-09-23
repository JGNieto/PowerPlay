package org.baylorschool.vision

import org.opencv.core.Mat
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

    var decimation: Float? = null
        set(decimation) {
            needToUpdateDecimation = true
            field = decimation
        }

    private val decimationUpdateSync = Object()
    private var needToUpdateDecimation = false

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

        detections = AprilTagDetectorJNI.runAprilTagDetectorSimple(nativeAprilTagPtr!!, greyscale, 0.0, 0.0, 0.0, 0.0, 0.0)

        synchronized(visibleAprilTagsSync) {
            visibleAprilTags = detections
        }

        return input
    }

}