//01-07-2017 18:05 Tejas
package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.internal.SystemProperties;

import java.util.Comparator;
import java.util.PriorityQueue;

@TeleOp(name = "AccSort", group = "Practice Mode")
public class SecondOpMode extends LinearOpMode {
    public class Action {
        long time;
        int action;

        Action(long t, int a) {
            time = t;
            action = a;
        }

    }

    Comparator<Action> comp = new TimeComp();

    public class TimeComp implements Comparator<Action> {
        @Override
        public int compare(Action a, Action b) {
            if (a == null || b == null) return 0;
            if (a.time > b.time) return 1;
            else if (a.time == b.time) return 0;
            else return -1;
        }
    }

    PriorityQueue<Action>[] queue = new PriorityQueue[3];

    private DcMotor leftfront, rightfront, leftback, rightback, collector, lift, climbleft, climbright;
    private Servo[] sorter = new Servo[3];
    private Servo deliver;
    private ColorSensor[] color = new ColorSensor[3];
    private DistanceSensor[] prox = new DistanceSensor[3];
    private boolean[] orange = new boolean[3], blue = new boolean[3];
    //{{0.1, 0.23, 0.4}, {0.2, 0.35, 0.5}, {0.4, 0.23, 0.1}}
    private double[][] endp = new double[][]{{0.15, 0.15, 0.37, 0.03}, {0.18, 0.18, 0.4,0.07}, {0.32, 0.32, 0.1,0.45}};
    private long lastNotDetected[] = new long[3];
    private int balldelay = 800, des = 0, cur = 0, enc = 6,lastpos=0,vibcount=0;
    private double velRight = 0, velLeft = 0, accer, accel, del = 0;
    private ElapsedTime period = new ElapsedTime();
    private boolean vibrate=false,scalelock = false, last = false, lasta = false, lastb = false, orb=false,orbt=true,sort=true;

    public void runOpMode() {
        robotInit();
        while (opModeIsActive()) {
            tankDrive();
            collect();
            if(sort){
                sortBalls();
            }
            //telemetry();
            scale3();
            deliver();
            if (!waitForTick(20)) return;
        }
        resetComponents();

    }

    private void robotInit() {
        leftfront = hardwareMap.dcMotor.get("left_front");
        rightfront = hardwareMap.dcMotor.get("right_front");
        leftback = hardwareMap.dcMotor.get("left_back");
        rightback = hardwareMap.dcMotor.get("right_back");
        collector = hardwareMap.dcMotor.get("collector");
        collector.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift = hardwareMap.dcMotor.get("lift");
        lift.setDirection(DcMotorSimple.Direction.REVERSE);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        for (int i = 0; i < 3; i++) {
            sorter[i] = hardwareMap.servo.get("sorter" + ((Integer) i).toString());
            color[i] = hardwareMap.colorSensor.get("color" + ((Integer) i).toString());
            prox[i] = (DistanceSensor) hardwareMap.get("color" + ((Integer) i).toString());
            queue[i] = new PriorityQueue<Action>(10, comp);
        }
        deliver = hardwareMap.servo.get("deliver");
        leftfront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftback.setDirection(DcMotorSimple.Direction.REVERSE);
        climbleft = hardwareMap.dcMotor.get("climb_left");
        climbright = hardwareMap.dcMotor.get("climb_right");
        climbright.setDirection(DcMotorSimple.Direction.REVERSE);
        climbright.setDirection(DcMotorSimple.Direction.REVERSE);
        climbleft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        climbright.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        climbleft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        climbright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        climbleft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        climbright.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        deliver.setPosition(0.53);
        resetComponents();
        waitForStart();
    }


    private void move(double left, double right){
        leftback.setPower(left);
        leftfront.setPower(left);
        rightback.setPower(right);
        rightfront.setPower(right);
    }

