package org.baylorschool.betabot

import com.acmerobotics.roadrunner.control.PIDCoefficients
import com.acmerobotics.roadrunner.control.PIDFController
import com.arcrobotics.ftclib.command.CommandBase
import org.baylorschool.betabot.Slides

class SlidePosition(slides: Slides, targetPosition: Double, tolerance: Double) :
    CommandBase() {
    private var slidesController: PIDFController? = null
    private val coefficients = PIDCoefficients(0.0, 0.0, 0.0)
    private val kStatic = 0.0
    private var tolerance = 0.0
    private val slides: Slides
    private val targetPosition: Double
    private var slidesPosition = 0.0

    constructor(slides: Slides, position: Slides.SlidesPosition) : this(slides, position.position.toDouble())
    constructor(slides: Slides, targetPosition: Double) : this(slides, targetPosition, 3.0)

    init {
        this.slides = slides
        this.tolerance = tolerance
        this.targetPosition = targetPosition
        slidesController = PIDFController(coefficients, 0.0, 0.0, kStatic)
        slidesController!!.setOutputBounds(-0.0, 0.0)
    }

    override fun initialize() {
        //once
        slides.stop()
        slidesController!!.reset()
        slidesController!!.targetPosition = targetPosition
    }

    override fun execute() {
        slidesPosition = slides.getslidesPosition()
        //Update the slides power with the controller
        slides.setslidesPower(slidesController!!.update(slidesPosition))
    }

    override fun isFinished(): Boolean {
        //End if the slides position is within the tolerance
        return Math.abs(slidesPosition - targetPosition) < tolerance
    }

    override fun end(interrupted: Boolean) {
        slides.stop()
    }
}