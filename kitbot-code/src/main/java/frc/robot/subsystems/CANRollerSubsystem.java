// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.RollerConstants;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

/** Class to run the rollers over CAN */
public class CANRollerSubsystem extends SubsystemBase {
  private final WPI_TalonSRX rollerMotor;

  public CANRollerSubsystem() {
    rollerMotor = new WPI_TalonSRX(RollerConstants.ROLLER_MOTOR_ID);
    rollerMotor.setNeutralMode(NeutralMode.Coast);
    rollerMotor.configVoltageCompSaturation(RollerConstants.ROLLER_MOTOR_VOLTAGE_COMP);
    rollerMotor.enableVoltageCompensation(true);
    rollerMotor.configContinuousCurrentLimit(RollerConstants.ROLLER_MOTOR_CURRENT_LIMIT);
    rollerMotor.enableCurrentLimit(true);
  }

  @Override
  public void periodic() {
  }

  /** This is a method that makes the roller spin */
  public void runRoller(double forward, double reverse) {
    rollerMotor.set(ControlMode.PercentOutput, forward - reverse);
  }
}
