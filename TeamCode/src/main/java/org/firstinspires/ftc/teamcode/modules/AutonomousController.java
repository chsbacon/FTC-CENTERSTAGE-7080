package org.firstinspires.ftc.teamcode.modules;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

public class AutonomousController {

    /*
            An attempt to make the autonomous system a bit less repetitive
            and a bit easier to change given the number of starting positions
             */
    /*
    Autonomous steps:
    1. Using vision, detect where the team prop is
    2. Score the purple pixel on the correct spike mark
    3. Escape the spike mark area to a known location on the 3rd tile column of the field, being sure not to to run over the spike mark with stuff on it
    4. From the known area, go to the correct spot on the backboard and score the yellow pixel
    5. Park
     */
    enum AutoState {
        NotStarted,
        RunningActions,
        Finished
    }
    private FieldPositions.StartingPosition startingPosition;
    private FieldPositions.Team team;
    private boolean doScoreBackboard;
    private boolean doPark;
    private AutoState autoState = AutoState.NotStarted;
    private Robot2023 robot;
    private Telemetry telemetry;
    private FieldPositions.SpikeMarkLocation spikeMarkLocation;
    public void setSettings(FieldPositions.StartingPosition startingPosition, FieldPositions.Team team, boolean doScoreBackboard, boolean doPark){
        this.startingPosition = startingPosition;
        this.team = team;
        this.doScoreBackboard = doScoreBackboard;
        this.doPark = doPark;
        this.autoState = AutoState.NotStarted;
    }
    public void onOpmodeInit(Robot2023 robot, Telemetry telemetry){
        this.robot = robot;
        this.telemetry = telemetry;
    }
    public void doLoop(){
        switch (autoState){
            case NotStarted:
                // get a reading from the camera, then start if the reading is valid
                // if the robot's tfod isn't running then skip this step and assume we're center spike mark
                // after getting a reading, figure out where we're headed and set it into the robot's current action
                // all of this is a bit repetitive but code now refactor later (after comp)
                ArrayList actions = new ArrayList<Action>();
                if(robot.tfodController != null){
                    double lastX = robot.tfodController.lastX;
                    // here's where the thresholds for position detection are
                    // with of screen is 640, so have the windows be each third of the screen
                    if(lastX < 640/3){
                        // left spike mark
                        spikeMarkLocation = FieldPositions.SpikeMarkLocation.Left;
                    } else if (lastX < 640*2/3){
                        // center spike mark
                        spikeMarkLocation = FieldPositions.SpikeMarkLocation.Center;
                    } else {
                        // right spike mark
                        spikeMarkLocation = FieldPositions.SpikeMarkLocation.Right;
                    }
                } else {
                    // if the tfod isn't running, assume we're center spike mark
                    spikeMarkLocation = FieldPositions.SpikeMarkLocation.Center;
                }
                // now that we know where we're headed, set up the actions
                // first, score the purple pixel on the correct spike mark
                actions.add(FieldPositions.getTrajToSpikeMark(robot.drive, startingPosition, team, spikeMarkLocation));
                // then, if we're parking and/or scoring, go to the prescore point
                if (doPark || doScoreBackboard){
                    actions.add(FieldPositions.getTrajEscapeSpikeMark(robot.drive, startingPosition, team, spikeMarkLocation));
                }
                // then, if we're scoring, go to the backboard and score
                if (doScoreBackboard){
                    actions.add(FieldPositions.getTrajToScore(robot.drive, startingPosition, team, spikeMarkLocation));
                    actions.add(new SequentialAction(robot.armController.openClaw(),new SleepAction(1))); // todo: also add arm
                }
                // then, if we're parking, go to the parking spot
                if (doPark){
                    actions.add(FieldPositions.getTrajToPark(robot.drive, startingPosition, team, spikeMarkLocation));
                }

                // now, construct into one big SequentialAction
                Action theAction = new SequentialAction(actions);
                // and set it into the robot
                robot.setCurrentAction(theAction);
                autoState = AutoState.RunningActions;
                break;
            case RunningActions:
                // don't do anything until the action is done (robot.getCurrentAction() == null)
                if (robot.getCurrentAction() == null){
                    autoState = AutoState.Finished;
                }
                break;
            case Finished:
                // continue doing nothing
                break;
        }
    }
}
