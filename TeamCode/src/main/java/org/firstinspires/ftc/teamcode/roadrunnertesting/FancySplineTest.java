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
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(-60, -36, 0));

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(drive.pose)
                        .lineToX(-40)
                        .setTangent(Math.PI/2)
                        .splineTo(new Vector2d(-36, -12), Math.PI / 2)
                        .splineTo(new Vector2d(-16, 56), Math.PI/2)
                        .waitSeconds(3)
                        .build());

    }
}
