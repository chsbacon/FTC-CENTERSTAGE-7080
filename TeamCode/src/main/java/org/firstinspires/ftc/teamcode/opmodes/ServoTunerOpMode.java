package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.modules.Robot2023;

@TeleOp
public class ServoTunerOpMode extends LinearOpMode {
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0,0,0));
        drive.imu.resetYaw();
        Robot2023 robot = new Robot2023(this, drive, true, false, false, false, false, true);
        waitForStart();
        Servo[] servos = {robot.leftClawServo, robot.rightClawServo, robot.clawWristServo, robot.droneServo};
        String[] names = {"leftClawServo", "rightClawServo", "clawWristServo", "droneServo"};
        for (Servo servo: servos) {
            servo.setPosition(0.5);
        }
        int selectedServoIndex = 0;
        while (opModeIsActive()){
            // dpad left/right to select servo
            if (gamepad1.dpad_left){
                selectedServoIndex--;
                if (selectedServoIndex < 0){
                    selectedServoIndex = servos.length - 1;
                }
                sleep(300);
            }
            if (gamepad1.dpad_right){
                selectedServoIndex++;
                if (selectedServoIndex >= servos.length){
                    selectedServoIndex = 0;
                }
                sleep(300);
            }
            // x/b to decrease/increase position
            if (gamepad1.x){
                servos[selectedServoIndex].setPosition(servos[selectedServoIndex].getPosition() - 0.01);
                sleep(200);
            }
            if (gamepad1.b){
                servos[selectedServoIndex].setPosition(servos[selectedServoIndex].getPosition() + 0.01);
                sleep(200);
            }

            //lb/rb to decrease/increase position quickly
            if (gamepad1.left_bumper){
                servos[selectedServoIndex].setPosition(servos[selectedServoIndex].getPosition() - 0.1);
                sleep(200);
            }
            if (gamepad1.right_bumper){
                servos[selectedServoIndex].setPosition(servos[selectedServoIndex].getPosition() + 0.1);
                sleep(200);
            }

            // start to set servo to 0.5
            if (gamepad1.start){
                servos[selectedServoIndex].setPosition(0.5);
                sleep(200);
            }

            telemetry.addData("Selected Servo", names[selectedServoIndex]);
            telemetry.addData("Position", servos[selectedServoIndex].getPosition());
            telemetry.update();
            idle();
        }
    }
}