    private void tankDrive(){
        boolean rightTrig = gamepad1.right_trigger > 0;
        boolean leftTrig = gamepad1.left_trigger > 0;
        boolean rightBum = gamepad1.right_bumper;
        boolean leftBum = gamepad1.left_bumper;
        double rightst=gamepad1.right_stick_y;
        double leftst=gamepad1.left_stick_y;
        double rightMult, leftMult;
        if(gamepad2.dpad_left){
            rightMult=0.3;
            leftMult=0.3;
        }
        else if(rightTrig && leftTrig) {
            rightMult = 0.9;
            leftMult =0.9;
        }
        else if(leftTrig) {
            rightMult = 0.2;
            leftMult =0.9;
        }
        else if(rightTrig){
            rightMult = 0.9;
            leftMult =0.2;
        }
        else {
            rightMult = 0.6;
            leftMult=0.6;
        }
        if(rightBum) vibrate=true;
        else if(leftBum) {
            vibcount=0;
            vibrate=false;
        }

        if(rightBum && leftBum) move(-leftst*0.3, -rightst*0.3);
        else if(vibrate) {
            vibe();
        }
        else move(-leftst*leftMult, -rightst*rightMult);

    }

    private void vibe(){
        if(vibcount>=0) {
            vibcount++;
            move(0.6,0.6);
        }
        else if(vibcount<=0) {
            vibcount--;
            move(-0.7,-0.7);
        }
        if(vibcount==1) vibcount=-1;
        if(vibcount==-2) vibcount=0;
    }
    private void tankDrive2() {
        double leftJoy = -gamepad1.left_stick_y;
        double rightJoy = -gamepad1.right_stick_y;
        boolean rightTrigger = gamepad1.right_trigger > 0;
        boolean leftTrigger = gamepad1.left_trigger > 0;
        double mult;
        if (rightTrigger && !leftTrigger) {
            mult = 0.95;
        } else if (leftTrigger && !rightTrigger) {
            mult = 0.4;
        } else if (leftTrigger && rightTrigger) {
            mult = 0;
        } else {
            mult = 0.7;
        }
        if(gamepad1.right_bumper){
            leftback.setPower(mult);
            leftfront.setPower(mult);
            rightback.setPower(mult);
            rightfront.setPower(mult);
        }else if(gamepad1.left_bumper){
            leftback.setPower(-mult);
            leftfront.setPower(-mult);
            rightback.setPower(-mult);
            rightfront.setPower(-mult);
        }else{
            leftback.setPower(mult * leftJoy);
            leftfront.setPower(mult * leftJoy);
            rightback.setPower(mult * rightJoy);
            rightfront.setPower(mult * rightJoy);
        }
    }
    private void collect(){
        if(gamepad2.x){
            if(!orb) {
                orbt=!orbt;
            }
            orb=true;
        }
        else{
            orb=false;
        }
        if(orbt){
            lift.setPower(0.7);
            collector.setPower(0.8);
        }
        else{
            lift.setPower(0);
            collector.setPower(0);
        }
        if(gamepad2.y){
            collector.setPower(-0.9);
            lift.setPower(-0.7);
        }

        if(gamepad2.dpad_left) {
            sort=true;
            for (int j = 0; j < 3; j++) sorter[j].setPosition(endp[j][0]);
        }else if(gamepad2.dpad_down){
            sort = false;
            for (int j = 0; j < 3; j++) sorter[j].setPosition(endp[j][2]);
        }else if(gamepad2.dpad_up){
            sort = false;
            for (int j = 0; j < 3; j++) sorter[j].setPosition(endp[j][3]);
        }
        //telemetry.addData("sort",sort);

        //telemetry.addData("lift",lift.getCurrentPosition()-lastpos);
        //lastpos=lift.getCurrentPosition();
    }
    private void accelDrive() {
        accer = -gamepad1.right_stick_y;
        if (accer == 0) accer = -0.9 * velRight;
        boolean brakeR = gamepad1.right_bumper;
        if (Math.signum(velRight) == 0) {
            velRight += accer * 0.1;
        } else if (brakeR) {
            velRight = 0;
        } else if (Math.signum(velRight) == Math.signum(accer)) {
            velRight += accer * Math.max(0.3, Math.abs(velRight));
        } else {
            velRight += accer * 0.5;
        }

        accel = -gamepad1.left_stick_y;
        if (accel == 0) accel = -0.9 * velLeft;
        boolean brakeL = gamepad1.left_bumper;
        if (Math.signum(velLeft) == 0) {
            velLeft += accel * 0.1;
        } else if (brakeL) {
            //velLeft += Math.signum(velLeft)*-0.1;
            velLeft = 0;
        } else if (Math.signum(velLeft) == Math.signum(accel)) {
            velLeft += accel * Math.max(0.3, Math.abs(velLeft));
        } else {
            velLeft += (accel) * 0.5;
        }

        if(gamepad1.right_trigger > 0){
            velLeft = Math.signum(velLeft)*Math.min(1, Math.abs(velLeft));
            velRight = Math.signum(velRight)*Math.min(1, Math.abs(velRight));

        }else if(gamepad1.left_trigger > 0){
            velLeft = Math.signum(velLeft)*Math.min(0.4, Math.abs(velLeft));
            velRight = Math.signum(velRight)*Math.min(0.4, Math.abs(velRight));

        }else{
            velLeft = Math.signum(velLeft)*Math.min(0.7, Math.abs(velLeft));
            velRight = Math.signum(velRight)*Math.min(0.7, Math.abs(velRight));
        }
        leftfront.setPower(velLeft);
        leftback.setPower(velLeft);
        rightfront.setPower(velRight);
        rightback.setPower(velRight);
    }

