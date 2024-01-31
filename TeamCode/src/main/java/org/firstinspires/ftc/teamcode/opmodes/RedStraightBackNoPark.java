package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.KookyClawTrajectories;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

@Autonomous
public class RedStraightBackNoPark extends LinearOpMode {
    Robot2023 robot;
    final KookyClawTrajectories.Team team = KookyClawTrajectories.Team.Red;
    final KookyClawTrajectories.StartingPosition startingPosition = KookyClawTrajectories.StartingPosition.Back;
    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot2023(this, new MecanumDrive(hardwareMap, KookyClawTrajectories.getStartingPose(startingPosition, team)), true, false, false, true, true, true);
        robot.autonomousController.setSettings(startingPosition, team);
        robot.onOpmodeInit();
        waitForStart();

        while (opModeIsActive()){
            robot.doLoop(gamepad1, gamepad2);
            idle();
        }
    }
}