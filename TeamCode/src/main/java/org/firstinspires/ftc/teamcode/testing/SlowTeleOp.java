package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.DriveController;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

@TeleOp
public class SlowTeleOp extends LinearOpMode {
    private DriveController driveController;
    public void runOpMode(){
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0,0,0));
        Robot2023 robot = new Robot2023(this, drive, false, true, false, false, false);
        robot.onOpmodeInit();
        waitForStart();
        while (opModeIsActive()){
            gamepad1.left_trigger = 1;
            gamepad1.right_trigger = 0;
            robot.doLoop(gamepad1, gamepad2);
            idle();
        }
    }
}
