//04-06-2017 23:05 Tejas
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Test5", group = "Practice Mode")
public class FirstOpMode extends LinearOpMode {

    private DcMotor leftfront, rightfront, leftback, rightback, collector, lift;
    private Servo sorter;
    private ColorSensor colorsort;
    private DistanceSensor prox;
    private ElapsedTime period = new ElapsedTime();



    public void runOpMode(){
        double left,right,sig;
        leftfront = hardwareMap.dcMotor.get("left_front");
        rightfront = hardwareMap.dcMotor.get("right_front");
        leftback = hardwareMap.dcMotor.get("left_back");
        rightback = hardwareMap.dcMotor.get("right_back");
        collector=hardwareMap.dcMotor.get("collector");
        lift=hardwareMap.dcMotor.get("lift");
        sorter=hardwareMap.servo.get("sorter");
        colorsort=hardwareMap.colorSensor.get("color");
        prox = (DistanceSensor)hardwareMap.get("color");
        leftfront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftback.setDirection(DcMotorSimple.Direction.REVERSE);
        resetComponents();

        waitForStart();
        while(opModeIsActive()){
            left=-gamepad1.left_stick_y;
            right=-gamepad1.left_stick_y;
            sig=Math.signum(left);
            if(sig == 0){
                sig=1;
            }
            left+=sig*gamepad1.left_stick_x;
            right-=sig*gamepad1.left_stick_x;
            if(Math.max(Math.abs(left),Math.abs(right))>1) {
                left /= Math.max(Math.abs(left),Math.abs(right));
                right /= Math.max(Math.abs(left),Math.abs(right));
            }
//            if(gamepad1.right_stick_x>0.2){
//                left=gamepad1.right_stick_x;
//                right=-gamepad1.right_stick_x;
//            }
            leftfront.setPower(left);
            rightfront.setPower(right);
            leftback.setPower(left);
            rightback.setPower(right);
            collector.setPower(0.8);
            lift.setPower(0.65);
            if(prox.getDistance(DistanceUnit.CM)<6 && colorsort.blue()<colorsort.red()){
                waitForTick(150);
                sorter.setPosition(0.867);
                waitForTick(400);
                sorter.setPosition(0.6);
            }
            //sorter.setPosition(0.5+0.36*gamepad2.left_stick_y);
            /*telemetry.addData("prox",prox.getDistance(DistanceUnit.CM));
            telemetry.addData("color",colorsort.blue());
            telemetry.update();*/
            boolean is = waitForTick(40);
            if(!is) return;
        }
        resetComponents();

    }

    private void sortBalls(){

    }

    private void resetComponents(){
        leftfront.setPower(0);
        rightfront.setPower(0);
        leftback.setPower(0);
        rightback.setPower(0);
        collector.setPower(0);
        lift.setPower(0);
        sorter.setPosition(0.6);
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