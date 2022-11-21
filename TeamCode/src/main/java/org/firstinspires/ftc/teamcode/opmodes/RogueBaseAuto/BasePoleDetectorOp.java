package org.firstinspires.ftc.teamcode.opmodes.RogueBaseAuto;

import android.annotation.SuppressLint;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.pipelines.BasePoleDetector;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequenceBuilder;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous
public class BasePoleDetectorOp extends RougeBaseAuto {
    private OpenCvCamera camera;
    private BasePoleDetector poleDetectorPipeline;


    /**
     * Override this method and place your code here.
     * <p>
     * Please do not swallow the InterruptedException, as it is used in cases
     * where the op mode needs to be terminated early.
     *
     * @throws InterruptedException
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();

        // Switch from AprilTag detector to the base pole detector
        setPoleDetectorAsPipeline();

        double[] polePos = {0, 0};
        while(!opModeIsActive()){
            polePos = getPolePosition();
            telemetry.addLine(String.format("Pole X: %.2f cm", polePos[0]));
            telemetry.addLine(String.format("Pole Y: %.2f cm", polePos[1]));
        }
        telemetry.addData(String.format("Final pole X: %.2f cm", polePos[0]), "");
        telemetry.addData(String.format("Final pole Y: %.2f cm", polePos[1]), "");
        telemetry.update();

        TrajectorySequence alignTraj = drive.trajectorySequenceBuilder(new Pose2d())
                .lineTo(new Vector2d(polePos[0], polePos[1]))
                .build();


    }
}
