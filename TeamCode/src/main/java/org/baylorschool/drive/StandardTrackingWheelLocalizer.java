package org.baylorschool.drive;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.baylorschool.Globals;
import org.baylorschool.util.Encoder;

import java.util.Arrays;
import java.util.List;

/*
 * Sample tracking wheel localizer implementation assuming the standard configuration:
 *
 *    /--------------\
 *    |     ____     |
 *    |     ----     |
 *    | ||        || |
 *    | ||        || |
 *    |              |
 *    |              |
 *    \--------------/
 *
 */
@Config
public class StandardTrackingWheelLocalizer extends ThreeTrackingWheelLocalizer {
    public static double TICKS_PER_REV = 8192;
    public static double WHEEL_RADIUS = 1.496 / 2.0; // in
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (encoder) speed

    public static double LATERAL_DISTANCE = 15.3125; // in; distance between the left and right wheels
    public static double FORWARD_OFFSET = -6.656; // in; offset of the lateral wheel

//    public static double X_MULTIPLIER = 90.0 / 90.7427;
//    public static double Y_MULTIPLIER = 90.0 / 91.5866;
    public static double LEFT_MULTIPLIER = 156874.0 / 157683.0;
    public static double RIGHT_MULTIPLIER = 156874.0 / 157916.7778;
    public static double CENTER_MULTIPLIER = 156874.0 / 158023.0;

    private Encoder leftEncoder, rightEncoder, frontEncoder;

    public StandardTrackingWheelLocalizer(HardwareMap hardwareMap) {
        super(Arrays.asList(
                new Pose2d(0, LATERAL_DISTANCE / 2, 0), // left
                new Pose2d(0, -LATERAL_DISTANCE / 2, 0), // right
                new Pose2d(FORWARD_OFFSET, 0, Math.toRadians(90)) // front
        ));

        leftEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, Globals.leftEncoder));
        rightEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, Globals.rightEncoder));
        frontEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, Globals.frontEncoder));

        leftEncoder.setDirection(Encoder.Direction.REVERSE);
        rightEncoder.setDirection(Encoder.Direction.FORWARD);
        frontEncoder.setDirection(Encoder.Direction.REVERSE);
    }

    public static double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
                encoderTicksToInches(leftEncoder.getCurrentPosition()) * LEFT_MULTIPLIER,
                encoderTicksToInches(rightEncoder.getCurrentPosition()) * RIGHT_MULTIPLIER,
                encoderTicksToInches(frontEncoder.getCurrentPosition()) * CENTER_MULTIPLIER
        );
    }

    @NonNull
    @Override
    public List<Double> getWheelVelocities() {
        // TODO: If your encoder velocity can exceed 32767 counts / second (such as the REV Through Bore and other
        //  competing magnetic encoders), change Encoder.getRawVelocity() to Encoder.getCorrectedVelocity() to enable a
        //  compensation method

        return Arrays.asList(
                encoderTicksToInches(leftEncoder.getCorrectedVelocity()),
                encoderTicksToInches(rightEncoder.getCorrectedVelocity()),
                encoderTicksToInches(frontEncoder.getCorrectedVelocity())
        );
    }
}
