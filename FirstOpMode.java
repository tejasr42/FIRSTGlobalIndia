package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Test5", group = "Practice Mode")
public class FirstOpMode extends LinearOpMode {

    private DcMotor leftfront, rightfront, leftback, rightback, collector, lift;
    private Servo sorter;
    private ColorSensor colorsort;
    private ElapsedTime period = new ElapsedTime();



    public void runOpMode(){
        leftfront = hardwareMap.dcMotor.get("left_front");
        rightfront = hardwareMap.dcMotor.get("right_front");
        leftback = hardwareMap.dcMotor.get("left_back");
        rightback = hardwareMap.dcMotor.get("right_back");
        

        leftfront.setPower(0);
        rightfront.setPower(0);
        leftback.setPower(0);
        rightback.setPower(0);

        waitForStart();
        while(opModeIsActive()){
            left_motor.setPower(-gamepad1.right_stick_y);
            right_motor.setPower(-gamepad1.left_stick_y);
            boolean is = waitForTick(40);
            if(!is) return;
        }
        left_motor.setPower(0);
        right_motor.setPower(0);

    }

    private boolean waitForTick(long periodMs){
        try{
            long remaining = periodMs - (long)period.milliseconds();
            if(remaining > 0){
                Thread.sleep(remaining);
            }
            period.reset();
        }catch(java.lang.InterruptedException exc){
            return false;
        }
        return true;
    }
}