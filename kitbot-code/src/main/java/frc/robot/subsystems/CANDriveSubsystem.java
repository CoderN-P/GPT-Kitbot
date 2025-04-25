// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

// Class to drive the robot over CAN
public class CANDriveSubsystem extends SubsystemBase {
  private final WPI_TalonSRX leftLeader;
  private final WPI_TalonSRX leftFollower;
  private final WPI_TalonSRX rightLeader;
  private final WPI_TalonSRX rightFollower;

  private final DifferentialDrive drive;

  public CANDriveSubsystem() {
    leftLeader = new WPI_TalonSRX(DriveConstants.LEFT_LEADER_ID);
    leftFollower = new WPI_TalonSRX(DriveConstants.LEFT_FOLLOWER_ID);
    rightLeader = new WPI_TalonSRX(DriveConstants.RIGHT_LEADER_ID);
    rightFollower = new WPI_TalonSRX(DriveConstants.RIGHT_FOLLOWER_ID);

    leftLeader.setNeutralMode(NeutralMode.Brake);
    leftFollower.setNeutralMode(NeutralMode.Brake);
    rightLeader.setNeutralMode(NeutralMode.Brake);
    rightFollower.setNeutralMode(NeutralMode.Brake);

    leftFollower.follow(leftLeader);
    rightFollower.follow(rightLeader);

    leftLeader.setInverted(true);
    rightLeader.setInverted(false);

    // set up differential drive class
    drive = new DifferentialDrive(leftLeader, rightLeader);
  }

  @Override
  public void periodic() {
  }

  // sets the speed of the drive motors
  public void driveArcade(double xSpeed, double zRotation) {
    drive.arcadeDrive(xSpeed, zRotation);
  }
}
