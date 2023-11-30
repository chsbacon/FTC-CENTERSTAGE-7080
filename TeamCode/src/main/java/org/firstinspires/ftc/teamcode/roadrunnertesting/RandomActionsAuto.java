package org.firstinspires.ftc.teamcode.roadrunnertesting;

import static org.firstinspires.ftc.teamcode.modules.FieldPositions.SpikeMarkLocation.Center;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.ArmController;
import org.firstinspires.ftc.teamcode.modules.FieldPositions;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

@Autonomous()
public final class RandomActionsAuto extends LinearOpMode {
    private ArmController armController;
    private Robot2023 robot;
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, FieldPositions.getStartingPose(FieldPositions.StartingPosition.Front, FieldPositions.Team.Blue));
        robot = new Robot2023(this, drive);
        armController = new ArmController();
        armController.onOpmodeInit(robot, telemetry);
        Actions.runBlocking(armController.closeClawAction());
        waitForStart();
        Action getToBoard = FieldPositions.getTrajToSpikeMark(drive, FieldPositions.StartingPosition.Front, FieldPositions.Team.Blue, Center);
        Action goPark = FieldPositions.getTrajEscapeSpikeMark(drive, FieldPositions.StartingPosition.Front, FieldPositions.Team.Blue, Center);
//                .setTangent(0)
//                .waitSeconds(2)
//                .splineToConstantHeading(new Vector2d(-16, 52), 0)
//                .splineToConstantHeading(new Vector2d(-9, 63), Math.PI/2)
//                .waitSeconds(3)
//                .build();
        Actions.runBlocking(
//                getToBoard
                new SequentialAction(
                    getToBoard,
                        //armController.openClaw(),
                        goPark
                        //FieldPositions.getTrajToPark(drive, FieldPositions.StartingPosition.Front, FieldPositions.Team.Blue, Center, false)
                  )
        );

    }
}
