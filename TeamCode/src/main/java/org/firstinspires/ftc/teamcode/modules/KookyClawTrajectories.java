package org.firstinspires.ftc.teamcode.modules;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;


import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;

import java.util.Vector;

public class KookyClawTrajectories {
    public enum StartingPosition {
        Front,
        Back,
    }
    public enum Team {
        Red,
        Blue
    }
    public enum TrussRelativeLocation{
        Inside, // spike mark location is adjacent to the truss
        Center,
        Outside // spike mark location is opposite the truss
    }
    public static TrussRelativeLocation getTRL(SpikeMarkLocation spikeMarkLocation, StartingPosition startingPosition, Team team){
        if (spikeMarkLocation == SpikeMarkLocation.Center){
            return TrussRelativeLocation.Center;
        }
        boolean insideIsLeft = (startingPosition == StartingPosition.Front) ^ (team == Team.Red); // ^ is xor (one but not both)
        if (spikeMarkLocation == SpikeMarkLocation.Left){
            return insideIsLeft ? TrussRelativeLocation.Inside : TrussRelativeLocation.Outside;
        } else {
            return insideIsLeft ? TrussRelativeLocation.Outside : TrussRelativeLocation.Inside;
        }
    }
    public enum SpikeMarkLocation {
        Left,
        Center,
        Right
    }
    public static Vector2d getRobotSize(){
        return new Vector2d(14, 15.1);
        // x is forward/back, y is left/right
    }
    public static final double sideShieldLength = 3;
    public static final Vector2d pixelRobotOffset = new Vector2d(getRobotSize().x/2 + 1, -2); // left claw
    public static Pose2d getStartingPose(StartingPosition startingPosition, Team team){
        // centered in tile, FACING INTO THE WALL
        if (startingPosition == StartingPosition.Front){
            if (team == Team.Red){
                return new Pose2d(72 - getRobotSize().x/2 - sideShieldLength, -36, 0); // red front
            } else {
                return new Pose2d(-72 + getRobotSize().x/2 + sideShieldLength, -36, Math.PI); // blue front
            }
        } else {
            if (team == Team.Red){
                return new Pose2d(72 - getRobotSize().x/2 - sideShieldLength, 12, 0); // red back
            } else {
                return new Pose2d(-72 + getRobotSize().x/2 + sideShieldLength, 12, Math.PI); // blue back
            }
        }
    }
    public static Pose2d getPurplePixelFinalPose(StartingPosition startingPosition, Team team, SpikeMarkLocation spikeMarkLocation){
        // final pose after purple pixel is deposited
        // for red vs blue it's just a reflection over the y axis
        // when pixel is Inside, this pose is in the outside (y pos -48/24) of the spike mark tile (x pos +- 36), facing the truss
        // when pixel is Center, this pose is in the middle (y pos -36/12) of the middle tile (x pos +- 18), facing the wall
        // when pixel is Outside, this pose is on the outside (y pos -48/24) edge of the middle tile (x pos +- 18), facing the wall

        TrussRelativeLocation trl = getTRL(spikeMarkLocation, startingPosition, team);

        Pose2d depositPose = getPurplePixelDepositPose(startingPosition, team, spikeMarkLocation);

        // start with red poses, reflect later
        Pose2d result;
        if(trl == TrussRelativeLocation.Center){
            if(startingPosition == StartingPosition.Front){
                result = new Pose2d(12, -36, 0);
            } else {
                result = new Pose2d(12, 12, 0);
            }
        } else if(trl == TrussRelativeLocation.Inside){
            if(startingPosition == StartingPosition.Front){
                result = new Pose2d(depositPose.position.x, -48, Math.PI/2);
            } else {
                result = new Pose2d(depositPose.position.x, 24, -Math.PI/2);
            }
        } else if(trl == TrussRelativeLocation.Outside){
            result = new Pose2d(12, depositPose.position.y, 0);
        } else {
            // unreachable
            result = new Pose2d(0, 0, 0);
        }

        // now reflect if we're blue (unless we're inside, in which case we already mirrored in depositPose)
        if (team == Team.Blue && trl != TrussRelativeLocation.Inside){
            result = new Pose2d(new Vector2d(-result.position.x, result.position.y), new Rotation2d(-result.heading.real, result.heading.imag)); // rotator2d is playing with complex numbers to mirror angle over Y axis
        }

        return result;
    }
    public static Pose2d getPurplePixelDepositPose(StartingPosition startingPosition, Team team, SpikeMarkLocation spikeMarkLocation){
        // pose to deposit purple pixel
        // for red vs blue it's just a reflection over the y axis
        // this is all about getting the claw over the spike mark line
        // the claw center is clawCenterOffset inches from the front of the robot

        TrussRelativeLocation trl = getTRL(spikeMarkLocation, startingPosition, team);

        // start with red poses, reflect later
        Pose2d result;
        if(trl == TrussRelativeLocation.Center){
            if(startingPosition == StartingPosition.Front){
                result = new Pose2d(24 - pixelRobotOffset.x, -36, 0);
            } else {
                result = new Pose2d(24 - pixelRobotOffset.x, 12, 0);
            }
        } else if(trl == TrussRelativeLocation.Inside){
            if(startingPosition == StartingPosition.Front){
                result = new Pose2d(30, -24 - pixelRobotOffset.x, Math.PI/2);
            } else {
                result = new Pose2d(30, 0 + pixelRobotOffset.x, -Math.PI/2);
            }
        } else if(trl == TrussRelativeLocation.Outside){
            if(startingPosition == StartingPosition.Front){
                result = new Pose2d(30 - pixelRobotOffset.x, -46 + pixelRobotOffset.y, 0);
            } else {
                result = new Pose2d(30 - pixelRobotOffset.x, 22 + pixelRobotOffset.y, 0);
            }
        } else {
            // unreachable
            result = new Pose2d(0, 0, 0);
        }

        // now reflect if we're blue
        if (team == Team.Blue) {
            result = new Pose2d(new Vector2d(-result.position.x, result.position.y), new Rotation2d(-result.heading.real, result.heading.imag)); // rotator2d is playing with complex numbers to mirror angle over Y axis
            // and if we're outside we need to also invert the pixel-robot offset
            if (trl == TrussRelativeLocation.Outside){
                result = new Pose2d(new Vector2d(result.position.x, result.position.y - 2 * pixelRobotOffset.y), result.heading);
            }
        }

        return result;
    }
    public static Action getPurplePixelTraj(MecanumDrive drive, StartingPosition startingPosition, Team team, SpikeMarkLocation spikeMarkLocation, Action clawReleaseAction){
        // this starts at startingPose, deposits the purple pixel, and then backs up to a final position
        // the final position is different depending on the spike mark location
        double departureHeading = getStartingPose(startingPosition, team).heading.log() + Math.PI; // leave the wall backwards
        Pose2d depositPose = getPurplePixelDepositPose(startingPosition, team, spikeMarkLocation);
        Rotation2d depositHeading = new Rotation2d(-depositPose.heading.real, depositPose.heading.imag); // a trick to arrive at inside poses facing forward but outside/center poses facing backward
        Pose2d finalPose = getPurplePixelFinalPose(startingPosition, team, spikeMarkLocation);

        return new SequentialAction(
                drive.actionBuilder(getStartingPose(startingPosition, team))
                        .setTangent(departureHeading)
                        .splineToLinearHeading(depositPose, depositHeading)
                        .build(),
                clawReleaseAction,
                drive.actionBuilder(depositPose)
                        .setTangent(finalPose.heading.log() + Math.PI) // leave in the robot-backwards direction
                        .splineToLinearHeading(finalPose, finalPose.heading.log() + Math.PI) // arrive in the robot-backwards direction
                        .build()
        );

    }
    public static Action getPurpleOnlyFinishTraj(MecanumDrive drive, StartingPosition startingPosition, Team team, SpikeMarkLocation spikeMarkLocation){
        // this is called if we are only scoring purple
        // it parks facing away from the wall
        return drive.actionBuilder(getPurplePixelFinalPose(startingPosition, team, spikeMarkLocation))
                .turnTo(team == Team.Red ? Math.PI : 0)
                .build();
    }
}
