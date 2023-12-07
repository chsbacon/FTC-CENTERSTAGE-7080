package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.FieldPositions;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

public class AutoOpModes {
    @OpModeRegistrar
    public static void registerMyOpModes(OpModeManager manager) {
        // register a permutation for every possible combination of starting position, team, and whether or not to score the backboard and park
        for (FieldPositions.Team team : FieldPositions.Team.values()){
            for (FieldPositions.StartingPosition startingPosition : FieldPositions.StartingPosition.values()){
                for (boolean doScoreBackboard : new boolean[]{true, false}){
                    for (boolean doPark : new boolean[]{true, false}){
                        registerPermutation(manager, team, startingPosition, doScoreBackboard, doPark);
                    }
                }
            }
        }
    }
    public static void registerPermutation(OpModeManager manager, FieldPositions.Team team, FieldPositions.StartingPosition startingPosition, boolean doScoreBackboard, boolean doPark){
        String group = team.toString()+startingPosition.toString();
        String name = group;
        if(doScoreBackboard){
            name += "Score";
        } else {
            name += "NoScore";
        }
        if(doPark){
            name += "Park";
        } else {
            name += "NoPark";
        }
        OpModeMeta.Builder builder = new OpModeMeta.Builder();
        builder.setFlavor(OpModeMeta.Flavor.AUTONOMOUS);
        builder.setName(name);
        builder.setGroup("Autonomous");
        manager.register(builder.build(), new AutonomousOpMode(team, startingPosition, doScoreBackboard, doPark));
    }
}

class AutonomousOpMode extends LinearOpMode {
    Robot2023 robot;
    final FieldPositions.Team team;
    final FieldPositions.StartingPosition startingPosition;
    final boolean doScoreBackboard;
    final boolean doPark;
    AutonomousOpMode(FieldPositions.Team team, FieldPositions.StartingPosition startingPosition, boolean doScoreBackboard, boolean doPark){
        this.team = team;
        this.startingPosition = startingPosition;
        this.doScoreBackboard = doScoreBackboard;
        this.doPark = doPark;
    }
    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot2023(this, new MecanumDrive(hardwareMap, FieldPositions.getStartingPose(startingPosition, team)), true, false, false, true, true, true);
        robot.autonomousController.setSettings(startingPosition, team, doScoreBackboard, doPark);
        robot.onOpmodeInit();
        waitForStart();

        while (opModeIsActive()){
            robot.doLoop(gamepad1, gamepad2);
            idle();
        }
    }
}