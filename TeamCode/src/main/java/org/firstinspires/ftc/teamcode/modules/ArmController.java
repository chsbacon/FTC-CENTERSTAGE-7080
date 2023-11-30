package org.firstinspires.ftc.teamcode.modules;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ArmController {
    public enum ArmLocation {
        Intake1,// picking up first pixel
        Intake2,//picking up second pixel
        GeneralProtected, // not in intake but still in protected area
        Hang, // straight vertical +- a few degrees
        Score // between hang and MAX
    }
    private Robot2023 robot;
    private Telemetry telemetry;
    private final double CLAW_OPEN = 0;
    private final double CLAW_CLOSED = 1;
    private final int LINEAR_MIN = 0;
    private final int LINEAR_MAX = 600;
    private final int LINEAR_INTAKE2 = 60;
    private final int FOREARM_MIN = 0;
    private final int FOREARM_MAX = 140;
    private final int FOREARM_VERTICAL = 85;
    private final int FOREARM_PARALELL = 105;
    // expects to be initted with arm in intake
    private ArmLocation armTargetLocation = ArmLocation.Intake1;
    ActionExecutor actionExecutor = new ActionExecutor();
    public void onOpmodeInit(Robot2023 robot, Telemetry telemetry) {
        this.robot = robot;
        this.telemetry = telemetry;
        for(DcMotorEx motor: new DcMotorEx[]{robot.leftForearmMotor, robot.rightForearmMotor, robot.linearExtenderMotor}){
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setTargetPosition(0);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setPower(1);
        }
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
        if (gamepad1.left_trigger > 0.5){
            //telemetry.log().add("HI open claw");
            openClaw();
        }
        if (gamepad1.right_trigger > 0.5){
            //telemetry.log().add("HI close claw");
            closeClaw();
        }
        ArmLocation armTargetLocation = getArmLocationFromPositions(robot.rightForearmMotor.getTargetPosition(), robot.linearExtenderMotor.getTargetPosition());
        ArmLocation armCurrentLocation = getArmLocationFromPositions(robot.rightForearmMotor.getCurrentPosition(), robot.linearExtenderMotor.getCurrentPosition());
        if (!(locationIsProtected(armTargetLocation) || locationIsProtected(armCurrentLocation)) || gamepad2.start){
            doManualLinear(gamepad2);
            doManualArm(gamepad2);
        }
        if(gamepad2.a){
            armTargetLocation = ArmLocation.Intake1;
            if(!locationIsProtected(armCurrentLocation) || armCurrentLocation == ArmLocation.GeneralProtected){
                actionExecutor.setAction(new SequentialAction(
                        goToLinearHeightAction(LINEAR_MAX),
                        goToArmPositionAction(FOREARM_MIN),
                        goToArmPositionAction(LINEAR_MIN)
                ));
            }
            if(armCurrentLocation == ArmLocation.Intake2 || armCurrentLocation == ArmLocation.Intake1){
                actionExecutor.setAction(new SequentialAction(
                        goToArmPositionAction(LINEAR_MIN)
                ));
            }
        }
        if(gamepad2.b){
            armTargetLocation = ArmLocation.Intake2;
            if(!locationIsProtected(armCurrentLocation) || armCurrentLocation == ArmLocation.GeneralProtected){
                actionExecutor.setAction(new SequentialAction(
                        goToLinearHeightAction(LINEAR_MAX),
                        goToArmPositionAction(FOREARM_MIN),
                        goToArmPositionAction(LINEAR_INTAKE2)
                ));
            }
            if(armCurrentLocation == ArmLocation.Intake2 || armCurrentLocation == ArmLocation.Intake1){
                actionExecutor.setAction(new SequentialAction(
                        goToArmPositionAction(LINEAR_INTAKE2)
                ));
            }
        }
        if(gamepad2.y){
            armTargetLocation = ArmLocation.Hang;
            actionExecutor.setAction(new SequentialAction(
                    goToLinearHeightAction(LINEAR_MAX),
                    goToArmPositionAction(FOREARM_VERTICAL)
            ));
        }
        if(gamepad2.x){
            armTargetLocation = ArmLocation.Score;
            actionExecutor.setAction(new SequentialAction(
                    goToLinearHeightAction(LINEAR_MAX),
                    goToArmPositionAction(FOREARM_PARALELL)
            ));
        }
        if(gamepad2.back && gamepad2.start){
            for(DcMotorEx motor: new DcMotorEx[]{robot.leftForearmMotor, robot.rightForearmMotor, robot.linearExtenderMotor}){
                motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motor.setTargetPosition(0);
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor.setPower(1);
            }
        }
        if(gamepad2.back){
            for(DcMotorEx motor: new DcMotorEx[]{robot.leftForearmMotor, robot.rightForearmMotor, robot.linearExtenderMotor}){
                motor.setPower(0);
            }
        } else {
            for(DcMotorEx motor: new DcMotorEx[]{robot.leftForearmMotor, robot.rightForearmMotor, robot.linearExtenderMotor}){
                motor.setPower(1);
            }
        }
        actionExecutor.doLoop();
        //telemetry.update();
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
        } else if (forearmPosition < FOREARM_VERTICAL - 20){
            return ArmLocation.GeneralProtected;
        } else if (forearmPosition > FOREARM_VERTICAL + 20){
            return ArmLocation.Hang;
        } else {
            return ArmLocation.Score;
        }
    }
    private void setForearmMotorTargetPosition(int targetPosition){
        // to ensure we don't drive the motors against each other, always
        // use this function to set target position
        robot.rightForearmMotor.setTargetPosition(targetPosition);
        robot.leftForearmMotor.setTargetPosition(targetPosition);
    }
    public void doManualArm(Gamepad gamepad2){
        int newTargetPosition = robot.rightForearmMotor.getTargetPosition();
        if (Math.abs(gamepad2.right_stick_y) > .15){
            newTargetPosition += -50 * gamepad2.right_stick_y; // negative 40 because y is reversed
        }
        newTargetPosition = clamp(newTargetPosition, FOREARM_MIN, FOREARM_MAX);
        setForearmMotorTargetPosition(newTargetPosition);
    }
    public void doManualLinear(Gamepad gamepad2){
        int newTargetPosition = robot.linearExtenderMotor.getTargetPosition();
        if (Math.abs(gamepad2.left_stick_y) > .15){
            newTargetPosition += -50 * gamepad2.left_stick_y; // negative 40 because y is reversed
        }
        newTargetPosition = clamp(newTargetPosition, LINEAR_MIN, LINEAR_MAX);
        robot.linearExtenderMotor.setTargetPosition(newTargetPosition);
    }
    public Action goToLinearHeightAction(int targetPosition){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                robot.linearExtenderMotor.setTargetPosition(targetPosition);
                return robot.linearExtenderMotor.isBusy();
            }
        };
    }
    public Action goToArmPositionAction(int targetPosition){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                setForearmMotorTargetPosition(targetPosition);
                return robot.rightForearmMotor.isBusy() || robot.leftForearmMotor.isBusy();
            }
        };
    }
    private int clamp(int val, int min, int max){
        return Math.max(min, Math.min(max, val));
    }
}
