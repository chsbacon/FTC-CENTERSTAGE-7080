package org.firstinspires.ftc.teamcode.modules;

import static org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit.DEGREES;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.checkerframework.checker.units.qual.Angle;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;

public class DriveController {
    private Robot2023 robot;
    private MecanumDrive drive;
    private Telemetry telemetry;
    double botHeading, lastError, integralSum, oldAngleTarget;
    // rotation PID constants
    double kP = .015;
    double kI = 0;
    double kD = 0;
    ElapsedTime pidTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    public void onOpmodeInit(Robot2023 robot, MecanumDrive drive, Telemetry telemetry){
        this.robot = robot;
        this.drive = drive;
        this.telemetry = telemetry;
    }
    public double oneRotationPID(double targetAng, double currAng){
        Orientation currOrient;
        double error;
        double derivative;
        double out;

        // convert radians to degrees
        targetAng = Math.toDegrees(targetAng);
        currAng = Math.toDegrees(currAng);
        double oldAng = currAng;

        // error is a bit weird because -180 and 180 are the same
        // first, make sure heading is between -180 and 180
        currAng = (currAng > 180) ? currAng - 360 : currAng;
        // make a guess at error
        error = targetAng - currAng;
        // if error is more or less than pi there's a quicker way to get where we want to go
        // this is probably a mudulo but i don't trust the negatives to work properly
        if(error > 180){
            error -= 360;
        } else if (error < -180){
            error += 360;
        }

        derivative = (error - lastError) / pidTimer.milliseconds();
        integralSum = integralSum + (error * pidTimer.time());

        if(integralSum > 2000){
            integralSum = 2000;
        }
        if(integralSum < -2000){
            integralSum = -2000;
        }

        out = (kP * error) + (kI * integralSum) + (kD * derivative);
        out = Math.min(Math.max(out, -.5), .5); // clamp to -.5 and .5

        telemetry.addData("yaw target: ", "%.2f", targetAng);
//        telemetry.addData("non-normalized ang: ", "%.2f", oldAng);
        telemetry.addData("yaw current: ", "%.2f", currAng);
//        telemetry.addData("error: ", "%.2f", error);
//        telemetry.addData("i: ", "%.2f", integralSum);
//        telemetry.addData("out: ", "%.2f", out);
        telemetry.addData("Yaw controller frequency: ", Math.round(1000/pidTimer.time()));
        telemetry.update();

        lastError = error;

        pidTimer.reset();
        return out;
    }
    public void driveMecanum(double xPower, double yPower, double targetRad, boolean shouldDriveNewAngle){
        YawPitchRollAngles angles =  drive.imu.getRobotYawPitchRollAngles();
        botHeading = -angles.getYaw(AngleUnit.RADIANS);

        double rotXPower = xPower * Math.cos(botHeading) - yPower * Math.sin(botHeading);
        double rotYPower = xPower * Math.sin(botHeading) + yPower * Math.cos(botHeading);

        double anglePower;
        if(shouldDriveNewAngle){
            anglePower = oneRotationPID(targetRad, botHeading);
            oldAngleTarget = targetRad;
        } else {
            anglePower = oneRotationPID(oldAngleTarget, botHeading);
        }

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(rotYPower) + Math.abs(rotXPower) + Math.abs(anglePower), 1);

        double frontLeftPower = (rotYPower + rotXPower + anglePower) / denominator;
        double backLeftPower = (rotYPower - rotXPower + anglePower) / denominator;
        double frontRightPower = (rotYPower - rotXPower - anglePower) / denominator;
        double backRightPower = (rotYPower + rotXPower - anglePower) / denominator;

        drive.leftFront.setPower(frontLeftPower);
        drive.leftBack.setPower(backLeftPower);
        drive.rightFront.setPower(frontRightPower);
        drive.rightBack.setPower(backRightPower);

    }
    public void doLoop(Gamepad gamepad1, Gamepad gamepad2){
        double x_vel = gamepad1.left_stick_x;
        double y_vel = -gamepad1.left_stick_y;
        if(gamepad1.left_trigger >= 0.5){
            x_vel *= 0.25;
            y_vel *= 0.25;
        }
        if(gamepad1.right_trigger <= 0.5 && gamepad1.left_trigger <= 0.5){
            x_vel *= 0.5;
            y_vel *= 0.5;
        }
        double angle_x_component = gamepad1.right_stick_x;
        double angle_y_component = -gamepad1.right_stick_y;
        double targetAngle = Math.atan2(gamepad1.right_stick_x, -gamepad1.right_stick_y);
        // x and y deadzone
        x_vel = (Math.abs(x_vel) < .15) ? 0 : x_vel;
        y_vel = (Math.abs(y_vel) < .15) ? 0 : y_vel;
        // angle deadzone, also partially handled in driveMecanum
        double angle_magnitude = Math.sqrt(Math.pow(angle_x_component, 2) + Math.pow(angle_y_component, 2));
        boolean shouldDriveNewAngle = angle_magnitude > .2;
        // gamepad x and b allow for fine angle adjustments
        // holding fast mode (right trigger) makes angle adjustments faster
        double angleAdjust = (gamepad1.right_trigger >= 0.5) ? .1 : .02;
        if(gamepad1.x && !shouldDriveNewAngle){
            targetAngle = oldAngleTarget - angleAdjust;
            targetAngle = AngleUnit.normalizeDegrees(targetAngle);
            shouldDriveNewAngle = true;
        }
        if(gamepad1.b && !shouldDriveNewAngle){
            targetAngle = oldAngleTarget + angleAdjust;
            targetAngle = AngleUnit.normalizeDegrees(targetAngle);
            shouldDriveNewAngle = true;
        }
        if(gamepad1.back){
            drive.imu.resetYaw();
            targetAngle = 0; // don't go crazy on yaw reset
            shouldDriveNewAngle = true;
        }
//        telemetry.addData("X final", x_vel);
//        telemetry.addData("Y final", y_vel);
//        telemetry.addData("Angle final", targetAngle);
//        telemetry.addData("SDNAngle", shouldDriveNewAngle);
//        telemetry.addData("Current heading", botHeading);
//        telemetry.update();

        driveMecanum(x_vel, y_vel, targetAngle, shouldDriveNewAngle);
    }
}