    /*private void arcade(){
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
    }*/

    private void scale() {
        telemetry.addData("scale", scalelock);
        boolean up = gamepad2.dpad_up;
        boolean down = gamepad2.dpad_down;
        int lenc=-climbleft.getCurrentPosition();
        int renc=-climbright.getCurrentPosition();
        telemetry.addData("leftc",lenc);

        telemetry.addData("rightc",renc);
        if (down) {
            if (lenc >=0) {
                climbleft.setPower(1);
            } else {
                climbleft.setPower(0);
            }

            if (renc >= 0) {
                climbright.setPower(1);
            } else {
                climbright.setPower(0);
            }

        } else if (up) {
            if (lenc < 630) {
                climbleft.setPower(-0.8);
            } else {
                climbleft.setPower(0);
            }
            if (renc <= 630) {
                climbright.setPower(-0.8);
            } else {
                climbleft.setPower(0);
            }

        } else {
            climbleft.setPower(0);
            climbright.setPower(0);
        }

        if (last == false && gamepad2.dpad_left) {
            if (scalelock) {
                scalelock = false;
                climbleft.setPower(0);
                climbright.setPower(0);
            }
            if (!scalelock) {
                scalelock = true;
                climbleft.setPower(0.2);
                climbright.setPower(0.2);
            }
        }
        last = gamepad2.dpad_left;
    }

    /*private void drive(){
        leftfront.setPower(left);
        rightfront.setPower(right);
        leftback.setPower(left);
        rightback.setPower(right);
    }*/
    boolean lastr=false,lastl=false;
    int errorl=0,errorr=0;

    private void scale3(){
        int lenc=climbleft.getCurrentPosition();
        int renc=climbright.getCurrentPosition();
        double leftstick = gamepad2.left_stick_y;
        if(leftstick != 0){
            climbright.setPower(-leftstick);
        }else if(gamepad2.right_bumper && renc < 620){
            climbright.setPower(0.5);
        }else if(gamepad2.right_trigger > 0 && renc > 0){
            climbright.setPower(-0.9);
        }else{
            climbright.setPower(0);
        }
        if(leftstick != 0){
            climbleft.setPower(-leftstick);
        }else if(gamepad2.left_bumper && lenc <620){
            climbleft.setPower(0.5);
        }else if(gamepad2.left_trigger > 0 && lenc > 0){
            climbleft.setPower(-0.9);
        }else{
            climbleft.setPower(0);
        }
    }

