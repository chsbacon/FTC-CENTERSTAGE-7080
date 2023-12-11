package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.FieldPositions;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

@Autonomous
public class BlueStraightBackPark extends LinearOpMode {
    Robot2023 robot;
    final FieldPositions.Team team = FieldPositions.Team.Blue;
    final FieldPositions.StartingPosition startingPosition = FieldPositions.StartingPosition.Back;
    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot2023(this, new MecanumDrive(hardwareMap, FieldPositions.getStartingPose(startingPosition, team)), true, false, false, true, true, true);
        robot.autonomousController.setSettings(startingPosition, team, false, true, true);
        robot.onOpmodeInit();
        waitForStart();

        while (opModeIsActive()){
            robot.doLoop(gamepad1, gamepad2);
            idle();
        }
    }
}