package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class ForearmEncoderReader extends LinearOpMode {
    ElapsedTime runtime = new ElapsedTime();
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx forearmLeft = hardwareMap.get(DcMotorEx.class, "forearmLeft");
        forearmLeft.setDirection(DcMotorEx.Direction.REVERSE);
        DcMotorEx forearmRight = hardwareMap.get(DcMotorEx.class, "forearmRight");
        forearmRight.setDirection(DcMotorEx.Direction.REVERSE);
        runtime.reset();
        waitForStart();
        while (opModeIsActive()){
            telemetry.addData("L encoder", forearmLeft.getCurrentPosition());
            telemetry.addData("R encoder", forearmRight.getCurrentPosition());
            telemetry.addData("runtime", runtime.seconds());
            telemetry.update();
            idle();
        }
    }
}
