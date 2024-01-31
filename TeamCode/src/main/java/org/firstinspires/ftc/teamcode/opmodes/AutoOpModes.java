package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.KookyClawTrajectories;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

public class AutoOpModes {
    @OpModeRegistrar
    public static void registerMyOpModes(OpModeManager manager) {
        // register a permutation for every possible combination of starting position, team, and whether or not to score the backboard and park
        for (KookyClawTrajectories.Team team : KookyClawTrajectories.Team.values()){
            for (KookyClawTrajectories.StartingPosition startingPosition : KookyClawTrajectories.StartingPosition.values()){
                //for (boolean doScoreBackboard : new boolean[]{true, false}){
                    //for (boolean doPark : new boolean[]{true, false}){
                        registerPermutation(manager, team, startingPosition, false, false); //doScoreBackboard, doPark);
                    //}
                //}
            }
        }
    }
    public static void registerPermutation(OpModeManager manager, KookyClawTrajectories.Team team, KookyClawTrajectories.StartingPosition startingPosition, boolean doScoreBackboard, boolean doPark){
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
        builder.setTransitionTarget("IntegratedOpMode"); // automatically select the correct teleop after auto ends
        manager.register(builder.build(), new AutonomousOpMode(team, startingPosition, doScoreBackboard, doPark));
    }
}

class AutonomousOpMode extends LinearOpMode {
    Robot2023 robot;
    final KookyClawTrajectories.Team team;
    final KookyClawTrajectories.StartingPosition startingPosition;
    final boolean doScoreBackboard;
    final boolean doPark;
    AutonomousOpMode(KookyClawTrajectories.Team team, KookyClawTrajectories.StartingPosition startingPosition, boolean doScoreBackboard, boolean doPark){
        this.team = team;
        this.startingPosition = startingPosition;
        this.doScoreBackboard = doScoreBackboard;
        this.doPark = doPark;
    }
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