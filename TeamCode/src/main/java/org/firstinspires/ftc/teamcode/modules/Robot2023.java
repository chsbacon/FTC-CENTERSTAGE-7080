package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Robot2023 {
    public HardwareMap hardwareMap;
    Servo clawServo;
    public Robot2023(HardwareMap hardwareMap){
        this.hardwareMap = hardwareMap;
        clawServo = hardwareMap.get(Servo.class, "clawServo");
    }
}
