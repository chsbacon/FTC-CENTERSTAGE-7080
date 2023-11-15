package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

@Autonomous
public class AprilTagDetection extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        Robot2023 robot = new Robot2023(this, drive, false, false, true);
        robot.onOpmodeInit();
        waitForStart();
        while(!isStopRequested() && opModeIsActive()) {
            robot.handleInput(gamepad1, gamepad2);
            telemetry.addData("Last detection", robot.visionController.getLastIDDetectedName());
            telemetry.update();
        }
    }
}
