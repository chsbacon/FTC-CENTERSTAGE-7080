package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(50, 50, Math.toRadians(180), Math.toRadians(180), 10.276)
                .build();

            Action getToBoard = myBot.getDrive().actionBuilder(new Pose2d(new Vector2d(-64, -36), 0))
                    .waitSeconds(3)
                    .splineTo(new Vector2d(-35, -30), Math.PI/2)
                    .splineTo(new Vector2d(-35, -12), Math.PI / 2)
                    .splineTo(new Vector2d(-36, 52), Math.PI/2)
                    .build();
        Action goPark = myBot.getDrive().actionBuilder(new Pose2d(new Vector2d(-36, 52), Math.PI/2)) // ending pose from last action
                .setTangent(0)
                .splineToConstantHeading(new Vector2d(-16, 52), 0)
                .splineToConstantHeading(new Vector2d(-9, 63), Math.PI/2)
                .waitSeconds(3)
                .build();
        myBot.runAction(
                new SequentialAction(
                        getToBoard,

                        goPark
                )
        );


        Image img = null;
        try { img = ImageIO.read(new File("./MeepMeepTesting/field-2023-official.png")); }
        catch (IOException e) {}

        meepMeep.setBackground(img)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}