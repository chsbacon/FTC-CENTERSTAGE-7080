package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DroneController {
    private Robot2023 robot;
    private Telemetry telemetry;
    private final double DRONE_HELD = 0.9;
    private final double DRONE_RELEASED = 0.65;
    public void onOpmodeInit(Robot2023 robot, Telemetry telemetry){
        this.robot = robot;
        this.telemetry = telemetry;
        holdDrone();
    }
    public void releaseDrone(){
        robot.droneServo.setPosition(DRONE_RELEASED);
    }
    public void holdDrone(){
        robot.droneServo.setPosition(DRONE_HELD);
    }

    public void doLoop(Gamepad gamepad1, Gamepad gamepad2){
        if (gamepad2.dpad_left && gamepad2.b){
            //telemetry.log().add("HI, I use spinTake");
            releaseDrone();
        } else {
            holdDrone();
        }

        //telemetry.update();
    }
}