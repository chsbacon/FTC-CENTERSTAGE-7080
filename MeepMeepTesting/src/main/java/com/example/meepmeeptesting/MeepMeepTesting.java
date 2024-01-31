package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SequentialAction;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import com.example.meepmeeptesting.OldClawTrajectories.StartingPosition;
import com.example.meepmeeptesting.OldClawTrajectories.Team;
import com.example.meepmeeptesting.OldClawTrajectories.SpikeMarkLocation;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(50, 50, Math.toRadians(180), Math.toRadians(180), 14)
                .build();
        myBot.setDimensions(OldClawTrajectories.getRobotSize().y, OldClawTrajectories.getRobotSize().x);

        // run sequence for state for every combination of spike mark location, team, and starting position
        ArrayList<Action> actions = new ArrayList<Action>();
        for (KookyClawTrajectories.Team team : KookyClawTrajectories.Team.values()){
            for (KookyClawTrajectories.StartingPosition startingPosition : KookyClawTrajectories.StartingPosition.values()){
                for (KookyClawTrajectories.SpikeMarkLocation spikeMarkLocation : KookyClawTrajectories.SpikeMarkLocation.values()){
                    actions.add(getSequenceForKooky(myBot, spikeMarkLocation, team, startingPosition));
                }
            }
        }
        // run a sequential action containing all of actions
        Action allActions = new SequentialAction(actions);
        myBot.runAction(allActions);
        //myBot.runAction(getSequenceForState(myBot, SpikeMarkLocation.Center, Team.Blue, StartingPosition.Front));

        Image img = null;
        try { img = ImageIO.read(new File("./MeepMeepTesting/field-2023-official.png")); }
        catch (IOException e) {}

        meepMeep.setBackground(img)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }

    private static Action getSequenceForKooky(RoadRunnerBotEntity myBot, KookyClawTrajectories.SpikeMarkLocation spikeMarkLocation, KookyClawTrajectories.Team team, KookyClawTrajectories.StartingPosition startingPosition) {
        //return FieldPositions.getStraightToScoreFromBack(myBot.getDrive(), StartingPosition.Back, team, spikeMarkLocation);
        Action purplePixelTraj = KookyClawTrajectories.getPurplePixelTraj(myBot.getDrive(), startingPosition, team, spikeMarkLocation, new SequentialAction());
        Action goBackboardTraj = KookyClawTrajectories.getTrajToBackboard(myBot.getDrive(), startingPosition, team, spikeMarkLocation);
        Action finishTraj = KookyClawTrajectories.getTrajToParkFromBackboard(myBot.getDrive(), team, spikeMarkLocation);

        return
                new SequentialAction(
                        purplePixelTraj,
                        goBackboardTraj,
                        finishTraj
                );
    }

    private static Action getSequenceForState(RoadRunnerBotEntity myBot, SpikeMarkLocation spikeMarkLocation, Team team, StartingPosition startingPosition) {
        //return FieldPositions.getStraightToScoreFromBack(myBot.getDrive(), StartingPosition.Back, team, spikeMarkLocation);
        Action goToSpikeMark = OldClawTrajectories.getTrajToSpikeMark(myBot.getDrive(), startingPosition, team, spikeMarkLocation);
        Action goToPrescorePoint = OldClawTrajectories.getTrajEscapeSpikeMark(myBot.getDrive(), startingPosition, team, spikeMarkLocation, true);
        Action scoreBackboard = OldClawTrajectories.getTrajToScore(myBot.getDrive(), startingPosition, team, spikeMarkLocation);
        Action park = OldClawTrajectories.getTrajToPark(myBot.getDrive(), startingPosition, team, spikeMarkLocation, true);
        return
                new SequentialAction(
                        goToSpikeMark,
                        goToPrescorePoint,
                        scoreBackboard,
                        park
                );
    }
}