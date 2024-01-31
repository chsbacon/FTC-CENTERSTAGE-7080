package org.firstinspires.ftc.teamcode.roadrunnertesting;

import static org.firstinspires.ftc.teamcode.modules.OldClawTrajectories.SpikeMarkLocation.Center;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.ActionExecutor;
import org.firstinspires.ftc.teamcode.modules.ArmController;
import org.firstinspires.ftc.teamcode.modules.OldClawTrajectories;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

@Autonomous()
public final class RandomActionsAuto extends LinearOpMode {
    private ArmController armController;
    private Robot2023 robot;
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, OldClawTrajectories.getStartingPose(OldClawTrajectories.StartingPosition.Front, OldClawTrajectories.Team.Blue));
        robot = new Robot2023(this, drive);
        armController = new ArmController();
        armController.onOpmodeInit(robot, telemetry);
        Actions.runBlocking(armController.closeLeftClawAction());
        ActionExecutor actionExecutor = new ActionExecutor();
        waitForStart();
        Action getToBoard = OldClawTrajectories.getTrajToSpikeMark(drive, OldClawTrajectories.StartingPosition.Front, OldClawTrajectories.Team.Blue, Center);
        Action goPark = OldClawTrajectories.getTrajEscapeSpikeMark(drive, OldClawTrajectories.StartingPosition.Front, OldClawTrajectories.Team.Blue, Center, false);
//                .setTangent(0)
//                .waitSeconds(2)
//                .splineToConstantHeading(new Vector2d(-16, 52), 0)
//                .splineToConstantHeading(new Vector2d(-9, 63), Math.PI/2)
//                .waitSeconds(3)
//                .build();
        actionExecutor.setAction(
        //Actions.runBlocking(
//                getToBoard
                new SequentialAction(
                    getToBoard,
                        //armController.openClaw(),
                        goPark
                        //FieldPositions.getTrajToPark(drive, FieldPositions.StartingPosition.Front, FieldPositions.Team.Blue, Center, false)
                  )
        );
        while (actionExecutor.actionIsActive()){
            actionExecutor.doLoop();
        }

    }
}
