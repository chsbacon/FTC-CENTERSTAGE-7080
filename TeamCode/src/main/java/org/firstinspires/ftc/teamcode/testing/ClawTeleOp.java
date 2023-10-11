package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.robot.Robot;

import org.checkerframework.checker.units.qual.A;
import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.ArmController;
import org.firstinspires.ftc.teamcode.modules.DriveController;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

@TeleOp
public class ClawTeleOp extends LinearOpMode {
    private DriveController driveController;
    private ArmController armController;
    private Robot2023 robot;
    public void runOpMode(){
        robot = new Robot2023(hardwareMap);
        driveController = new DriveController();
        driveController.onOpmodeInit(robot, new MecanumDrive(hardwareMap, new Pose2d(0,0,0)), telemetry);
        armController = new ArmController();
        armController.onOpmodeInit(robot, telemetry);

        waitForStart();
        while (opModeIsActive()){
            driveController.handleInput(gamepad1, gamepad2);
            armController.handleInput(gamepad1,gamepad2);

            idle();
        }
    }
}
