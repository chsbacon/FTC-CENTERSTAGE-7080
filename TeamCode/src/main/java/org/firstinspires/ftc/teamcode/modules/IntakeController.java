package org.firstinspires.ftc.teamcode.modules;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.security.KeyStore;

public class IntakeController {
    private Robot2023 robot;
    private Telemetry telemetry;
    private final double INTAKE_OFF = 0;
    private final double INTAKE_ON = 0.5;
    public void onOpmodeInit(Robot2023 robot, Telemetry telemetry){
        this.robot = robot;
        this.telemetry = telemetry;
    }
    public void spinTake(){
        robot.intakeMotor.setPower(INTAKE_ON);
    }
    public void stopTake(){
        robot.intakeMotor.setPower(INTAKE_OFF);
    }
    public void outTake(){
        robot.intakeMotor.setPower(-INTAKE_ON);
    }
    public Action spinTakeAction(){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                spinTake();
                // telemetryPacket.addLine("Claw opened");
                return false;
            }
        };
    }
    public Action stopTakeAction(){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                stopTake();
                // telemetryPacket.addLine("Claw closed");
                return false;
            }
        };
    }
    public void doLoop(Gamepad gamepad1, Gamepad gamepad2){
        if (gamepad2.dpad_up){
            //telemetry.log().add("HI, I use spinTake");
            spinTake();
        }
        if (gamepad2.dpad_down){
            //telemetry.log().add("HI, spinTake has fainted");
            stopTake();
        }
        if (gamepad2.dpad_left && gamepad2.left_bumper){
            outTake();
        }

        //telemetry.update();
    }
}