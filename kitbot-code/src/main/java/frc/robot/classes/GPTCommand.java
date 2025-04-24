package frc.robot.classes;

import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANRollerSubsystem;

public class GPTCommand {
    public CommandEnum command_type;
    public Object command;
    public double duration;
    public double pause_duration;

    public void run(CANDriveSubsystem driveSubsystem, CANRollerSubsystem rollerSubsystem) {
        switch (command_type) {
            case DRIVE:
                ((GPTDriveCommand) command).run(duration, driveSubsystem);
                break;
            case ROLLER:
                ((GPTRollerCommand) command).run(duration, rollerSubsystem);
                break;
            default:
                throw new IllegalArgumentException("Unknown command type: " + command_type);
        }
    }
}