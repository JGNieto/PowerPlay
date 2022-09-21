package org.baylorschool.meepmeep

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.noahbres.meepmeep.MeepMeep
import com.noahbres.meepmeep.MeepMeep.Background
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder
import com.noahbres.meepmeep.roadrunner.DriveShim

object MeepMeepTesting {
    @JvmStatic
    fun main(args: Array<String>) {
        val meepMeep = MeepMeep(800)
        val ohmBot = DefaultBotBuilder(meepMeep) // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60.0, 60.0, Math.toRadians(180.0), Math.toRadians(180.0), 15.0)
                .followTrajectorySequence { drive: DriveShim ->
                    drive.trajectorySequenceBuilder(Pose2d(0.0, 0.0, 0.0))
                            //put trajectory sequence inputs here
                        .build()
                }
        meepMeep.setBackground(Background.FIELD_POWERPLAY_OFFICIAL)
            .setDarkMode(true)
            .setBackgroundAlpha(0.95f)
            .addEntity(ohmBot)
            .start()
    }
}