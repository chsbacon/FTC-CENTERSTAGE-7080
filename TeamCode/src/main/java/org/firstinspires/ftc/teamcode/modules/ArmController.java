package org.firstinspires.ftc.teamcode.modules;

import static com.acmerobotics.roadrunner.ftc.Actions.runBlocking;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ArmController {
    public enum ArmLocation {
        Intake1,// picking up first pixel
        Intake2,//picking up second pixel
        GeneralProtected, // not in intake but still in protected area
        Passing,
        Hang, // straight vertical +- a few degrees
        Score // between hang and MAX
    }
    private Robot2023 robot;
    private Telemetry telemetry;
    private final double CLAW_OPEN = 0.8;
    private final double CLAW_CLOSED = 0;
    public final int LINEAR_MIN = 0;
    public final int LINEAR_MAX = 1450;
    public final int LINEAR_INTAKE2 = 200;
    // these are the left encoder numbers; the right encoder is not up to spec
    public final int FOREARM_MIN = 0;
    public final int FOREARM_PASSING = FOREARM_MIN;
    public final int FOREARM_MAX = 190;
    public static final int FOREARM_VERTICAL = 110;
    public final int FOREARM_PARALELL = 150;
    static final double DEGREES_PER_COREHEX_TICK = 360.0/288.0;
    private ElapsedTime loopTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

    // expects to be initted with arm in intake
    PIDController forearmPID = new PIDController(0.005, 0.0007, 0.0013); // old: 0.004, 0.0007, 0.0013; old old: 0.004, 0.01, 0.0007
    double gravityGain = 0.1; // old: 0.1; old old: 0.1
    double frictionGain = 0.1; // old: 0.11; old old: 0.1
    final double cgOffset = 0; // degrees off arm
    private ArmLocation armTargetLocation = ArmLocation.Intake1;
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
    public void openClaw(){
        robot.clawServo.setPosition(CLAW_OPEN);
    }
    public void closeClaw(){
        robot.clawServo.setPosition(CLAW_CLOSED);
    }
    public void enterIntake(){

    }
    public void leaveIntake(){

    }

    public Action openClawAction(){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                openClaw();
                // telemetryPacket.addLine("Claw opened");
                return false;
            }
        };
    }
    public Action closeClawAction(){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                closeClaw();
                // telemetryPacket.addLine("Claw closed");
                return false;
            }
        };
    }
    public void doLoop(Gamepad gamepad1, Gamepad gamepad2){
        if (gamepad2.left_trigger > 0.5){
            //telemetry.log().add("HI open claw");
            openClaw();
        }
        if (gamepad2.right_trigger > 0.5){
            //telemetry.log().add("HI close claw");
            closeClaw();
        }
        ArmLocation armTargetLocation = getArmLocationFromPositions((int)forearmPID.getSetPoint(), robot.linearExtenderMotor.getTargetPosition());
        ArmLocation armCurrentLocation = getArmLocationFromPositions(robot.leftForearmMotor.getCurrentPosition(), robot.linearExtenderMotor.getCurrentPosition());
        if (true || !(locationIsProtected(armTargetLocation) || locationIsProtected(armCurrentLocation)) || gamepad2.start){
            doManualLinear(gamepad2, gamepad2.start && gamepad2.left_bumper);
            doManualArm(gamepad2, gamepad2.start && gamepad2.left_bumper);
        }
        if(gamepad2.a){
            armTargetLocation = ArmLocation.Intake1;
            if(!locationIsProtected(armCurrentLocation) || armCurrentLocation == ArmLocation.GeneralProtected){
                actionExecutor.setAction(new ParallelAction(
                        //goToLinearHeightAction(LINEAR_MAX),
                        goToArmPositionAction(FOREARM_MIN),
                        //new SleepAction(0.5),
                        goToLinearHeightAction(LINEAR_MIN)
                ));
            }
            if(armCurrentLocation == ArmLocation.Intake2 || armCurrentLocation == ArmLocation.Intake1){
                actionExecutor.setAction(new SequentialAction(
                        goToLinearHeightAction(LINEAR_MIN)
                ));
            }
        }
//        if(gamepad2.b){
//            armTargetLocation = ArmLocation.Intake2;
//            if(!locationIsProtected(armCurrentLocation) || armCurrentLocation == ArmLocation.GeneralProtected){
//                actionExecutor.setAction(new SequentialAction(
//                        //goToLinearHeightAction(LINEAR_MAX),
//                        goToArmPositionAction(FOREARM_MIN)//,
//                        //new SleepAction(0.5),
//                        //goToLinearHeightAction(LINEAR_INTAKE2)
//                ));
//            }
//            if(armCurrentLocation == ArmLocation.Intake2 || armCurrentLocation == ArmLocation.Intake1){
//                actionExecutor.setAction(new SequentialAction(
//                        goToLinearHeightAction(LINEAR_INTAKE2)
//                ));
//            }
//        }
        if(gamepad2.right_bumper){
            armTargetLocation = ArmLocation.Hang;
            actionExecutor.setAction(new ParallelAction(
                    goToLinearHeightAction(LINEAR_MAX),
                    goToArmPositionAction(FOREARM_VERTICAL)
            ));
        }
        if(gamepad2.x){
            armTargetLocation = ArmLocation.Passing;
            actionExecutor.setAction(new SequentialAction(
                    //goToLinearHeightAction(LINEAR_MAX),
                    goToArmPositionAction(FOREARM_PASSING)//,
                    //goToLinearHeightAction(LINEAR_MIN)
            ));
        }
        if(gamepad2.y){
            armTargetLocation = ArmLocation.Score;
            actionExecutor.setAction(new SequentialAction(
                    //goToLinearHeightAction(LINEAR_MAX),
                    goToArmPositionAction(FOREARM_PARALELL)//,
                    //goToLinearHeightAction(LINEAR_MIN)
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
        if(gamepad2.back){
            for(DcMotorEx motor: new DcMotorEx[]{robot.leftForearmMotor, robot.rightForearmMotor, robot.linearExtenderMotor}){
                motor.setPower(0);
            }
        } else {
            robot.linearExtenderMotor.setPower(1);
        }
        doArmControl();

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
    }
    public static double ticksToAngle(double ticks){
        return (ticks - FOREARM_VERTICAL) * DEGREES_PER_COREHEX_TICK;
    }

    public boolean locationIsProtected(ArmLocation armLocation){
        return armLocation == ArmLocation.GeneralProtected || armLocation == ArmLocation.Intake1 || armLocation == ArmLocation.Intake2;
    }
    public ArmLocation getArmLocationFromPositions(int forearmPosition, int linearPosition){
        final int FUDGE_FACTOR = 10;
        if (linearPosition < LINEAR_MIN + FUDGE_FACTOR && forearmPosition < FOREARM_MIN + FUDGE_FACTOR){
            return ArmLocation.Intake1;
        } else if (linearPosition < LINEAR_INTAKE2 + FUDGE_FACTOR && forearmPosition < FOREARM_MIN + FUDGE_FACTOR){
            return ArmLocation.Intake2;
        } else if (forearmPosition < FOREARM_PASSING - 10) {
            return ArmLocation.GeneralProtected;
        } else if (forearmPosition < FOREARM_PASSING + 10){
            return ArmLocation.Passing;
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
            ElapsedTime armPosTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
            int loopsBeforeArrival = 0;
            int loopsAfterArrival = 0;
            int delayedLoops = 0;
            double maxDelta = 0;
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                forearmPID.setSetPoint(targetPosition);
                return !forearmPID.atSetPoint();
//                if(loopTimer.seconds() < 0.01){
//                    delayedLoops++;
//                    while(loopTimer.seconds() < 0.01){
//                        // do nothing
//                    }
//                }
//                double distToTargetSetpoint = targetPosition - forearmPID.getSetPoint();
//                double deltaThisLoop = TICKS_PER_SEC * loopTimer.seconds();
//                armPosTimer.reset();
//                if(deltaThisLoop > maxDelta){
//                    maxDelta = deltaThisLoop;
//                }
//                if (Math.abs(distToTargetSetpoint) < deltaThisLoop){
//                    forearmPID.setSetPoint(targetPosition);
//                    loopsAfterArrival++;
//                } else {
//                    forearmPID.setSetPoint(forearmPID.getSetPoint() + deltaThisLoop * Math.signum(distToTargetSetpoint));
//                    loopsBeforeArrival++;
//                }
//                telemetry.addData("deltaThisLoop: ", deltaThisLoop);
//                telemetry.addData("distToTargetSetpoint: ", distToTargetSetpoint);
//                telemetry.addData("loopsbeforearrival: ", loopsBeforeArrival);
//                telemetry.addData("loopsafterarrival: ", loopsAfterArrival);
//                telemetry.addData("maxdelta", maxDelta);
//                telemetry.addData("looptimer", loopTimer.seconds());
//                telemetry.addData("delayedLoops",delayedLoops);
//                if (forearmPID.atSetPoint() && Math.abs(distToTargetSetpoint) < deltaThisLoop){
//                    telemetry.log().add("Finished GoToArmPositionAction");
//                }
//                return !(forearmPID.atSetPoint() && Math.abs(distToTargetSetpoint) < deltaThisLoop);
            }
        };
    }
    private double clamp(double val, double min, double max){
        return Math.max(min, Math.min(max, val));
    }
}
