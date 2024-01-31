package org.firstinspires.ftc.teamcode.modules;

import static com.acmerobotics.roadrunner.ftc.Actions.runBlocking;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
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
    private KookyClawTrajectories.StartingPosition startingPosition;
    public KookyClawTrajectories.Team team;
    private boolean doScoreBackboard;
    private boolean doPark;
    private boolean doEarlyScore;
    private AutoState autoState = AutoState.NotStarted;
    private Robot2023 robot;
    private Telemetry telemetry;
    private KookyClawTrajectories.SpikeMarkLocation spikeMarkLocation;
    ActionExecutor actionExecutor = new ActionExecutor();
    public void setSettings(KookyClawTrajectories.StartingPosition startingPosition, KookyClawTrajectories.Team team){
        this.startingPosition = startingPosition;
        this.team = team;
        this.autoState = AutoState.NotStarted;
    }
    public void onOpmodeInit(Robot2023 robot, Telemetry telemetry){
        this.robot = robot;
        this.telemetry = telemetry;
        robot.armController.closeLeftClaw();
        robot.armController.closeRightClaw();
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
                        spikeMarkLocation = KookyClawTrajectories.SpikeMarkLocation.Left;
                    } else if (lastX < 640*2/3){
                        // center spike mark
                        spikeMarkLocation = KookyClawTrajectories.SpikeMarkLocation.Center;
                    } else {
                        // right spike mark
                        spikeMarkLocation = KookyClawTrajectories.SpikeMarkLocation.Right;
                    }
                    telemetry.log().add("LastX is " + robot.tfodController.lastX + ", so spike mark location is " + spikeMarkLocation.toString());
                } else {
                    // if the tfod isn't running, assume we're center spike mark
                    spikeMarkLocation = KookyClawTrajectories.SpikeMarkLocation.Center;
                    telemetry.log().add("Cound not get TFOD controller, assuming center mark");
                }

                actions.add(KookyClawTrajectories.getPurplePixelTraj(robot.drive, startingPosition, team, spikeMarkLocation, robot.armController.openLeftClawAction()));
                actions.add(KookyClawTrajectories.getPurpleOnlyFinishTraj(robot.drive, startingPosition, team, spikeMarkLocation));

                // now, construct into one big SequentialAction
                Action theAction = new SequentialAction(actions);
                // and set it into the robot
                actionExecutor.setAction(theAction);
                autoState = AutoState.RunningActions;
                telemetry.log().add("Action constructed, running...");
//                runBlocking(theAction);
//                telemetry.log().add("Run complete!");
                break;
            case RunningActions:
                actionExecutor.doLoop();
                if (!actionExecutor.actionIsActive()){
                    autoState = AutoState.Finished;
                    telemetry.log().add("Finished auto!");
                }
                break;
            case Finished:
                // continue doing nothing
                // robot.opMode.stop();
                break;
        }
    }
}
