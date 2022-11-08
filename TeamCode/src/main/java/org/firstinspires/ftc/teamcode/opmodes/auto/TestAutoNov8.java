package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.util.RobotConstants;
import org.firstinspires.ftc.teamcodekt.components.scheduler.Scheduler;

@Autonomous
public class TestAutoNov8 extends RougeBaseAuto {
    private Runnable armPosFunction;
    private Runnable wristPosFunction;

    private int signalZone;

    // Variable added to signal when to start the parking sequence
    private boolean startParking = false;

    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();

        armPosFunction = arm::setToRestingPos; // TODO: Clean up this notation
        wristPosFunction = wrist::setToRestingPos;

        Scheduler.beforeEach(() -> {
            armPosFunction.run();
            wristPosFunction.run();
        });

        schedulePaths();

        // Added this during init
        do {
            signalZone = getApriltagNumber();
            telemetry.addData("Signal zone detected", signalZone);
        }
        while(!opModeIsActive());

        Scheduler.start(this, () -> {
            arm.update(telemetry);
            lift.update(telemetry);
            wrist.update();
            drive.update();
            telemetry.update();
        });
    }

    public void schedulePaths() {
        // TODO: Fix RoadrunnerPlus in order to accommodate asynchronous trajectories

        Pose2d startPose = new Pose2d(in(91), in(-159), rad(90));

        drive.setPoseEstimate(startPose);

        TrajectorySequence trajSeq = drive.trajectorySequenceBuilder(startPose)
            .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                // Start getting the lift ready while turning
                lift.setHeight(RobotConstants.Lift.HIGH);
                wristPosFunction = wrist::setToForwardsPos;

                armPosFunction = arm::setToForwardsPos;
            })

            .splineTo(new Vector2d(in(91), in(-50)), rad(90))
            .splineTo(new Vector2d(in(AutoData.DEPOSIT_X), in(AutoData.DEPOSIT_Y)), rad(AutoData.DEPOSIT_ANGLE))
            .UNSTABLE_addTemporalMarkerOffset(AutoData.LOWER_OFFSET, () -> lift.setHeight(RobotConstants.Lift.MID))
            .UNSTABLE_addTemporalMarkerOffset(AutoData.DEPOSIT_OFFSET, () -> {
                // Deposit the cone while turning
                claw.openForDeposit();
            })

            .waitSeconds(AutoData.DEPOSIT_DELAY)

            .UNSTABLE_addTemporalMarkerOffset(AutoData.RETRACT_OFFSET, () -> {
                // Prepare the robot for intaking
                claw.openForIntake();
                intake.enable();
                lift.setHeight(RobotConstants.Lift.AUTO_INTAKE_1);
                armPosFunction = arm::setToRestingPos;  // TODO: See about just setting this directly to backwards now that arm PIDF is fixed
                wristPosFunction = wrist::setToBackwardsPos;
            })

            // Auto Cycle #1
            .setReversed(true)
            .splineTo(new Vector2d(in(AutoData.INTAKE_X), in(AutoData.INTAKE_Y)), rad(0))
            .UNSTABLE_addTemporalMarkerOffset(0, () -> armPosFunction = arm::setToBackwardsPos)
            .setReversed(false)
            .UNSTABLE_addTemporalMarkerOffset(AutoData.INTAKE_OFFSET, () -> claw.close())
            .UNSTABLE_addTemporalMarkerOffset(AutoData.INTAKE_LIFT_OFFSET, () -> {
                armPosFunction = arm::setToForwardsPos;

                lift.setHeight(RobotConstants.Lift.HIGH);
                wristPosFunction = wrist::setToForwardsPos;

            })
            .waitSeconds(AutoData.INTAKE_DELAY)

            .splineTo(new Vector2d(in(AutoData.DEPOSIT_X), in(AutoData.DEPOSIT_Y)), rad(AutoData.DEPOSIT_ANGLE))
            .UNSTABLE_addTemporalMarkerOffset(AutoData.LOWER_OFFSET, () -> lift.setHeight(RobotConstants.Lift.MID))
            .UNSTABLE_addTemporalMarkerOffset(AutoData.DEPOSIT_OFFSET, () -> {
                // Deposit the cone while turning
                claw.openForDeposit();
            })

            .waitSeconds(AutoData.DEPOSIT_DELAY)

            .UNSTABLE_addTemporalMarkerOffset(AutoData.RETRACT_OFFSET, () -> {
                // Prepare the robot for intaking
                claw.openForIntake();
                intake.enable();
                lift.setHeight(RobotConstants.Lift.AUTO_INTAKE_1);
                armPosFunction = arm::setToBackwardsPos;

                wristPosFunction = wrist::setToBackwardsPos;

            })


            // Auto Cycle #2
            .setReversed(true)
            .splineTo(new Vector2d(in(AutoData.INTAKE_X), in(AutoData.INTAKE_Y)), rad(0))
            .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                armPosFunction = arm::setToBackwardsPos; // TODO: Move this earlier to reduce time it takes to intake

            })
            .setReversed(false)
            .UNSTABLE_addTemporalMarkerOffset(AutoData.INTAKE_OFFSET, () -> claw.close())
            .UNSTABLE_addTemporalMarkerOffset(AutoData.INTAKE_LIFT_OFFSET, () -> {
                armPosFunction = arm::setToForwardsPos;

                lift.setHeight(RobotConstants.Lift.HIGH);
                wristPosFunction = wrist::setToForwardsPos;

            })
            .waitSeconds(AutoData.INTAKE_DELAY)

            .splineTo(new Vector2d(in(AutoData.DEPOSIT_X), in(AutoData.DEPOSIT_Y)), rad(AutoData.DEPOSIT_ANGLE))
            .UNSTABLE_addTemporalMarkerOffset(AutoData.LOWER_OFFSET, () -> lift.setHeight(RobotConstants.Lift.MID))
            .UNSTABLE_addTemporalMarkerOffset(AutoData.DEPOSIT_OFFSET, () -> {
                // Deposit the cone while turning
                claw.openForDeposit();
            })

            .waitSeconds(AutoData.DEPOSIT_DELAY)

            .UNSTABLE_addTemporalMarkerOffset(AutoData.RETRACT_OFFSET, () -> {
                // Prepare the robot for intaking
                claw.openForIntake();
                intake.enable();
                lift.setHeight(RobotConstants.Lift.AUTO_INTAKE_2);
                armPosFunction = arm::setToBackwardsPos;

                wristPosFunction = wrist::setToBackwardsPos;
            })


            // Auto Cycle #3
            .setReversed(true)
            .splineTo(new Vector2d(in(AutoData.INTAKE_X), in(AutoData.INTAKE_Y)), rad(0))
            .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                armPosFunction = arm::setToBackwardsPos; // TODO: Move this earlier to reduce time it takes to intake

            })
            .setReversed(false)
            .UNSTABLE_addTemporalMarkerOffset(AutoData.INTAKE_OFFSET, () -> claw.close())
            .UNSTABLE_addTemporalMarkerOffset(AutoData.INTAKE_LIFT_OFFSET, () -> {
                armPosFunction = arm::setToForwardsPos;

                lift.setHeight(RobotConstants.Lift.HIGH);
                wristPosFunction = wrist::setToForwardsPos;

            })
            .waitSeconds(AutoData.INTAKE_DELAY)

            .splineTo(new Vector2d(in(AutoData.DEPOSIT_X), in(AutoData.DEPOSIT_Y)), rad(AutoData.DEPOSIT_ANGLE))
            .UNSTABLE_addTemporalMarkerOffset(AutoData.LOWER_OFFSET, () -> lift.setHeight(RobotConstants.Lift.MID))
            .UNSTABLE_addTemporalMarkerOffset(AutoData.DEPOSIT_OFFSET, () -> {
                // Deposit the cone while turning
                claw.openForDeposit();
            })

            .waitSeconds(AutoData.DEPOSIT_DELAY)

            .UNSTABLE_addTemporalMarkerOffset(AutoData.RETRACT_OFFSET, () -> {
                // Prepare the robot for intaking
                claw.openForIntake();
                intake.enable();
                lift.setHeight(RobotConstants.Lift.AUTO_INTAKE_3);
                armPosFunction = arm::setToBackwardsPos;

                wristPosFunction = wrist::setToBackwardsPos;
            })

            // Auto Cycle #4
            .setReversed(true)
            .splineTo(new Vector2d(in(AutoData.INTAKE_X), in(AutoData.INTAKE_Y)), rad(0))
            .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                armPosFunction = arm::setToBackwardsPos; // TODO: Move this earlier to reduce time it takes to intake

            })
            .setReversed(false)
            .UNSTABLE_addTemporalMarkerOffset(AutoData.INTAKE_OFFSET, () -> claw.close())
            .UNSTABLE_addTemporalMarkerOffset(AutoData.INTAKE_LIFT_OFFSET, () -> {
                armPosFunction = arm::setToForwardsPos;

                lift.setHeight(RobotConstants.Lift.HIGH);
                wristPosFunction = wrist::setToForwardsPos;

            })
            .waitSeconds(AutoData.INTAKE_DELAY)

            .splineTo(new Vector2d(in(AutoData.DEPOSIT_X), in(AutoData.DEPOSIT_Y)), rad(AutoData.DEPOSIT_ANGLE))
            .UNSTABLE_addTemporalMarkerOffset(AutoData.LOWER_OFFSET, () -> lift.setHeight(RobotConstants.Lift.MID))
            .UNSTABLE_addTemporalMarkerOffset(AutoData.DEPOSIT_OFFSET, () -> {
                // Deposit the cone while turning
                claw.openForDeposit();
            })

            .waitSeconds(AutoData.DEPOSIT_DELAY)

            .UNSTABLE_addTemporalMarkerOffset(AutoData.RETRACT_OFFSET, () -> {
                // Prepare the robot for intaking
                claw.openForIntake();
                intake.enable();
                lift.setHeight(RobotConstants.Lift.AUTO_INTAKE_4);
                armPosFunction = arm::setToBackwardsPos;

                wristPosFunction = wrist::setToBackwardsPos;
            })

            .back(in(10))
            .turn(rad(50))
            .UNSTABLE_addTemporalMarkerOffset(0, () -> startParking = true)

            .build();


        drive.followTrajectorySequenceAsync(trajSeq);

        // Park in zone 1
        TrajectorySequence parkTraj1 = drive.trajectorySequenceBuilder(startPose)
            .forward(in(50))
            .build();

        // Park in zone 3
        TrajectorySequence parkTraj3 = drive.trajectorySequenceBuilder(startPose)
            .back(in(50))
            .build();

        // TODO: Implement parking code
        // I don't know if this works.
//        while (!startParking);
//
//        if(signalZone == 1)
//            drive.followTrajectorySequence(parkTraj1);
//        if(signalZone == 3)
//            drive.followTrajectorySequence(parkTraj3);

    }

    private static double in(double cm) {
        return cm / 2.54;
    }

    private static double rad(double deg) {
        return deg * Math.PI / 180;
    }
}
