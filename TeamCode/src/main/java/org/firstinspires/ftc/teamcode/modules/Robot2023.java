package org.firstinspires.ftc.teamcode.modules;

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
    public Servo leftClawServo;
    public Servo rightClawServo;
    public Servo clawWristServo;
    public Servo droneServo;
    public DcMotorEx linearExtenderMotor;
    public DcMotorEx rightForearmMotor;
    public DcMotorEx leftForearmMotor;
    //public DcMotorEx intakeMotor;
    public ArmController armController = null;
    public DriveController driveController = null;
    public AprilTagController aprilTagController = null;
    public TfodController tfodController = null;
    public AutonomousController autonomousController = null;
    public DroneController droneController = null;
    MecanumDrive drive;
    Telemetry telemetry;
    WebcamName webcam;

    public Robot2023(LinearOpMode opMode, MecanumDrive drive, boolean doArmController, boolean doDriveController, boolean doAprilTags, boolean doTfod, boolean doAuto, boolean doDrone){
        this.hardwareMap = opMode.hardwareMap;
        this.telemetry = opMode.telemetry;
        this.drive = drive;
        // conditional hardware inits
        if(doAprilTags || doTfod){
            this.webcam = hardwareMap.get(WebcamName.class, "Webcam1");
        }
        if(doArmController) {
            leftClawServo = this.hardwareMap.get(Servo.class, "leftClawServo");
            rightClawServo = this.hardwareMap.get(Servo.class, "rightClawServo");
            clawWristServo = this.hardwareMap.get(Servo.class, "clawWristServo");
            linearExtenderMotor = this.hardwareMap.get(DcMotorEx.class, "linearExtender");
            linearExtenderMotor.setDirection(DcMotorEx.Direction.FORWARD);
            rightForearmMotor = this.hardwareMap.get(DcMotorEx.class, "forearmRight");
            rightForearmMotor.setDirection(DcMotorEx.Direction.REVERSE);
            leftForearmMotor = this.hardwareMap.get(DcMotorEx.class, "forearmLeft");
            leftForearmMotor.setDirection(DcMotorEx.Direction.REVERSE);
        }
        if(doDrone){
            droneServo = this.hardwareMap.get(Servo.class, "droneServo");
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
        if(doDrone) {
            droneController = new DroneController();
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
        if(droneController != null){
            droneController.onOpmodeInit(this, this.telemetry);
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
        if(droneController != null){
            droneController.doLoop(gamepad1, gamepad2);
        }

    }

}
