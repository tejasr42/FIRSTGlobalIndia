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
        collector=hardwareMap.dcMotor.get("collector");
        lift=hardwareMap.dcMotor.get("lift");
        sorter=hardwareMap.servo.get("sorter");
        colorsort=hardwareMap.colorSensor.get("color");

        resetComponents();

        waitForStart();
        while(opModeIsActive()){
            leftfront.setPower(-gamepad1.left_stick_y);
            rightfront.setPower(-gamepad1.rightt_stick_y);
            leftback.setPower(-gamepad1.left_stick_y);
            rightback.setPower(-gamepad1.right_stick_y);
            collector.setPower(gamepad1.left_trigger);
            lift.setPower(gamepad1.right_trigger);

            sorter.setPosition(gamepad2.left_stick_y);

            boolean is = waitForTick(40);
            if(!is) return;
        }
        resetComponents();

    }
    private void resetComponents(){
        leftfront.setPower(0);
        rightfront.setPower(0);
        leftback.setPower(0);
        rightback.setPower(0);
        collector.setPower(0);
        lift.setPower(0);
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