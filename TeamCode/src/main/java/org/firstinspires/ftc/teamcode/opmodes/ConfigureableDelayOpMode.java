package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.KookyClawTrajectories;
import org.firstinspires.ftc.teamcode.modules.KookyClawTrajectories.StartingPosition;
import org.firstinspires.ftc.teamcode.modules.KookyClawTrajectories.Team;

import org.firstinspires.ftc.teamcode.modules.Robot2023;

@Autonomous(name="ConfigureableDelayOpMode", group="Autonomous", preselectTeleOp = "IntegratedOpMode")
public class ConfigureableDelayOpMode extends LinearOpMode {
    Robot2023 robot;
    final Team team = Team.Blue;
    final StartingPosition startingPosition = StartingPosition.Back;
    final boolean doScoreBackboard = true;
    final boolean doPark = true;
    final double delay = 0;
    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot2023(this, new MecanumDrive(hardwareMap, KookyClawTrajectories.getStartingPose(startingPosition, team)), true, false, false, true, true, true);
        robot.autonomousController.setSettings(startingPosition, team, doScoreBackboard, doPark, 0);
        robot.onOpmodeInit();
        telemetry.log().add("ConfigureableDelayOpMode settings: " + team + " " + startingPosition + " " + doScoreBackboard + " " + doPark + " " + delay);
        waitForStart();

        while (opModeIsActive()){
            robot.doLoop(gamepad1, gamepad2);
            idle();
        }
    }
}