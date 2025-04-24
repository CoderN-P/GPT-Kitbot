package frc.robot.classes;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.commands.DriveCommand;

public class GPTDriveCommand {
    private double speed;
    private double rotation;

    public GPTDriveCommand(double speed, double rotation) {
        this.speed = speed;
        this.rotation = rotation;
    }

    public void run(double duration, CANDriveSubsystem driveSubsystem) {
        // Schedule the command to run for the specified duration
        CommandScheduler.getInstance().schedule(
                new DriveCommand(speed, rotation, driveSubsystem).withTimeout(duration));

    }
}