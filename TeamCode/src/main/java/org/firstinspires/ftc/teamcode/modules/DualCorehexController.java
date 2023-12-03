package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.math.MathContext;

public class DualCorehexController {
    // coordinate system: 0deg is arm vertical, positive is arm towards back of robot
    // negative is arm towards intake
    // also: using left encoder b/c right encoder is out of spec
    static final int MIN_TICKS = 0;
    static final int MAX_TICKS = 165;
    static final double DEGREES_PER_TICK = 360/288;
    static final int VERTICAL_TICKS = 110;
    static final double INTAKE_DEGREES = ticksToAngle(MIN_TICKS);
    static final double MAX_DEGREES = ticksToAngle(MAX_TICKS);
    static final double ARM_MASS = 2.01; // kg
    static final double ARM_CG_DIST = 0.014; // m
    static final double ARM_GRAVITY_FORCE =  ARM_MASS * 9.81; // N
    public static double armTorqueDueToGravity(double angle){
        return ARM_GRAVITY_FORCE * ARM_CG_DIST * Math.sin(Math.toRadians(angle)); // Nm
    }

    public static double ticksToAngle(double ticks){
        return (ticks - VERTICAL_TICKS) * DEGREES_PER_TICK;
    }
    public static double angleToTicks(double angle){
        return angle/DEGREES_PER_TICK + VERTICAL_TICKS;
    }
    public double correctTorqueForGravity(double inputTorque, double currentAngle){
        return inputTorque + armTorqueDueToGravity(currentAngle);
    }
    Telemetry telemetry;
    double botHeading, lastError, integralSum, oldAngleTarget;
    // rotation PID constants
    double kP = .01;
    double kI = 0;
    double kD = 0;
    ElapsedTime pidTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    public double onePIDCycle(double targetAng, double currAng){
        double error;
        double derivative;
        double outTorque;

        double oldAng = currAng;

        error = targetAng - currAng;

        derivative = (error - lastError) / pidTimer.milliseconds();
        integralSum = integralSum + (error * pidTimer.time());

        if(integralSum > 2000){
            integralSum = 2000;
        }
        if(integralSum < -2000){
            integralSum = -2000;
        }

        outTorque = (kP * error) + (kI * integralSum) + (kD * derivative);
        outTorque = correctTorqueForGravity(outTorque, currAng);

        telemetry.addData("arm target: ", "%.2f", targetAng);
//        telemetry.addData("non-normalized ang: ", "%.2f", oldAng);
        telemetry.addData("arm current: ", "%.2f", currAng);
        telemetry.addData("arm gravity torque: ", "%.2f", armTorqueDueToGravity(currAng));
//        telemetry.addData("error: ", "%.2f", error);
//        telemetry.addData("i: ", "%.2f", integralSum);
//        telemetry.addData("out: ", "%.2f", out);
        telemetry.addData("Arm controller frequency: ", Math.round(1000/pidTimer.time()));
        telemetry.update();

        lastError = error;

        pidTimer.reset();

        double outPower ;
        return outTorque;
    }
}
