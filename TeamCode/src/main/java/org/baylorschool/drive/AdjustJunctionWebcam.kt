package org.baylorschool.drive

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.hardware.rev.Rev2mDistanceSensor
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.baylorschool.Globals
import org.baylorschool.vision.YellowJunctionPipeline
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

object AdjustJunctionWebcam {

    enum class Side {
        RIGHT,
        LEFT
    }

    fun adjustJunctionWebcam(opMode: LinearOpMode, distance: Rev2mDistanceSensor, junctionPipeline: YellowJunctionPipeline, mecanum: Mecanum, fieldSide: Side) {
        var correctionStartTime = System.currentTimeMillis()

        var speed = 0.2
        var direction = 0

        while (System.currentTimeMillis() - correctionStartTime < 3000 && opMode.opModeIsActive()) {
            mecanum.updatePoseEstimate()

            val rect = junctionPipeline.junctionRect
            if (rect == null) break

            val x = rect.x + rect.width / 2.0
            val y = rect.y + rect.height / 2.0

            val yTolerance = rect.height / 4.0

            //if (distance.getDistance(DistanceUnit.INCH) < Globals.seeingPoleThreshold && System.currentTimeMillis() - correctionStartTime > 1500) break

            val poleVisible = rect.width >= Globals.junctionWidthMinimum

            if ((!poleVisible && fieldSide == Side.RIGHT) || y < Globals.junctionYPosition - yTolerance) {
                if (direction == -1) speed *= 0.7
                direction = 1

                mecanum.setWeightedDrivePower(Pose2d(0.0, speed, 0.0))

                opMode.telemetry.addData("Direction", "Right")
            } else if ((!poleVisible && fieldSide == Side.LEFT) || y > Globals.junctionYPosition + yTolerance) {
                if (direction == 1) speed *= 0.7
                direction = -1

                mecanum.setWeightedDrivePower(Pose2d(0.0, -speed, 0.0))

                opMode.telemetry.addData("Direction", "Left")
            } else {
                break
            }

            opMode.telemetry.addData("Rect Y", rect.y)
            opMode.telemetry.addData("Y", y)
            opMode.telemetry.addData("Speed", speed)
            opMode.telemetry.update()
        }

        speed = 0.2
        direction = 0
        correctionStartTime = System.currentTimeMillis()

        while (System.currentTimeMillis() - correctionStartTime < 3000 && opMode.opModeIsActive()) {
            mecanum.updatePoseEstimate()
            val dist = distance.getDistance(DistanceUnit.INCH)

            if (dist > Globals.seeingPoleThreshold) {
                direction = 0
            } else if (dist > Globals.optimalReleaseDistance + Globals.optimalReleaseDistanceTolerance) {
                if (direction == -1) speed *= 0.7
                direction = 1
            } else if (dist < Globals.optimalReleaseDistance - Globals.optimalReleaseDistanceTolerance) {
                if (direction == 1) speed *= 0.7
                direction = -1
            } else {
                opMode.telemetry.addData("Distance", dist)
                opMode.telemetry.addData("Speed", speed)
                opMode.telemetry.update()
                break
            }

            mecanum.setWeightedDrivePower(Pose2d(speed * direction * -1, 0.0, 0.0))
            opMode.telemetry.addData("Distance", dist)
            opMode.telemetry.addData("Speed", speed)
            opMode.telemetry.update()
        }

        mecanum.setDrivePower(Pose2d(0.0, 0.0, 0.0))
    }
}