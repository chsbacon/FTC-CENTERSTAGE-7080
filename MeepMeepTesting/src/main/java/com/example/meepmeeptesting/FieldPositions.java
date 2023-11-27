package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.roadrunner.DriveShim;


public class FieldPositions {
    public enum StartingPosition {
        Front,
        Back,
    }
    public enum Team {
        Red,
        Blue
    }
    public enum SpikeMarkLocation {
        Left,
        Center,
        Right
    }
    public static Vector2d getRobotSize(){
        return new Vector2d(14.5, 14.6);
        // x is forward/back, y is left/right
    }
    public static Pose2d getStartingPose(StartingPosition startingPosition, Team team){
        // centered in tile, FACING INTO THE WALL
        if (startingPosition == StartingPosition.Front){
            if (team == Team.Red){
                return new Pose2d(72 - getRobotSize().x/2, -36, 0); // red front
            } else {
                return new Pose2d(-72 + getRobotSize().x/2, -36, Math.PI); // blue front
            }
        } else {
            if (team == Team.Red){
                return new Pose2d(72 - getRobotSize().x/2, 12, 0); // red back
            } else {
                return new Pose2d(-72 + getRobotSize().x/2, 12, Math.PI); // blue back
            }
        }
    }
    public static Action getTrajToSpikeMark(DriveShim drive, StartingPosition startingPosition, Team team, SpikeMarkLocation spikeMarkLocation){
        Vector2d targetPosition = getPurpleScoreTarget(startingPosition, team, spikeMarkLocation);
        double departureHeading;
        switch(team){
            case Red:
                departureHeading = Math.PI;
                break;
            case Blue:
                departureHeading = 0;
                break;
            default:
                departureHeading = 0;
                break;
        }
        double arrivalHeading = getPurpleScoreArrivalHeading(team, spikeMarkLocation);

        return drive.actionBuilder(getStartingPose(startingPosition, team))
                .setTangent(departureHeading)
                .splineTo(targetPosition, arrivalHeading)
                .build();
    }

    private static double getPurpleScoreArrivalHeading(Team team, SpikeMarkLocation spikeMarkLocation) {
        double arrivalHeading = Math.PI;
        switch (spikeMarkLocation){
            case Left:
                arrivalHeading = Math.PI/2;
                break;
            case Center:
                arrivalHeading = 0;
                break;
            case Right:
                arrivalHeading = -Math.PI/2;
                break;
            default:
                arrivalHeading = 0;
                break;
        }
        if (team == Team.Red){
            arrivalHeading += Math.PI;
        }
        return arrivalHeading;
    }

    private static Vector2d getPurpleScoreTarget(StartingPosition startingPosition, Team team, SpikeMarkLocation spikeMarkLocation) {
        // center of shovel is end of robot, so we only need to get to (spike mark center) - (robot size)/2, but also add in (robot size)/2 because it's the back of the robot that starts against the wall
        // raw centers of spike marks, in field reference frame relative to robot start:
        Vector2d blueLeftSpikeTargetPosition = new Vector2d(42 - getRobotSize().x/2, -12 + getRobotSize().x);
        Vector2d blueCenterSpikeTargetPosition = new Vector2d(48- (getRobotSize().x), 0);
        Vector2d blueRightSpikeTargetPosition = new Vector2d(42 - getRobotSize().x/2, 12- getRobotSize().x);
        Vector2d redLeftSpikeTargetPosition = new Vector2d(-42 + getRobotSize().x/2, 12 - getRobotSize().x);
        Vector2d redCenterSpikeTargetPosition = new Vector2d(-48 + getRobotSize().x, 0);
        Vector2d redRightSpikeTargetPosition = new Vector2d(-42 + getRobotSize().x/2, -12 + getRobotSize().x);

        Vector2d targetPosition;
        if (team == Team.Red){
            if (spikeMarkLocation == SpikeMarkLocation.Left){
                targetPosition = redLeftSpikeTargetPosition;
            } else if (spikeMarkLocation == SpikeMarkLocation.Center){
                targetPosition = redCenterSpikeTargetPosition;
            } else {
                targetPosition = redRightSpikeTargetPosition;
            }
        } else {
            if (spikeMarkLocation == SpikeMarkLocation.Left){
                targetPosition = blueLeftSpikeTargetPosition;
            } else if (spikeMarkLocation == SpikeMarkLocation.Center){
                targetPosition = blueCenterSpikeTargetPosition;
            } else {
                targetPosition = blueRightSpikeTargetPosition;
            }
        }

        targetPosition = getStartingPose(startingPosition, team).position.plus(targetPosition);
        return targetPosition;
    }

