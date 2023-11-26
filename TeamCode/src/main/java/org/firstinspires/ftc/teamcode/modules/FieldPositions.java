package org.firstinspires.ftc.teamcode.modules;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Trajectory;
import com.acmerobotics.roadrunner.TrajectoryBuilder;
import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;

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
    public static Action getTrajToSpikeMark(MecanumDrive drive, StartingPosition startingPosition, Team team, SpikeMarkLocation spikeMarkLocation){
        // center of shovel is end of robot, so we only need to get to (spike mark center) - (robot size)/2, but also add in (robot size)/2 because it's the back of the robot that starts against the wall
        // raw centers of spike marks, in field reference frame relative to robot start:
        Vector2d blueLeftSpikeTargetPosition = new Vector2d(42 - getRobotSize().x/2, 12 - getRobotSize().x);
        Vector2d blueCenterSpikeTargetPosition = new Vector2d(48- (getRobotSize().x), 0);
        Vector2d blueRightSpikeTargetPosition = new Vector2d(42 - getRobotSize().x/2, -12+ getRobotSize().x);
        Vector2d redLeftSpikeTargetPosition = new Vector2d(-42 + getRobotSize().x/2, -12+ getRobotSize().x);
        Vector2d redCenterSpikeTargetPosition = new Vector2d(-48 + getRobotSize().x, 0);
        Vector2d redRightSpikeTargetPosition = new Vector2d(-42 + getRobotSize().x/2, 12 - getRobotSize().x);

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
        double arrivalHeading = Math.PI;
        switch (spikeMarkLocation){
            case Left:
                arrivalHeading = -Math.PI/2;
                break;
            case Center:
                arrivalHeading = 0;
                break;
            case Right:
                arrivalHeading = Math.PI/2;
                break;
            default:
                arrivalHeading = 0;
                break;
        }
        if (team == Team.Red){
            arrivalHeading += Math.PI;
        }

        return drive.actionBuilder(getStartingPose(startingPosition, team))
                .setTangent(departureHeading)
                .splineTo(targetPosition, arrivalHeading)
                .build();
    }
    public static Action getTrajEscapeSpikeMark(MecanumDrive drive, StartingPosition startingPosition, Team team, SpikeMarkLocation spikeMarkLocation){
        // absolute position this time, just for funsies ;P
        Vector2d blueFrontStagePoint = new Vector2d(-12, -24);
        Vector2d bluePrescorePoint = new Vector2d(-12, 48);
        Vector2d redFrontStagePoint = new Vector2d(12, -24);
        Vector2d redPrescorePoint = new Vector2d(12, 48);
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
                departureHeading = Math.PI/2;
                break;
            case Center:
                departureHeading = Math.PI;
                break;
            case Right:
                departureHeading = -Math.PI/2;
                break;
            default:
                departureHeading = 0;
                break;
        }
        if (team == Team.Red){
            departureHeading += Math.PI;
        }
        if(startingPosition == StartingPosition.Back){
            return drive.actionBuilder(drive.pose)
                    .setTangent(departureHeading)
                    .splineToLinearHeading(new Pose2d(firstTargetPosition, Math.PI/2), Math.PI/2)
                    .build();
        } else {
            Vector2d secondTargetPosition;
            if (team == Team.Red){
                secondTargetPosition = redPrescorePoint;
            } else {
                secondTargetPosition = bluePrescorePoint;
            }
            return drive.actionBuilder(drive.pose)
                    .setTangent(departureHeading)
                    .splineTo(firstTargetPosition, Math.PI/2)
                    .splineToLinearHeading(new Pose2d(secondTargetPosition, Math.PI/2), Math.PI/2)
                    .build();
        }

    }
}
