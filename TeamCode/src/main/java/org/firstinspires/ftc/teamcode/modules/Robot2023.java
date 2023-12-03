package org.firstinspires.ftc.teamcode.modules;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
import com.acmerobotics.roadrunner.ftc.RawEncoder;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.MotorControlAlgorithm;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
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
    public IntakeController intakeController = null;
    MecanumDrive drive;
    Telemetry telemetry;
    WebcamName webcam;

    public Robot2023(LinearOpMode opMode, MecanumDrive drive, boolean doArmController, boolean doDriveController, boolean doAprilTags, boolean doTfod, boolean doAuto, boolean doIntake){
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
            linearExtenderMotor.setDirection(DcMotorEx.Direction.FORWARD);
            rightForearmMotor = this.hardwareMap.get(DcMotorEx.class, "forearmRight");
            rightForearmMotor.setDirection(DcMotorEx.Direction.REVERSE);
            leftForearmMotor = this.hardwareMap.get(DcMotorEx.class, "forearmLeft");
            leftForearmMotor.setDirection(DcMotorEx.Direction.REVERSE);
        }
        if(doIntake){
            intakeMotor = this.hardwareMap.get(DcMotorEx.class, "intakeMotor");
        }

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
        if(doIntake) {
            intakeController = new IntakeController();
        }
    }
    public Robot2023(LinearOpMode opMode, MecanumDrive drive){
        this(opMode, drive, true, true, false,false, false, true);
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
        if (autonomousController != null){
            autonomousController.onOpmodeInit(this, this.telemetry);
        }
        if (tfodController != null) {
            if (autonomousController != null) {
                tfodController.onOpmodeInit(this, this.telemetry, autonomousController.team);
            } else {
                telemetry.log().add("WARNING: tfod controller is running without an autonomous controller");
                tfodController.onOpmodeInit(this, this.telemetry, FieldPositions.Team.Blue);
            }
        }
        if(intakeController != null){
            intakeController.onOpmodeInit(this, this.telemetry);
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
        if(intakeController != null){
            intakeController.doLoop(gamepad1, gamepad2);
        }

    }

}
