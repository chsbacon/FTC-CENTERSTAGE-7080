package org.firstinspires.ftc.teamcode.modules;

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
        GoingToSpikeMark,
        GoingToPrescorePoint,
        GoingToBackboard,
        ScoringBackboard,
        Parking,
        Finished
    }
    private FieldPositions.StartingPosition startingPosition;
    private FieldPositions.Team team;
    private boolean doScoreBackboard;
    private boolean doPark;
    private AutoState autoState = AutoState.NotStarted;
    private Robot2023 robot;
    private FieldPositions.SpikeMarkLocation spikeMarkLocation;
    public void setSettings(FieldPositions.StartingPosition startingPosition, FieldPositions.Team team, boolean doScoreBackboard, boolean doPark){
        this.startingPosition = startingPosition;
        this.team = team;
        this.doScoreBackboard = doScoreBackboard;
        this.doPark = doPark;
        this.autoState = AutoState.NotStarted;
    }
    public void onOpmodeInit(Robot2023 robot){
        this.robot = robot;
    }
    public void doLoop(){
        switch (autoState){
            case NotStarted:
                // get a reading from the camera, then start if the reading is valid
                // if the robot's tfod isn't running then skip this step and assume we're center spike mark
                // after getting a reading, figure out where we're headed and set it into the robot's current action
                // all of this is a bit repetitive but code now refactor later (after comp)
                if(robot.tfodController != null){
                    double lastX = robot.tfodController.lastX;
                    // here's where the thresholds for position detection are
                    // with of screen is 640, so have the windows be each third of the screen
                    if(lastX < 640/3){
                        // left spike mark
                        spikeMarkLocation = FieldPositions.SpikeMarkLocation.Left;
                        robot.setCurrentAction(FieldPositions.getTrajToSpikeMark(robot.drive, startingPosition, team, spikeMarkLocation));
                        autoState = AutoState.GoingToSpikeMark;
                    } else if (lastX < 640*2/3){
                        // center spike mark
                        spikeMarkLocation = FieldPositions.SpikeMarkLocation.Center;
                        robot.setCurrentAction(FieldPositions.getTrajToSpikeMark(robot.drive, startingPosition, team, spikeMarkLocation));
                        autoState = AutoState.GoingToSpikeMark;
                    } else {
                        // right spike mark
                        spikeMarkLocation = FieldPositions.SpikeMarkLocation.Right;
                        robot.setCurrentAction(FieldPositions.getTrajToSpikeMark(robot.drive, startingPosition, team, spikeMarkLocation));
                        autoState = AutoState.GoingToSpikeMark;
                    }
                } else {
                    // if the tfod isn't running, assume we're center spike mark
                    spikeMarkLocation = FieldPositions.SpikeMarkLocation.Center;
                    robot.setCurrentAction(FieldPositions.getTrajToSpikeMark(robot.drive, startingPosition, team, spikeMarkLocation));
                    autoState = AutoState.GoingToSpikeMark;
                }
                break;
            case GoingToSpikeMark:
                // currently following the trajectory
                // this stage ends when the robot's current action finishes (i.e. is null)
                if(robot.getCurrentAction() == null){
                    // get the next route
                    robot.setCurrentAction(FieldPositions.getTrajEscapeSpikeMark(robot.drive, startingPosition, team, spikeMarkLocation));
                    autoState = AutoState.GoingToPrescorePoint;
                }
                break;
            case GoingToPrescorePoint:
                // currently following the trajectory
                // this stage ends when the robot's current action finishes (i.e. is null)

                break;
            case GoingToBackboard:
                // do nothing
                break;
            case ScoringBackboard:
                // do nothing
                break;
            case Parking:
                // do nothing
                break;
            case Finished:
                // do nothing
                break;
        }
    }
}
