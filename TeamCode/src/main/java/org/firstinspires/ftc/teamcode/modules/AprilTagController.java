package org.firstinspires.ftc.teamcode.modules;

import android.util.Size;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseRaw;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;

public class AprilTagController {
    Robot2023 robot;
    Telemetry telemetry;
    public AprilTagProcessor tagProcessor;

    public VisionPortal visionPortal;

    public final ArrayList<String> ids = new ArrayList<String>();
    boolean doTelemetry;
    int lastIDDetected = 0;
    public void onOpmodeInit(Robot2023 robot, Telemetry telemetry, boolean doTelemetry){
        this.robot = robot;
        this.telemetry = telemetry;
        this.doTelemetry = doTelemetry;
        tagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true) //axes on center of AprilTag
                .setDrawCubeProjection(true) //cube projected from tag
                .setDrawTagID(true) //display tag number
                .setDrawTagOutline(true) //border of tag
                .build();
        visionPortal = new VisionPortal.Builder()
                .addProcessor(tagProcessor)
                .setCamera(robot.webcam)
                .setCameraResolution(new Size(640,480)) //set resolution
                .build();
        ids.add(null);
        ids.add("BlueL");
        ids.add("BlueC");
        ids.add("BlueR");
        ids.add("RedL");
        ids.add("RedC");
        ids.add("RedR");
        ids.add("RedW");
        ids.add("RedW");
        ids.add("BlueW");
        ids.add("BlueW");
    }
    public void doLoop(Gamepad gamepad1, Gamepad gamepad2){
        ArrayList<AprilTagDetection> detections = tagProcessor.getDetections();
        if (detections.size() > 0) { //if tag detected
            AprilTagDetection tag = detections.get(0);
            if(tag.id>0 && tag.id<=10) {
                AprilTagPoseRaw rawPose = tag.rawPose;
                if (doTelemetry) {
                    telemetry.addData("x", tag.ftcPose.x); //in
                    telemetry.addData("y", tag.ftcPose.y); //in
                    telemetry.addData("z", tag.ftcPose.z); //in
                    telemetry.addData("roll", tag.ftcPose.roll); //deg (likely 0)
                    telemetry.addData("pitch", tag.ftcPose.pitch); //deg (likely 0 unless on backdrop)
                    telemetry.addData("yaw", tag.ftcPose.yaw); //deg
                    telemetry.addData("ID", tag.id); //integer of tag ID, 0-20 (0 will cause issues, 10+ cannot display processes
                    telemetry.addData("ID Name", ids.get(tag.id)); //String of tag
                    telemetry.update();
                }
                lastIDDetected = tag.id;
            }
        }
    }
    public int getLastIDDetected(){
        return lastIDDetected;
    }
    public String getLastIDDetectedName(){
        return ids.get(lastIDDetected);
    }
}
