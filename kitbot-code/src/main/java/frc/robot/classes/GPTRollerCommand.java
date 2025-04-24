package frc.robot.classes;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.CANRollerSubsystem;
import frc.robot.commands.RollerCommand;

public class GPTRollerCommand {
    private double forward;
    private double backward;

    public GPTRollerCommand(double forward, double backward) {
        this.forward = forward;
        this.backward = backward;
    }

    public void run(double duration, CANRollerSubsystem rollerSubsystem) {
        // Schedule the command to run for the specified duration
        CommandScheduler.getInstance().schedule(
                new RollerCommand(forward, backward, rollerSubsystem).withTimeout(duration));

    }
}