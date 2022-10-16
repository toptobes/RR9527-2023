package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcodekt.components.easytoggle.EasyToggle;

/**
 * This is a test teleop class for testing. Do not use in competition. - Seb on may 7th, 2021.
 * Also Tiernan :)
 *
 * Needs to be converted to an actually good teleop using the RR+ architecture and commandbased API
 */
@TeleOp(name="TestOP")
public class TestOP extends OpMode{
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx leftFront, leftBack, rightFront, rightBack, liftA, liftB, arm;
    private Servo wrist, claw;
    private CRServo intake;

    // stupid seb variable
    private int armTarget = 0;
    private double testNum = .36;

    // Toggle objects for the A and B button respectively
    private EasyToggle toggleA;
    private EasyToggle toggleB;


    @Override
    public void init(){
        leftFront = (DcMotorEx) hardwareMap.dcMotor.get("FL");
        leftBack = (DcMotorEx) hardwareMap.dcMotor.get("BL");
        rightFront = (DcMotorEx) hardwareMap.dcMotor.get("FR");
        rightBack = (DcMotorEx) hardwareMap.dcMotor.get("BR");

        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        liftA = (DcMotorEx) hardwareMap.dcMotor.get("L1");
        liftB = (DcMotorEx) hardwareMap.dcMotor.get("L2");
        arm = (DcMotorEx) hardwareMap.dcMotor.get("AR");

        liftA.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftB.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftA.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        liftB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        liftB.setDirection(DcMotor.Direction.REVERSE);

        intake = hardwareMap.crservo.get("IN");
        claw = hardwareMap.servo.get("CL");
        wrist = hardwareMap.servo.get("WR");

        toggleA = new EasyToggle(false);
        toggleB = new EasyToggle(false);
    }
    public void start(){
        arm.setTargetPosition(armTarget);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm.setPower(.7);
        wrist.setPosition(.83);
    }
    @Override
    public void loop() {
        drive();
        wrist();
        //servoTest();
        succ();
        armMove();
        lift();

        telemetry.addData("servoPos", claw.getPosition());
        telemetry.addData("armPos", arm.getCurrentPosition());
        telemetry.update();

    }

    public void drive() {
        if (Math.abs(gamepad1.left_stick_y) > 0.1 || Math.abs(gamepad1.left_stick_x) > 0.1 || Math.abs(gamepad1.right_stick_x) > 0.1) {
            double FLP = gamepad1.left_stick_y - gamepad1.left_stick_x - gamepad1.right_stick_x;
            double FRP = -gamepad1.left_stick_y - gamepad1.left_stick_x - gamepad1.right_stick_x;
            double BLP = gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x;
            double BRP = -gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x;
            double max = Math.max(Math.max(Math.abs(FLP), Math.abs(FRP)), Math.max(Math.abs(BLP), Math.abs(BRP)));
            if (max > 1) {
                FLP /= max;
                FRP /= max;
                BLP /= max;
                BRP /= max;
            }
            if (gamepad1.left_trigger > .5) {
                leftFront.setPower(FLP * 0.35);
                rightFront.setPower(FRP * 0.35);
                leftBack.setPower(BLP * 0.35);
                rightBack.setPower(BRP * 0.35);
                telemetry.addData("FrontLeftPow:", FLP * 0.35);
                telemetry.addData("FrontRightPow:", FRP * 0.35);
                telemetry.addData("BackLeftPow:", BLP * 0.35);
                telemetry.addData("BackRightPow:", BRP * 0.35);
            } else {
                leftFront.setPower(FLP);
                rightFront.setPower(FRP);
                leftBack.setPower(BLP);
                rightBack.setPower(BRP);
            }
        } else {
            leftFront.setPower(0);
            rightFront.setPower(0);
            leftBack.setPower(0);
            rightBack.setPower(0);
        }
    }

    private void succ(){
        if (gamepad1.left_trigger > .5) {
            claw.setPosition(.65);
        } else {
            claw.setPosition(.52);
        }

        if (gamepad1.right_trigger > .5){
            intake.setPower(1);
        } else {
            intake.setPower(0);
        }

    }

    private void armMove(){
        if(gamepad1.x){
            armTarget = 480;
            arm.setTargetPosition(armTarget);
            arm.setPower(.7);
        } else if (gamepad1.y){
            armTarget = 0;
            arm.setTargetPosition(armTarget);
            arm.setPower(.7);
        } else if (gamepad1.b){
            armTarget = -480;
            arm.setTargetPosition(armTarget);
            arm.setPower(.7);
        }
    }

    private void lift(){
        if(gamepad1.right_bumper) {
            liftA.setPower(.8);
            liftB.setPower(.8);
        } else {
            liftA.setPower(0);
            liftB.setPower(0);
        }
    }

    private void wrist(){
        if(gamepad1.dpad_up){
            wrist.setPosition(.21);
        }
        if(gamepad1.dpad_down){
            wrist.setPosition(.84);
        }
    }

    private void servoTest(){
        toggleA.setState(gamepad2.a);
        toggleB.setState(gamepad2.b);
        if(toggleB.nowTrue()){
            testNum += .01;
        } else if (toggleA.nowTrue()){
            testNum -= .01;
        }
        claw.setPosition(testNum);
    }

}