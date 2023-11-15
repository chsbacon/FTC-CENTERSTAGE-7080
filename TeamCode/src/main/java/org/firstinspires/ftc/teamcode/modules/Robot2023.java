package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;

public class Robot2023 {
    LinearOpMode opMode;
    public HardwareMap hardwareMap;
    Servo clawServo;
    public ArmController armController = null;
    public DriveController driveController = null;
    public AprilTagController visionController = null;
    MecanumDrive drive;
    Telemetry telemetry;
    WebcamName webcam;
    public Robot2023(LinearOpMode opMode, MecanumDrive drive, boolean doArmController, boolean doDriveController, boolean doVisionController){
        this.hardwareMap = opMode.hardwareMap;
        this.telemetry = opMode.telemetry;
        this.drive = drive;
        this.webcam = hardwareMap.get(WebcamName.class, "Webcam1");
        clawServo = this.hardwareMap.get(Servo.class, "clawServo");
        if (doArmController){
            armController = new ArmController();
        }
        if (doDriveController){
            driveController = new DriveController();
        }
        if (doVisionController){
            visionController = new AprilTagController();
        }
    }
    public Robot2023(LinearOpMode opMode, MecanumDrive drive){
        this(opMode, drive, true, true, false);
    }
    public void onOpmodeInit(){
        if (armController != null){
            armController.onOpmodeInit(this, this.telemetry);
        }
        if (driveController != null){
            this.telemetry.log().add("initting drive...");
            this.telemetry.update();
            driveController.onOpmodeInit(this, this.drive, this.telemetry);
        }
        if (visionController != null){
            visionController.onOpmodeInit(this, this.telemetry, false);
        }
    }
    public void handleInput(Gamepad gamepad1, Gamepad gamepad2){
        if (armController != null){
            armController.handleInput(gamepad1, gamepad2);
        }
        if (driveController != null){
            driveController.handleInput(gamepad1, gamepad2);
        }
        if (visionController != null){
            visionController.handleInput(gamepad1, gamepad2);
        }
    }
}
