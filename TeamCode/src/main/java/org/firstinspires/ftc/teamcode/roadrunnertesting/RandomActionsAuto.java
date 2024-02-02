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
import org.firstinspires.ftc.teamcode.modules.KookyClawTrajectories;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

@Autonomous()
public final class RandomActionsAuto extends LinearOpMode {
    private ArmController armController;
    private Robot2023 robot;
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, KookyClawTrajectories.getStartingPose(KookyClawTrajectories.StartingPosition.Back, KookyClawTrajectories.Team.Blue));
        robot = new Robot2023(this, drive);
        armController = new ArmController();
        armController.onOpmodeInit(robot, telemetry);
        Actions.runBlocking(armController.closeLeftClawAction());
        ActionExecutor actionExecutor = new ActionExecutor();
        telemetry.log().add("At start, pose is " + drive.pose);
        telemetry.log().add("Targeting X pos " + (drive.pose.position.x + KookyClawTrajectories.getRobotSize().x));
        waitForStart();
        Action robotLength = drive.actionBuilder(drive.pose)
                .setTangent(0)
                .lineToX(drive.pose.position.x + KookyClawTrajectories.getRobotSize().x)
                .build();
        Action robotLengthPlusSideShield = drive.actionBuilder(drive.pose)
                .setTangent(0)
                .lineToX(drive.pose.position.x + KookyClawTrajectories.getRobotSize().x+KookyClawTrajectories.sideShieldLength)
                .build();
        Action halfRobotPlusPixelOffset = drive.actionBuilder(drive.pose)
                .setTangent(0)
                .lineToX(drive.pose.position.x + KookyClawTrajectories.getRobotSize().x/2+KookyClawTrajectories.pixelRobotOffset.x)
                .build();
        actionExecutor.setAction(
            robotLength
        );
        while (actionExecutor.actionIsActive()){
            actionExecutor.doLoop();
        }
        telemetry.log().add("Current X position: " + drive.pose.position.x +", delta: " + (drive.pose.position.x - KookyClawTrajectories.getStartingPose(KookyClawTrajectories.StartingPosition.Back, KookyClawTrajectories.Team.Blue).position.x));
        while (opModeIsActive()){
            // spin; do nothing
            idle();
        }
    }
}