    // absolute position this time, just for funsies ;P
    public static final Vector2d blueFrontStagePoint = new Vector2d(-12, -24);
    public static final Vector2d bluePrescorePoint = new Vector2d(-16, 60 - getRobotSize().x / 2);
    public static final Vector2d redFrontStagePoint = new Vector2d(12, -24);
    public static final Vector2d redPrescorePoint = new Vector2d(16, 60 - getRobotSize().x / 2);
    public static final Vector2d blueFrontSecondaryEscapePoint = new Vector2d(-24, -56);
    public static final Vector2d blueBackSecondaryEscapePoint = new Vector2d(-20, 11);
    public static final Vector2d redFrontSecondaryEscapePoint = new Vector2d(24, -56);
    public static final Vector2d redBackSecondaryEscapePoint = new Vector2d(20, 11);
    public static Action getTrajEscapeSpikeMark(DriveShim drive, StartingPosition startingPosition, Team team, SpikeMarkLocation spikeMarkLocation){

        Vector2d firstTargetPosition;
        if (team == Team.Red){
            if (startingPosition == StartingPosition.Front){
                firstTargetPosition = redFrontStagePoint;
            } else {
                firstTargetPosition = redPrescorePoint;
            }
        } else {
            if (startingPosition == StartingPosition.Front){
                firstTargetPosition = blueFrontStagePoint;
            } else {
                firstTargetPosition = bluePrescorePoint;
            }
        }
        double departureHeading;
        switch (spikeMarkLocation){
            case Left:
                departureHeading = -Math.PI/2;
                break;
            case Center:
                departureHeading = Math.PI;
                break;
            case Right:
                departureHeading = +Math.PI/2;
                break;
            default:
                departureHeading = 0;
                break;
        }
        if (team == Team.Red){
            departureHeading += Math.PI;
        }
        Vector2d secondaryEscapePoint = null;
        // secondary escape point needed if starting from front position and spike in center
        // or starting ihn back and spike in blue: left or red: right
        if (startingPosition == StartingPosition.Front && spikeMarkLocation == SpikeMarkLocation.Center){
            if (team == Team.Red){
                secondaryEscapePoint = redFrontSecondaryEscapePoint;
            } else {
                secondaryEscapePoint = blueFrontSecondaryEscapePoint;
            }
        } else if (startingPosition == StartingPosition.Back && spikeMarkLocation == SpikeMarkLocation.Left && team == Team.Blue){
            secondaryEscapePoint = blueBackSecondaryEscapePoint;
        } else if (startingPosition == StartingPosition.Back && spikeMarkLocation == SpikeMarkLocation.Right && team == Team.Red){
            secondaryEscapePoint = redBackSecondaryEscapePoint;
        }
        double secondaryEscapeArrivalHeading = 0;
        if (team == Team.Red){
            secondaryEscapeArrivalHeading = Math.PI;
        }
        Action result;
        Pose2d startingPose = new Pose2d(getPurpleScoreTarget(startingPosition, team, spikeMarkLocation), getPurpleScoreArrivalHeading(team, spikeMarkLocation)+Math.PI);
        if(startingPosition == StartingPosition.Back){
            if(secondaryEscapePoint != null){
                result = drive.actionBuilder(startingPose)
                        .setTangent(departureHeading)
                        .splineToLinearHeading(new Pose2d(secondaryEscapePoint, departureHeading), secondaryEscapeArrivalHeading)
                        .splineToLinearHeading(new Pose2d(firstTargetPosition, Math.PI/2), Math.PI/2)
                        .build();
            } else {
                result = drive.actionBuilder(startingPose)
                        .setTangent(departureHeading)
                        .splineToLinearHeading(new Pose2d(firstTargetPosition, Math.PI/2), Math.PI/2)
                        .build();
            }

        } else {
            Vector2d secondTargetPosition;
            if (team == Team.Red){
                secondTargetPosition = redPrescorePoint;
            } else {
                secondTargetPosition = bluePrescorePoint;
            }
            if (secondaryEscapePoint != null){
                result = drive.actionBuilder(startingPose)
                        .setTangent(departureHeading)
                        .splineToLinearHeading(new Pose2d(secondaryEscapePoint, departureHeading), secondaryEscapeArrivalHeading)
                        .splineToLinearHeading(new Pose2d(firstTargetPosition, Math.PI/2), Math.PI/2)
                        .splineToLinearHeading(new Pose2d(secondTargetPosition, Math.PI/2), Math.PI/2)
                        .build();
            } else {
                result = drive.actionBuilder(startingPose)
                        .setTangent(departureHeading)
                        .splineToLinearHeading(new Pose2d(firstTargetPosition, Math.PI / 2), Math.PI / 2)
                        .splineToLinearHeading(new Pose2d(secondTargetPosition, Math.PI / 2), Math.PI / 2)
                        .build();
            }
        }
        return result;
    }
    public static final Vector2d blueBackboardCenterScoreTarget = new Vector2d(-36, 60 - getRobotSize().x / 2);
    public static final Vector2d blueBackboardLeftScoreTarget = new Vector2d(-42, 60 - getRobotSize().x / 2);
    public static final Vector2d blueBackboardRightScoreTarget = new Vector2d(-30, 60 - getRobotSize().x / 2);
    public static final Vector2d redBackboardCenterScoreTarget = new Vector2d(36, 60 - getRobotSize().x / 2);
    public static final Vector2d redBackboardLeftScoreTarget = new Vector2d(30, 60 - getRobotSize().x / 2);
    public static final Vector2d redBackboardRightScoreTarget = new Vector2d(42, 60 - getRobotSize().x / 2);
    public static Action getTrajToScore(DriveShim drive, StartingPosition startingPosition, Team team, SpikeMarkLocation spikeMarkLocation) {
        // assume we're starting from the prescore point


        Vector2d targetPosition = getScorePoint(team, spikeMarkLocation);
        double arrivalHeading;
        if (team == Team.Red) {
            arrivalHeading = 0;
        } else {
            arrivalHeading = Math.PI;
        }
        Pose2d startingPose;
        if (team == Team.Red){
            startingPose = new Pose2d(redPrescorePoint, Math.PI/2);
        } else {
            startingPose = new Pose2d(bluePrescorePoint, Math.PI/2);
        }
        Action result = drive.actionBuilder(startingPose)
                .setTangent(arrivalHeading)
                .splineToConstantHeading(targetPosition, arrivalHeading)
                .build();
        return result;
    }


