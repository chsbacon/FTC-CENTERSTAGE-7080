package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseRaw;

import java.util.ArrayList;

@Autonomous
public class AutoVision extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive robot = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        waitForStart();
        while(!isStopRequested() && opModeIsActive()) {

            ArrayList<AprilTagDetection> detections = robot.tagProcessor.getDetections();
            if (detections.size() > 0) { //if tag detected
                AprilTagDetection tag = detections.get(0);
                if(tag.id>0 && tag.id<=10) {
                    AprilTagPoseRaw rawPose = tag.rawPose;
                    telemetry.addData("x", tag.ftcPose.x); //in
                    telemetry.addData("y", tag.ftcPose.y); //in
                    telemetry.addData("z", tag.ftcPose.z); //in
                    telemetry.addData("roll", tag.ftcPose.roll); //deg (likely 0)
                    telemetry.addData("pitch", tag.ftcPose.pitch); //deg (likely 0 unless on backdrop)
                    telemetry.addData("yaw", tag.ftcPose.yaw); //deg
                    telemetry.addData("ID", tag.id); //integer of tag ID, 0-20 (0 will cause issues, 10+ cannot display processes
                    telemetry.addData("ID Name", robot.ids.get(tag.id)); //String of tag
                }

                telemetry.update();
            }
        }
    }
}
