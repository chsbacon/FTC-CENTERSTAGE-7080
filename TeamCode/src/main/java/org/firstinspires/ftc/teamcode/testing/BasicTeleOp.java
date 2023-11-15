package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.ftccommon.configuration.RobotConfigResFilter;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.DriveController;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

@TeleOp
public class BasicTeleOp extends LinearOpMode {
    private DriveController driveController;
    public void runOpMode(){
        Robot2023 robot = new Robot2023(this, new MecanumDrive(hardwareMap, new Pose2d(0,0,0)), false, true, false);

        waitForStart();
        while (opModeIsActive()){
            robot.handleInput();
            idle();
        }
    }
}
