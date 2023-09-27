package org.firstinspires.ftc.teamcode.roadrunnertesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
@TeleOp()
public final class FancySplineTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(drive.pose)
                        .splineTo(new Vector2d(30, 30), Math.PI / 2)
                        .waitSeconds(1)
                        .splineToLinearHeading(new Pose2d(60, 0,Math.PI), -Math.PI/2)
                        .waitSeconds(1)
                        .setTangent(Math.PI)
                        .splineToLinearHeading(new Pose2d(20, 0, 0), Math.PI)
                        .lineToX(0)
                        .build());

    }
}
