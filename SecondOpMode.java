package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by tejas on 24-06-2017.
 */


@TeleOp(name = "Test101", group = "Practice Mode")
public class SecondOpMode extends LinearOpMode {
/*
    private DcMotor collector, lift;
    private Servo sorter;
    private ColorSensor colorsort;
    private DistanceSensor prox;
    private int sensordelay;
    private int servowait;
    private boolean servoact;
    private boolean servocheck;
    private boolean orange,blue;
    private ElapsedTime period = new ElapsedTime();
    double left,right,sig;

    public void runOpMode(){
        robotInit();
        while(opModeIsActive()){
            //arcade();
            collector.setPower(-0.9);
            lift.setPower(0.9);
            //tankDrive();
            //drive();
            sortBalls();
            telemetry();
            //scale();
            if(!waitForTick(40)) return;
        }
        resetComponents();

    }
    private void robotInit(){
        collector=hardwareMap.dcMotor.get("collector");
        collector.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift=hardwareMap.dcMotor.get("lift");
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sorter=hardwareMap.servo.get("sorter");
        colorsort=hardwareMap.colorSensor.get("color");
        prox= (DistanceSensor)hardwareMap.get("color");
        sensordelay=0;
        servowait=0;
        servoact=false;
        servocheck=false;
        orange=false;
        blue=false;
        resetComponents();
        waitForStart();
    }



    long lastTime = System.currentTimeMillis();
    private void sortBalls(){
        sorting();

    }
    int lastPos = 0;
    private void telemetry(){
        telemetry.update();

    }
    private void resetComponents(){
        collector.setPower(0);
        lift.setPower(0);
        sorter.setPosition(0.6);
    }
    private void sorting(){
        if(prox.getDistance(DistanceUnit.CM)<6 && colorsort.blue()<colorsort.red()) {
            orange=true;
        }
        if(orange && prox.getDistance(DistanceUnit.CM)>6){
            orange=false;
            if(servocheck){
                sensordelay=21;
            }
            else{
                servoact=true;
                servowait=10;
                sensordelay=11;
            }

        }
        if(prox.getDistance(DistanceUnit.CM)<6 && colorsort.blue()>colorsort.red()) {
            blue=true;
        }
        if(blue && prox.getDistance(DistanceUnit.CM)>6){
            blue=false;
            if(servocheck){
                sensordelay=Math.min(sensordelay,12);
            }
        }
        if(!servoact) {
            if (sensordelay == 0) {
                sorter.setPosition(0.3);
                servocheck=false;

            } else {
                sensordelay--;
            }
        }else {
            if (servowait == 0) {
                sorter.setPosition(0.1);
                servocheck=true;
                servoact=false;
            } else {
                servowait--;
            }
        }
    }
    */
private DcMotor rf,rb,lf,lb;
private double left,right,sig;
    private double velRight = 0, velLeft = 0,accel,accer;
    ElapsedTime period = new ElapsedTime();
    public void runOpMode(){
    robotInit();
    while(opModeIsActive()){
        //arcade();
        //tankDrive();
        //drive();
        //sortBalls();

        telemetry.addData("aleft",accel);
        telemetry.addData("aright",accer);
        telemetry.addData("left",velLeft);
        telemetry.addData("right",velRight);
        telemetry.update();
        drive();
        //scale();

        if(!waitForTick(40)) return;
    }
    resetComponents();

}
    private void resetComponents(){
        rf.setPower(0);
        rb.setPower(0);
        lf.setPower(0);
        lb.setPower(0);

    }
    private void robotInit(){
        rf=hardwareMap.dcMotor.get("rf");
        rb=hardwareMap.dcMotor.get("rb");
        lf=hardwareMap.dcMotor.get("lf");
        lb=hardwareMap.dcMotor.get("lb");
        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lb.setDirection(DcMotorSimple.Direction.REVERSE);


//        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        resetComponents();
        waitForStart();
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
    private void arcade(){
        double y=-gamepad1.left_stick_y;
        left=Math.signum(y)*Math.pow((Math.max(Math.abs(y),0.2)-0.2)*1.25,5);
        right=left;
        if(left==0) {
            left=Math.pow(gamepad1.left_stick_x,3);
            right=-left;
        }
        else {
            left*=1+(0.2*gamepad1.left_stick_x);
            right/=1+(0.2*gamepad1.left_stick_x);
        }
        left*=1-Math.pow(gamepad1.right_trigger,2);
        right*=1-Math.pow(gamepad1.right_trigger,2);
        double maxlr=Math.max(Math.abs(left),Math.abs(right));
        if(maxlr>1) {
            left /= maxlr;
            right /= maxlr;
        }
        if(gamepad1.dpad_up) {left=0.1;right=0.1;}
    }

    private void accelDrive(){

    }


    private void drive(){
        rb.setPower(gamepad1.right_stick_y);
        rf.setPower(gamepad1.right_stick_y);
        lb.setPower(gamepad1.left_stick_y);
        lf.setPower(gamepad1.left_stick_y);
    }

}
