package org.firstinspires.ftc.teamcode.roadrunnertesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
@Autonomous()
public final class RightBluePark extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(-64, -36, 0));

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(drive.pose)
                        .waitSeconds(3)
                        .splineTo(new Vector2d(-35, -30), Math.PI/2)
                        .splineTo(new Vector2d(-35, -12), Math.PI / 2)
                        .splineTo(new Vector2d(-36, 52), Math.PI/2)
                        .waitSeconds(1)
                        .setTangent(0)
                        .splineToConstantHeading(new Vector2d(-16, 52), 0)
                        .splineToConstantHeading(new Vector2d(-9, 63), Math.PI/2)
                        .waitSeconds(3)
                        .build());

    }
}
