package org.firstinspires.ftc.teamcode.roadrunnertesting;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.ArmController;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

@Autonomous()
@Disabled()
public final class RightBluePark extends LinearOpMode {
    private ArmController armController;
    private Robot2023 robot;
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(-64, -36, 0));
        robot = new Robot2023(this, drive);
        armController = new ArmController();
        armController.onOpmodeInit(robot, telemetry);
        //Actions.runBlocking(armController.closeClawAction());
        waitForStart();
        Action getToBoard = drive.actionBuilder(drive.pose)
                .waitSeconds(3)
                .splineTo(new Vector2d(-35, -30), Math.PI/2)
                .splineTo(new Vector2d(-35, -12), Math.PI / 2)
                .splineTo(new Vector2d(-36, 52), Math.PI/2)
                .build();
        Action goPark = drive.actionBuilder(new Pose2d(new Vector2d(-36, 52), Math.PI/2)) // ending pose from last action
                .setTangent(0)
                .waitSeconds(2)
                .splineToConstantHeading(new Vector2d(-16, 52), 0)
                .splineToConstantHeading(new Vector2d(-9, 63), Math.PI/2)
                .waitSeconds(3)
                .build();
        Actions.runBlocking(
                new SequentialAction(
                    getToBoard,
                        //armController.openClawAction(),
                        goPark
                )
        );

    }
}
