package org.baylorschool.betabot

import com.arcrobotics.ftclib.command.CommandBase
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import org.baylorschool.betabot.Slides

class ManualslidesCommand(slides: Slides, manipulator: GamepadEx) : CommandBase() {
    private val slides: Slides
    private val manipulator: GamepadEx

    init {
        addRequirements(slides)
        this.slides = slides
        this.manipulator = manipulator
    }

    override fun execute() {
        //Two dpad buttons cant be pressed at the same time so we don't have to worry about that.
        val multiplier = if (manipulator.getButton(GamepadKeys.Button.X)) 0.5 else 1.0

        //Check if the up button is pressed
        if (manipulator.getButton(GamepadKeys.Button.DPAD_UP) && !slides.atUpperLimit() && !cappingMode) {
            slides.setslidesPower(0.8 * multiplier)
        } else if (manipulator.getButton(GamepadKeys.Button.DPAD_DOWN) && !slides.atLowerLimit() && !cappingMode) {
            slides.setslidesPower(-0.5 * multiplier)
        } else {
            slides.stop()
        }
    }

    companion object {
        var cappingMode = false
    }
}