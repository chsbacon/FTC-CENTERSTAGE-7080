package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;

public class ControllerTemplate {
    private HardwareMap robot;
    private MecanumDrive drive;
    private Telemetry telemetry;
    ElapsedTime pidTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    public void onOpmodeInit(HardwareMap robot, MecanumDrive drive, Telemetry telemetry){
        this.robot = robot;
        this.drive = drive;
        this.telemetry = telemetry;
    }

}
