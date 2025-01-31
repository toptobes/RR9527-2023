package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.components.taskchains.BackwardsDepositChain;
import org.firstinspires.ftc.teamcode.components.taskchains.ForwardsDepositChain;
import org.firstinspires.ftc.teamcode.components.taskchains.IntakeChain;
import org.firstinspires.ftc.teamcode.util.RobotConstants;
import org.firstinspires.ftc.teamcodekt.components.scheduler.taskchains.CancellableTaskChain;
import org.firstinspires.ftc.teamcodekt.components.scheduler.listeners.Listener;
import org.firstinspires.ftc.teamcodekt.components.scheduler.taskchains.TaskChain;

@TeleOp
public class RogueCompOp extends RogueBaseTeleOp {
    private TaskChain intakeChain;
    private CancellableTaskChain forwardsDepositChain, backwardsDepositChain;

    @Override
    public void scheduleTasks() {
        codriver.dpad_up   .onRise(lift::goToHigh);
        codriver.dpad_down .onRise(lift::goToZero);
        codriver.dpad_right.onRise(lift::goToMid);
        codriver.dpad_left .onRise(lift::goToLow);

        intakeChain.invokeOn(codriver.left_bumper);

        forwardsDepositChain.invokeOn(codriver.right_bumper);
        forwardsDepositChain.cancelOn(codriver.x);

        backwardsDepositChain.invokeOn(codriver.y);
        backwardsDepositChain.cancelOn(codriver.x);

        codriver.right_stick_y(.2).whileHigh(() -> {
            lift.setHeight(lift.getHeight() + (int) (RobotConstants.Lift.MANUAL_ADJUSTMENT_MULT * Math.pow(-gamepad2.right_stick_y, 3)));
        });

        driver.left_trigger(.1).whileHigh(this::halveDriveSpeed);
        driver.right_trigger(.1).whileHigh(this::decreaseDriveSpeedABit);

        new Listener(() -> lift.getHeight() > 1525)
            .whileHigh(() -> powerMulti /= 2);
    }

    @Override
    protected void initAdditionalHardware() {
        intakeChain = new IntakeChain(bot, 200);
        forwardsDepositChain = new ForwardsDepositChain(bot, 500);
        backwardsDepositChain = new BackwardsDepositChain(bot, 500);
    }
}