    private void scale2(){
        int lenc=climbleft.getCurrentPosition();
        int renc=climbright.getCurrentPosition();
        telemetry.addData("r",errorr);
        telemetry.addData("l",errorl);

        if(gamepad2.right_bumper){
            if(renc+errorr<620){
                climbright.setPower(0.5);
            }
            else{
                climbright.setPower(0);
            }
            if(lenc+errorl<620){
                climbleft.setPower(0.5);
            }
            else{
                climbleft.setPower(0);
            }
        }else if(gamepad2.left_bumper){
            if(renc+errorr>5){
                climbright.setPower(-0.8);
            }
            else{
                climbright.setPower(0);
            }
            if(lenc+errorl>5){
                climbleft.setPower(-0.8);
            }
            else{
                climbleft.setPower(0);
            }
        }else if (gamepad2.right_trigger > 0.5) {
            lastr = true;
            lastl = false;
            climbleft.setPower(gamepad2.right_trigger);
            climbright.setPower(gamepad2.right_trigger);

        } else if (gamepad2.left_trigger > 0.5) {
            lastl = true;
            lastr = false;
            climbleft.setPower(-1);
            climbright.setPower(-1);
        } else {
            climbleft.setPower(0);
            climbright.setPower(0);
            if (lastl) {
                climbleft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                climbleft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                climbright.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                climbright.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                errorl = 0;
                errorr = 0;
            }
            if (lastr) {
                //telemetry.addData("lastrt","yes");
                errorl = 620-climbleft.getCurrentPosition();
                errorr = 620-climbright.getCurrentPosition();
            }
            lastr = false;
            lastl = false;
        }
    }


    long tick = 0;
    private void deliver() {
        if (gamepad2.a) {
            deliver.setPosition(0.38);
        } else if (gamepad2.b) {
            deliver.setPosition(0.72);
        } else {
            deliver.setPosition(0.53);
        }
//        deliver.setPosition(0.5 + Math.signum(des - cur) * 0.2);
//        cur += Math.e4signum(des - cur) * 1;
//        lastb = gamepad2.b;
//        if(des-cur==0){
//            deliver.setPosition(gamepad2.right_stick_x*0.5 + 0.5);
//            //if(gamepad2.right_stick_y>0.5) deliver.setPosition(0.3);
//            //else if(gamepad2.right_stick_y<-0.5) deliver.setPosition(0.7);
//        }
        /*
        if (gamepad2.a) {
            des = enc;
            //cur += ((tick++)%30 == 0?1:0);
        } else if (gamepad2.b) {
            des = -enc;
            //cur += ((tick++)%30 == 0?1:0);
        } else {
            des = 0;
        }
        deliver.setPosition(0.5 + Math.signum(des - cur) * 0.2);
        cur += Math.signum(des - cur) * 1;
        lastb = gamepad2.b;
        if(des-cur==0){
            deliver.setPosition(gamepad2.right_stick_x*0.5 + 0.5);
            //if(gamepad2.right_stick_y>0.5) deliver.setPosition(0.3);
            //else if(gamepad2.right_stick_y<-0.5) deliver.setPosition(0.7);
        }
*/
    }

    private void sortBalls(){
        sorting(0);sorting(1);sorting(2);
    }
    private void telemetry(){
        //telemetry.update();

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
        boolean distance = prox[col].getDistance(DistanceUnit.CM) < 6;
        boolean isRed = color[col].blue() < color[col].red();
        if (!orange[col] && distance && isRed) {
            orange[col] = true;

        } else if (!blue[col] && distance && !isRed){
            blue[col] = true;
        }
        if (orange[col] && !distance) {
            orange[col] = false;
            lastNotDetected[col] = System.currentTimeMillis();
            queue[col].add(new Action(270 + System.currentTimeMillis(), -1));
        }
        else if (blue[col] && !distance) {
            blue[col] = false;
            lastNotDetected[col] = System.currentTimeMillis();
            queue[col].add(new Action(270 + System.currentTimeMillis(), 1));
        }
        if(!queue[col].isEmpty() && System.currentTimeMillis() > queue[col].peek().time) {
            int state = queue[col].peek().action;
            queue[col].poll();
            if (state == 1) {
                sorter[col].setPosition(endp[col][0]);
                //if (queue[col].isEmpty()) queue[col].add(new Action(440 + System.currentTimeMillis(), 0));
            }
            else if (state == -1) {
                sorter[col].setPosition(endp[col][2]);
                //if (queue[col].isEmpty()) queue[col].add(new Action(440 + System.currentTimeMillis(), 0));
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