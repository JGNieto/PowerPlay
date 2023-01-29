package org.baylorschool.vision

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.openftc.apriltag.AprilTagDetection
import org.openftc.apriltag.AprilTagDetectorJNI
import org.openftc.easyopencv.OpenCvPipeline

// This class is an EasyOpenCV pipeline to determine which april tags are present (which is why it
// is has "Binary" in the name - the tag is either there or not).
class AprilTagBinaryPipeline(private val telemetry: Telemetry?, private val rotate: Boolean = false): OpenCvPipeline() {

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
                AprilTagDetectorJNI.setApriltagDetectorDecimation(nativeAprilTagPtr!!, decimation)
                needToUpdateDecimation = false
            }
        }

        if (rotate) {
            Core.rotate(input, input, Core.ROTATE_180)
        }

        Imgproc.cvtColor(input, greyscale, Imgproc.COLOR_RGBA2GRAY)

        detections = AprilTagDetectorJNI.runAprilTagDetectorSimple(nativeAprilTagPtr!!, greyscale, 0.166, 578.272, 578.272, 402.145, 221.506)

        synchronized(visibleAprilTagsSync) {
            visibleAprilTags = detections
            determineTargetTag()
        }

        for (tag in visibleAprilTags) {
            Imgproc.circle(greyscale, tag.center, 50, Scalar(255.0, 0.0, 0.0, 255.0), 10)
        }

        return greyscale
    }

    // The fallback tag, which may change if a new one is found
    var fallbackTag = 1

    fun determineTargetTag(): Int {
        var goodTag = -1

        // println("VISIBLE TAGS:")

        for (tag in visibleAprilTags) {
            // println(tag.id)
            if (tag.id == 0 || tag.id == 1 || tag.id == 2) { // Tag is good
                if (goodTag != -1 && tag.id != goodTag) { // Another good tag was already found and it was different; use fallback
                    // println("Multiple good tags. ${tag.id} and $goodTag. Using fallback: $fallbackTag")
                    return fallbackTag
                } else { // First good tag found; save it
                    goodTag = tag.id
                }
            } // else: tag is bad; ignore
        }

        // println("END VISIBLE TAGS")

        if (goodTag == -1) { // No good tag was found
            // println("No good tags. Using fallback: $fallbackTag")
            return fallbackTag
        } else { // Good tag was found
            if (telemetry != null) {
                telemetry.addData("Tag found", goodTag)
                telemetry.update()
            }
            fallbackTag = goodTag
            return goodTag
        }
    }

}