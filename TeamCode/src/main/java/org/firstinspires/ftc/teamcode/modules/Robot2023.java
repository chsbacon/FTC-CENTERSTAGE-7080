package org.firstinspires.ftc.teamcode.modules;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
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
    public DcMotorEx linearExtenderMotor;
    public DcMotorEx rightForearmMotor;
    public DcMotorEx leftForearmMotor;
    public DcMotorEx intakeMotor;
    public ArmController armController = null;
    public DriveController driveController = null;
    public AprilTagController aprilTagController = null;
    public TfodController tfodController = null;
    public AutonomousController autonomousController = null;
    MecanumDrive drive;
    Telemetry telemetry;
    WebcamName webcam;
    public Robot2023(LinearOpMode opMode, MecanumDrive drive, boolean doArmController, boolean doDriveController, boolean doAprilTags, boolean doTfod, boolean doAuto){
        this.hardwareMap = opMode.hardwareMap;
        this.telemetry = opMode.telemetry;
        this.drive = drive;
        // conditional hardware inits
        if(doAprilTags || doTfod){
            this.webcam = hardwareMap.get(WebcamName.class, "Webcam1");
        }
        if(doArmController) {
            clawServo = this.hardwareMap.get(Servo.class, "clawServo");
            linearExtenderMotor = this.hardwareMap.get(DcMotorEx.class, "linearExtender");
            rightForearmMotor = this.hardwareMap.get(DcMotorEx.class, "rightForearm");
            leftForearmMotor = this.hardwareMap.get(DcMotorEx.class, "leftForearm");
        }
//        if(doIntake){
//            intakeMotor = this.hardwareMap.get(DcMotorEx.class, "intakeMotor");
//        }

        // controller inits
        if (doArmController){
            armController = new ArmController();
        }
        if (doDriveController){
            driveController = new DriveController();
        }
        if (doAprilTags){
            aprilTagController = new AprilTagController();
        }
        if (doTfod){
            tfodController = new TfodController();
        }
        if (doAuto){
            autonomousController = new AutonomousController();
        }
    }
    public Robot2023(LinearOpMode opMode, MecanumDrive drive){
        this(opMode, drive, true, true, false,false, false);
    }
    public void onOpmodeInit(){
        //drive.imu.resetYaw();
        if (armController != null){
            armController.onOpmodeInit(this, this.telemetry);
        }
        if (driveController != null){
            this.telemetry.log().add("initting drive...");
            this.telemetry.update();
            driveController.onOpmodeInit(this, this.drive, this.telemetry);
        }
        if (aprilTagController != null){
            aprilTagController.onOpmodeInit(this, this.telemetry, false);
        }
        if (tfodController != null) {
            tfodController.onOpmodeInit(this, this.telemetry);
        }
        if (autonomousController != null){
            autonomousController.onOpmodeInit(this, this.telemetry);
        }
    }
    public void doLoop(Gamepad gamepad1, Gamepad gamepad2){
        if (armController != null){
            armController.doLoop(gamepad1, gamepad2);
        }
        if (driveController != null){
            driveController.doLoop(gamepad1, gamepad2);
        }
        if (aprilTagController != null){
            aprilTagController.doLoop(gamepad1, gamepad2);
        }
        if (tfodController != null){
            tfodController.doLoop(gamepad1, gamepad2);
        }
        if(autonomousController != null){
            autonomousController.doLoop();
        }
    }


}
