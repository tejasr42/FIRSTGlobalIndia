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

/*
@TeleOp(name = "Test101", group = "Practice Mode")
public class FirstOpMode extends LinearOpMode {

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
*/