package org.firstinspires.ftc.teamcode.tuning;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.TankDrive;

public final class SplineTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.log().add("opmode started");
        if (TuningOpModes.DRIVE_CLASS.equals(MecanumDrive.class)) {
            MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
            telemetry.log().add("drive constructed");

            waitForStart();
            telemetry.log().add("started");

            Action action = drive.actionBuilder(drive.pose)
                    .splineTo(new Vector2d(30, 30), Math.PI / 2)
                    .splineTo(new Vector2d(60, 0), Math.PI)
                    .build();
            telemetry.log().add("action built");

            Actions.runBlocking(action);
            telemetry.log().add("finished");
        } else if (TuningOpModes.DRIVE_CLASS.equals(TankDrive.class)) {
            TankDrive drive = new TankDrive(hardwareMap, new Pose2d(0, 0, 0));

            waitForStart();

            Actions.runBlocking(
                    drive.actionBuilder(drive.pose)
                            .splineTo(new Vector2d(30, 30), Math.PI / 2)
                            .splineTo(new Vector2d(60, 0), Math.PI)
                            .build());
        } else {
            throw new AssertionError();
        }
    }
}
