package frc.robot.classes;

import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANRollerSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

public class GPTCommand {
    public CommandEnum command_type;
    public Object command;
    public String id;
    public double duration;
    public double pause_duration;

    public Command getCommand(CANDriveSubsystem driveSubsystem, CANRollerSubsystem rollerSubsystem) {
        switch (command_type) {
            case DRIVE:
                return ((GPTDriveCommand) command).getCommand(driveSubsystem, duration);
            case ROLLER:
                return ((GPTRollerCommand) command).getCommand(rollerSubsystem, duration);
            default:
                throw new IllegalArgumentException("Unknown command type: " + command_type);
        }
    }
}