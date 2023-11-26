package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import com.example.meepmeeptesting.FieldPositions;
import com.example.meepmeeptesting.FieldPositions.StartingPosition;
import com.example.meepmeeptesting.FieldPositions.Team;
import com.example.meepmeeptesting.FieldPositions.SpikeMarkLocation;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import jdk.javadoc.internal.tool.Start;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(50, 50, Math.toRadians(180), Math.toRadians(180), 14)
                .build();
        myBot.setDimensions(FieldPositions.getRobotSize().y, FieldPositions.getRobotSize().x);

        // run sequence for state for every combination of spike mark location, team, and starting position
        ArrayList<Action> actions = new ArrayList<Action>();
        for (Team team : Team.values()){
            for (StartingPosition startingPosition : StartingPosition.values()){
                for (SpikeMarkLocation spikeMarkLocation : SpikeMarkLocation.values()){
                    actions.add(getSequenceForState(myBot, spikeMarkLocation, team, startingPosition));
                }
            }
        }
        // run a seuqntial action containing all of actions
        Action allActions = new SequentialAction(actions);
        myBot.runAction(allActions);

        Image img = null;
        try { img = ImageIO.read(new File("./MeepMeepTesting/field-2023-official.png")); }
        catch (IOException e) {}

        meepMeep.setBackground(img)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }

    private static Action getSequenceForState(RoadRunnerBotEntity myBot, SpikeMarkLocation spikeMarkLocation, Team team, StartingPosition startingPosition) {
        Action goToSpikeMark = FieldPositions.getTrajToSpikeMark(myBot.getDrive(), startingPosition, team, spikeMarkLocation);
        Action goToPrescorePoint = FieldPositions.getTrajEscapeSpikeMark(myBot.getDrive(), startingPosition, team, spikeMarkLocation);
        return
                new SequentialAction(
                        goToSpikeMark,
                        goToPrescorePoint
                );
    }
}