package org.firstinspires.ftc.teamcode.modules;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ArmController {
    public enum ArmLocation {
        Intake,// picking up first pixel
        GeneralProtected, // not in intake but still in protected area
        Hang, // straight vertical +- a few degrees
        Score // between hang and MAX
    }
    private Robot2023 robot;
    private Telemetry telemetry;
    private final double LEFT_CLAW_OPEN = 0.2;
    private final double LEFT_CLAW_CLOSED = 0;
    private final double RIGHT_CLAW_OPEN = 0.8;
    private final double RIGHT_CLAW_CLOSED = 1;
    private final double WRIST_GROUND_POS = 0.17;
    private final double WRIST_PARALLEL_POS = 0;
    private final double WRIST_DEGREES_PER_POS = 258; // empirical data
    public final int LINEAR_MIN = 0;
    public final int LINEAR_MAX = 2500;
    public final int LINEAR_INTAKE2 = 200;
    // these are the left encoder numbers; the right encoder is not up to spec
    public final int FOREARM_MIN = 0;
    public final int FOREARM_PASSING = FOREARM_MIN;
    public final int FOREARM_MAX = 185;
    public static final int FOREARM_VERTICAL = 110;
    public final int FOREARM_PARALELL = 150;
    static final double DEGREES_PER_COREHEX_TICK = 360.0/288.0;
    private ElapsedTime loopTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

    // expects to be initted with arm in intake
    PIDController forearmPID = new PIDController(0.005, 0.0007, 0.0013); // old: 0.004, 0.0007, 0.0013; old old: 0.004, 0.01, 0.0007
    double gravityGain = 0.1; // old: 0.1; old old: 0.1
    double frictionGain = 0.1; // old: 0.11; old old: 0.1
    final double cgOffset = 0; // degrees off arm
    private ArmLocation armTargetLocation = ArmLocation.Intake;
    ActionExecutor actionExecutor = new ActionExecutor();
    public void onOpmodeInit(Robot2023 robot, Telemetry telemetry) {
        this.robot = robot;
        this.telemetry = telemetry;
        robot.linearExtenderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.linearExtenderMotor.setTargetPosition(0);
        robot.linearExtenderMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.linearExtenderMotor.setPower(1);
        for(DcMotorEx motor: new DcMotorEx[]{robot.leftForearmMotor, robot.rightForearmMotor}){
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setTargetPosition(0);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setPower(0);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        forearmPID.setTolerance(4);
        forearmPID.setIntegrationBounds(-1000,1000);
    }
    public void openLeftClaw(){
        robot.leftClawServo.setPosition(LEFT_CLAW_OPEN);
    }
    public void closeLeftClaw(){
        robot.leftClawServo.setPosition(LEFT_CLAW_CLOSED);
    }
    public void openRightClaw(){
        robot.rightClawServo.setPosition(RIGHT_CLAW_OPEN);
    }
    public void closeRightClaw(){
        robot.rightClawServo.setPosition(RIGHT_CLAW_CLOSED);
    }
    public void enterIntake(){

    }
    public void leaveIntake(){

    }

    public Action openLeftClawAction(){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                openLeftClaw();
                // telemetryPacket.addLine("Claw opened");
                return false;
            }
        };
    }
    public Action closeLeftClawAction(){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                closeLeftClaw();
                // telemetryPacket.addLine("Claw closed");
                return false;
            }
        };
    }
    public Action openRightClawAction(){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                openRightClaw();
                // telemetryPacket.addLine("Claw opened");
                return false;
            }
        };
    }
    public Action closeRightClawAction(){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                closeRightClaw();
                // telemetryPacket.addLine("Claw closed");
                return false;
            }
        };
    }
    public void doLoop(Gamepad gamepad1, Gamepad gamepad2){
        if (gamepad2.left_trigger > 0.5){
            //telemetry.log().add("HI close claw");
            closeLeftClaw();
        }
        if (gamepad2.right_trigger > 0.5){
            //telemetry.log().add("HI close claw");
            closeRightClaw();
        }
        if (gamepad2.left_bumper){
            //telemetry.log().add("HI open claw");
            openLeftClaw();
        }
        if (gamepad2.right_bumper){
            //telemetry.log().add("HI open claw");
            openRightClaw();
        }
        ArmLocation armTargetLocation = getArmLocationFromPositions((int)forearmPID.getSetPoint(), robot.linearExtenderMotor.getTargetPosition());
        ArmLocation armCurrentLocation = getArmLocationFromPositions(robot.leftForearmMotor.getCurrentPosition(), robot.linearExtenderMotor.getCurrentPosition());

        doManualLinear(gamepad2, gamepad2.start && gamepad2.left_bumper);
        doManualArm(gamepad2, gamepad2.start && gamepad2.left_bumper);

        if(gamepad2.a){
            armTargetLocation = ArmLocation.Intake;
                actionExecutor.setAction(new ParallelAction(
                        //goToLinearHeightAction(LINEAR_MAX),
                        goToArmPositionAction(FOREARM_MIN),
                        //new SleepAction(0.5),
                        goToLinearHeightAction(LINEAR_MIN)
                ));
        }
        if(gamepad2.x){
            armTargetLocation = ArmLocation.Hang;
            actionExecutor.setAction(new ParallelAction(
                    goToLinearHeightAction(LINEAR_MAX),
                    goToArmPositionAction(FOREARM_VERTICAL)
            ));
        }
        if(gamepad2.b){
            armTargetLocation = ArmLocation.Score;
            actionExecutor.setAction(new ParallelAction(
                    //goToLinearHeightAction(LINEAR_MAX),
                    goToArmPositionAction(175),
                    goToLinearHeightAction(LINEAR_MIN)
            ));
        }
        if(gamepad2.y){
            armTargetLocation = ArmLocation.Score;
            actionExecutor.setAction(new ParallelAction(
                    //goToLinearHeightAction(LINEAR_MAX),
                    goToArmPositionAction(175),
                    goToLinearHeightAction(LINEAR_MAX)
            ));
        }
        if(gamepad2.back && gamepad2.start){
            for(DcMotorEx motor: new DcMotorEx[]{robot.leftForearmMotor, robot.rightForearmMotor}){
                motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
            forearmPID.reset();
            forearmPID.setSetPoint(0);
            robot.linearExtenderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            robot.linearExtenderMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.linearExtenderMotor.setTargetPosition(0);
        }

        doArmControl();
        if(gamepad2.back){
            for(DcMotorEx motor: new DcMotorEx[]{robot.leftForearmMotor, robot.rightForearmMotor, robot.linearExtenderMotor}){
                motor.setPower(0);
            }
        } else {
            robot.linearExtenderMotor.setPower(1);
        }

        actionExecutor.doLoop();
        telemetry.addData("Forearm at target: ", forearmPID.atSetPoint());
        telemetry.addData("forearmC: ", robot.leftForearmMotor.getCurrentPosition());
        telemetry.addData("forearmCD: ", ticksToAngle(robot.leftForearmMotor.getCurrentPosition()));
        telemetry.addData("linearC: ", robot.linearExtenderMotor.getCurrentPosition());
        telemetry.addData("forearmT: ", forearmPID.getSetPoint());
        telemetry.addData("linearT: ", robot.linearExtenderMotor.getTargetPosition());
        telemetry.addData("forearmP: ", robot.leftForearmMotor.getPower());
        telemetry.addData("f compensation: ", gravityGain * -Math.sin(Math.toRadians(ticksToAngle(robot.leftForearmMotor.getCurrentPosition())+cgOffset)));
        telemetry.addData("current zone:", armCurrentLocation.toString());
        telemetry.addData("target zone: ", armTargetLocation.toString());
        telemetry.addData("arm loop freq: ", Math.round(1/loopTimer.seconds()));
        telemetry.addData("aciton active: ", actionExecutor.actionIsActive());
        //telemetry.update();
        loopTimer.reset();
    }

    private void doArmControl() {
        double forearmPower = forearmPID.calculate(robot.leftForearmMotor.getCurrentPosition());
        // gravity compensation is proportional to the sine of the angle, because circle physics
        forearmPower += gravityGain * -Math.sin(Math.toRadians(ticksToAngle(robot.leftForearmMotor.getCurrentPosition())+cgOffset)); // gravity gain points "up"
        forearmPower += frictionGain * Math.signum(forearmPower); // friction gain always points in the direction we're going
        if(forearmPID.atSetPoint()) {
            forearmPower = 0;
            forearmPID.clearTotalError();
        }
        forearmPower = clamp(forearmPower, -1, 1);
        setForearmMotorPowers(forearmPower);

        robot.clawWristServo.setPosition(getServoPositionForArmAngle(ticksToAngle(robot.leftForearmMotor.getCurrentPosition()))); // put wrist servo at correct angle
    }
    public static double ticksToAngle(double ticks){
        return (ticks - FOREARM_VERTICAL) * DEGREES_PER_COREHEX_TICK;
    }

    public boolean locationIsProtected(ArmLocation armLocation){
        return armLocation == ArmLocation.GeneralProtected || armLocation == ArmLocation.Intake;
    }
    public ArmLocation getArmLocationFromPositions(int forearmPosition, int linearPosition){
        final int FUDGE_FACTOR = 10;
        if (forearmPosition < FOREARM_MIN + FUDGE_FACTOR){
            return ArmLocation.Intake;
        } else if (forearmPosition < FOREARM_VERTICAL - 5) {
            return ArmLocation.GeneralProtected;
        } else if (forearmPosition < FOREARM_VERTICAL + 5){
            return ArmLocation.Hang;
        } else {
            return ArmLocation.Score;
        }
    }

    private void setForearmMotorPowers(double power){
        // to ensure we don't drive the motors against each other, always
        // use this function to set target position
        robot.rightForearmMotor.setPower(power);
        robot.leftForearmMotor.setPower(power);
    }
    public void doManualArm(Gamepad gamepad2, boolean allowPastEndstops){
        double newTargetPosition = forearmPID.getSetPoint();
        if (Math.abs(gamepad2.right_stick_y) > .15){
            newTargetPosition += -5 * gamepad2.right_stick_y; // negative 40 because y is reversed
        }
        if(!allowPastEndstops) {
            newTargetPosition = clamp(newTargetPosition, FOREARM_MIN, FOREARM_MAX);
        }
        forearmPID.setSetPoint(newTargetPosition);
    }
    public void doManualLinear(Gamepad gamepad2, boolean allowPastEndstops){
        int newTargetPosition = robot.linearExtenderMotor.getTargetPosition();
        if (Math.abs(gamepad2.left_stick_y) > .15){
            newTargetPosition += -40 * gamepad2.left_stick_y; // negative 40 because y is reversed
        }
        if(!allowPastEndstops) {
            newTargetPosition = (int) clamp(newTargetPosition, LINEAR_MIN, LINEAR_MAX);
        }
        robot.linearExtenderMotor.setTargetPosition(newTargetPosition);
    }
    public Action goToLinearHeightAction(int targetPosition){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                robot.linearExtenderMotor.setTargetPosition(targetPosition);
                if(!robot.linearExtenderMotor.isBusy()){
                    telemetry.log().add("Finished goToLinearHeightAction!");
                }
                return robot.linearExtenderMotor.isBusy();
            }
        };
    }
    public Action goToArmPositionAction(int targetPosition){
        final double TICKS_PER_SEC = 100;
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                forearmPID.setSetPoint(targetPosition);
                return !forearmPID.atSetPoint();
            }
        };
    }

    private double getServoPositionForArmAngle(double armAngle) {
        if(armAngle < 10){
            // be parallel to ground if it's possible we're approaching it at speed
            return WRIST_GROUND_POS;
        }
        // the goal from here on is to keep the servo at an angle of 60 degrees relative to the ground
        double targetServoAngle = 200 - armAngle; // from alternate interior angles plus 30 degree offset, minus 10 degree slop
        double targetServoPosition = targetServoAngle / WRIST_DEGREES_PER_POS + WRIST_PARALLEL_POS;
        return targetServoPosition;
    }

    private double clamp(double val, double min, double max){
        return Math.max(min, Math.min(max, val));
    }
}
