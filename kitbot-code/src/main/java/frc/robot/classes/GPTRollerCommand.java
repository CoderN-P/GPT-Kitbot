package frc.robot.classes;

import frc.robot.subsystems.CANRollerSubsystem;
import frc.robot.commands.RollerCommand;
import edu.wpi.first.wpilibj2.command.Command;

public class GPTRollerCommand {
    private double forward;
    private double backward;

    public GPTRollerCommand(double forward, double backward) {
        this.forward = forward;
        this.backward = backward;
    }

    public Command getCommand(CANRollerSubsystem rollerSubsystem, double duration) {
        return new RollerCommand(() -> forward, () -> backward, rollerSubsystem).withTimeout(duration);
    }
}