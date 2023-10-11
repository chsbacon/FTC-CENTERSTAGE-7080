package org.firstinspires.ftc.teamcode.modules;

import static com.acmerobotics.roadrunner.ftc.Actions.runBlocking;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;

public class ArmController {
    private Robot2023 robot;
    private Telemetry telemetry;
    private final double CLAW_OPEN = 0;
    private final double CLAW_CLOSED = 1;
    public void onOpmodeInit(Robot2023 robot, Telemetry telemetry){
        this.robot = robot;
        this.telemetry = telemetry;
    }
    Action openClaw(){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                robot.clawServo.setPosition(CLAW_OPEN);
                telemetryPacket.addLine("Claw opened");
                return false;
            }
        };
    }
    Action closeClaw(){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                robot.clawServo.setPosition(CLAW_CLOSED);
                telemetryPacket.addLine("Claw closed");
                return false;
            }
        };
    }
    public void handleInput(Gamepad gamepad1, Gamepad gamepad2){
        if (gamepad1.left_bumper){
            telemetry.log().add("HI open claw");
            runBlocking(openClaw());
        }
        if (gamepad1.right_bumper){
            telemetry.log().add("HI close claw");
            runBlocking(closeClaw());
        }
    }
}
