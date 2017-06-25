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

import java.util.Comparator;
import java.util.PriorityQueue;

@TeleOp(name = "Test5", group = "Practice Mode")
public class FirstOpMode extends LinearOpMode {
    public class Action{
        long time;
        int action;

        Action(long t, int a) {
            time = t;
            action = a;
        }

    }
    Comparator<Action> comp = new TimeComp();
    public class TimeComp implements Comparator<Action>{
        @Override
        public int compare(Action a, Action b){
            if(a==null || b==null) return 0;
            if(a.time > b.time) return 1;
            else if(a.time == b.time) return 0;
            else return -1;

        }

    }
    PriorityQueue<Action>[] queue = new PriorityQueue[3];

    private DcMotor leftfront, rightfront, leftback, rightback, collector, lift, climbleft, climbright;
    private Servo[] sorter = new Servo[3];
    private ColorSensor[] color = new ColorSensor[3];
    private DistanceSensor[] prox = new DistanceSensor[3];
    private boolean[] orange = new boolean[3],blue = new boolean[3];
    private double[][] endp = new double[][]{{0.1, 0.23, 0.4}, {0.2, 0.35, 0.5}, {0.4, 0.23, 0.1}};
    private long lastNotDetected[] = new long[3];
    private int balldelay=800;
    private double left,right,sig;
    private ElapsedTime period = new ElapsedTime();

    public void runOpMode(){
        robotInit();
        while(opModeIsActive()){
            tankDrive();
            //drive();
            sortBalls();
            telemetry();
            scale();
            if(!waitForTick(10)) return;
        }
        resetComponents();

    }
    private void robotInit(){
        leftfront = hardwareMap.dcMotor.get("left_front");
        rightfront = hardwareMap.dcMotor.get("right_front");
        leftback = hardwareMap.dcMotor.get("left_back");
        rightback = hardwareMap.dcMotor.get("right_back");
        collector=hardwareMap.dcMotor.get("collector");
        collector.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift=hardwareMap.dcMotor.get("lift");
        lift.setDirection(DcMotorSimple.Direction.REVERSE);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        for(int i = 0; i < 3; i++){
            sorter[i] = hardwareMap.servo.get("sorter"+((Integer)i).toString());
            color[i] = hardwareMap.colorSensor.get("color"+((Integer)i).toString());
            prox[i] = (DistanceSensor)hardwareMap.get("color"+((Integer)i).toString());
            queue[i]= new PriorityQueue<Action>(10,comp);
        }
        leftfront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftback.setDirection(DcMotorSimple.Direction.REVERSE);
        climbleft = hardwareMap.dcMotor.get("climb_left");
        climbright = hardwareMap.dcMotor.get("climb_right");
        climbleft.setDirection(DcMotorSimple.Direction.REVERSE);
        resetComponents();
        waitForStart();
    }

    private void tankDrive(){
        double leftJoy = -gamepad1.left_stick_y;
        double rightJoy = -gamepad1.right_stick_y;
        boolean rightTrigger = gamepad1.right_trigger > 0;
        boolean leftTrigger = gamepad1.left_trigger > 0;
        double mult;
        if(rightTrigger && !leftTrigger){
            mult = 0.8;
        }else if(leftTrigger && !rightTrigger){
            mult = 0.3;
        }else if(leftTrigger && rightTrigger){
            mult = 0;
        }else{
            mult = 0.6;
        }
        leftback.setPower(mult*leftJoy);
        leftfront.setPower(mult*leftJoy);
        rightback.setPower(mult*rightJoy);
        rightfront.setPower(mult*rightJoy);
    }

    private void arcade(){
        left=-Math.pow(gamepad1.right_trigger,3)*Math.max(1,27*Math.pow(gamepad1.left_stick_y,3));
        right=Math.pow(-gamepad1.right_trigger,3);
        sig=Math.signum(gamepad1.left_stick_y);
        if(sig == 0){
            sig=1;
        }
        left+=sig*gamepad1.left_stick_x;
        right-=sig*gamepad1.left_stick_x;
        if(Math.max(Math.abs(left),Math.abs(right))>1) {
            left /= Math.max(Math.abs(left),Math.abs(right));
            right /= Math.max(Math.abs(left),Math.abs(right));
        }
    }

    private void scale(){
        boolean up = gamepad1.dpad_up;
        boolean down = gamepad1.dpad_down;

        if(down){

            climbleft.setPower(1);
            climbright.setPower(1);
        }else if(up){
            climbleft.setPower(-0.5);
            climbright.setPower(-0.5);
        }else{
            climbleft.setPower(0);
            climbright.setPower(0);
        }

    }
    private void drive(){
        leftfront.setPower(left);
        rightfront.setPower(right);
        leftback.setPower(left);
        rightback.setPower(right);
    }

    long lastTime = System.currentTimeMillis();
    private void sortBalls(){
        sorting(0);sorting(1);sorting(2);
    }

    private void telemetry(){
        telemetry.update();

    }
    private void resetComponents(){
        leftfront.setPower(0);
        rightfront.setPower(0);
        leftback.setPower(0);
        rightback.setPower(0);
        collector.setPower(0);
        lift.setPower(0);
        for(int i = 0; i < 3; i++) sorter[i].setPosition(endp[i][1]);
    }
    private void sorting(int col){
        if (!orange[col] && prox[col].getDistance(DistanceUnit.CM) < 6 && color[col].blue() < color[col].red()) {
            orange[col] = true;
            lastNotDetected[col] = System.currentTimeMillis();
            queue[col].add(new Action(800 + System.currentTimeMillis(), 1));
        } else if (!blue[col] && prox[col].getDistance(DistanceUnit.CM) < 6 && color[col].blue() > color[col].red()) {
            blue[col] = true;
            lastNotDetected[col] = System.currentTimeMillis();
            queue[col].add(new Action(800 + System.currentTimeMillis(), -1));
        }
        if (orange[col] && (prox[col].getDistance(DistanceUnit.CM) > 6 || System.currentTimeMillis() - lastNotDetected[col] > balldelay)) {
            orange[col] = false;

        }
        else if (blue[col] && (prox[col].getDistance(DistanceUnit.CM) > 6 || System.currentTimeMillis() - lastNotDetected[col] > balldelay)) {
            blue[col] = false;

        }
        if(!queue[col].isEmpty() && System.currentTimeMillis() > queue[col].peek().time) {
            int state = queue[col].peek().action;
            queue[col].poll();
            if (state == 1) {
                sorter[col].setPosition(endp[col][0]);
                if (queue[col].isEmpty()) queue[col].add(new Action(440 + System.currentTimeMillis(), 0));
            }
            else if (state == -1) {
                sorter[col].setPosition(endp[col][2]);
                if (queue[col].isEmpty()) queue[col].add(new Action(440 + System.currentTimeMillis(), 0));
            }
            else if(state==0) sorter[col].setPosition(endp[col][1]);
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
