package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class EncoderReader extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx theMotor = hardwareMap.get(DcMotorEx.class, "forearmRight");
        theMotor.setDirection(DcMotorEx.Direction.REVERSE);
        waitForStart();
        while (opModeIsActive()){
            telemetry.addData("encoder", theMotor.getCurrentPosition());
            telemetry.update();
            idle();
        }
    }
}
