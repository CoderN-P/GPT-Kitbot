package frc.robot.classes;

import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.commands.DriveCommand;
import edu.wpi.first.wpilibj2.command.Command;

public class GPTDriveCommand {
    private double speed;
    private double rotation;

    public GPTDriveCommand(double speed, double rotation) {
        this.speed = speed;
        this.rotation = rotation;
    }

    public Command getCommand(CANDriveSubsystem driveSubsystem) {
        return new DriveCommand(() -> speed, () -> rotation, driveSubsystem);
    }
}