    private static Vector2d getScorePoint(Team team, SpikeMarkLocation spikeMarkLocation) {
        Vector2d targetPosition;
        if (team == Team.Red) {
            if (spikeMarkLocation == SpikeMarkLocation.Left) {
                targetPosition = redBackboardLeftScoreTarget;
            } else if (spikeMarkLocation == SpikeMarkLocation.Center) {
                targetPosition = redBackboardCenterScoreTarget;
            } else {
                targetPosition = redBackboardRightScoreTarget;
            }
        } else {
            if (spikeMarkLocation == SpikeMarkLocation.Left) {
                targetPosition = blueBackboardLeftScoreTarget;
            } else if (spikeMarkLocation == SpikeMarkLocation.Center) {
                targetPosition = blueBackboardCenterScoreTarget;
            } else {
                targetPosition = blueBackboardRightScoreTarget;
            }
        }
        return targetPosition;
    }

    public static final Vector2d blueParkTarget = new Vector2d(-9, 70 - getRobotSize().x / 2);
    public static final Vector2d redParkTarget = new Vector2d(9, 70 - getRobotSize().x / 2);
    public static Action getTrajToPark(DriveShim drive, StartingPosition startingPosition, Team team, SpikeMarkLocation spikeMarkLocation, boolean didScoreBackboard) {
        // we could be starting from either prescore or score position
        // so first, go to the prescore (if we're already there nothing happens)
        // then, slide into park position

        Vector2d firstTargetPosition;
        if (team == Team.Red) {
            firstTargetPosition = redPrescorePoint;
        } else {
            firstTargetPosition = bluePrescorePoint;
        }
        Vector2d secondTargetPosition;
        if (team == Team.Red) {
            secondTargetPosition = redParkTarget;
        } else {
            secondTargetPosition = blueParkTarget;
        }
        double departureHeading;
        if (team == Team.Red) {
            departureHeading = Math.PI;
        } else {
            departureHeading = 0;
        }
        Pose2d startingPose;
        if(didScoreBackboard){
            startingPose = new Pose2d(getScorePoint(team, spikeMarkLocation), Math.PI/2);
        } else {
            // we're at the prescore point
            // some bug in meepmeep/roadrunner means we can't start *exactly* where we stop
            // but starting a ten thousandth of an inch off is fine
            if (team == Team.Red){
                startingPose = new Pose2d(redPrescorePoint.plus(new Vector2d(0.0001,0.0001)), Math.PI/2);
            } else {
                startingPose = new Pose2d(bluePrescorePoint.plus(new Vector2d(0.0001,0.0001)), Math.PI/2);
            }
        }

        Action result = drive.actionBuilder(startingPose)
                .setTangent(departureHeading)
                .splineToConstantHeading(firstTargetPosition, departureHeading)
                .splineToLinearHeading(new Pose2d(secondTargetPosition, departureHeading), Math.PI/2)
                .build();
        return result;
    }
}
