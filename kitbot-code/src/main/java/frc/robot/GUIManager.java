package frc.robot;

import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANRollerSubsystem;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.RollerCommand;
import frc.robot.classes.CommandEnum;
import frc.robot.classes.GPTDriveCommand;
import frc.robot.classes.GPTRollerCommand;
import frc.robot.classes.GPTCommand;
import frc.robot.classes.CommandDeserializer;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;

class GUIManager {
    private CANDriveSubsystem driveSubsystem;
    private CANRollerSubsystem rollerSubsystem;
    private Gson gson;

    public GUIManager(CANDriveSubsystem driveSubsystem, CANRollerSubsystem rollerSubsystem) {
        this.driveSubsystem = driveSubsystem;
        this.rollerSubsystem = rollerSubsystem;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(GPTCommand.class, new CommandDeserializer())
                .create();
    }

    public void runGUICommands() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("flask_gui");

        String commandJSON = table.getEntry("command").getString("");
        Type commandListType = new TypeToken<List<GPTCommand>>() {
        }.getType();

        if (!commandJSON.isEmpty()) {
            try {
                // Fix: You were using GPTCommand.class instead of commandListType
                List<GPTCommand> commands = gson.fromJson(commandJSON, commandListType);

                for (GPTCommand command : commands) {
                    System.out.println("Running command: " + command.command_type);
                    command.run(driveSubsystem, rollerSubsystem);

                    // Add pause between commands if specified
                    if (command.pause_duration > 0) {
                        try {
                            Thread.sleep((long) (command.pause_duration * 1000));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.err.println("Command sequence interrupted during pause");
                            break;
                        }
                    }
                }

                // Optional: Report success back to NetworkTables
                table.getEntry("status").setString("Commands executed successfully");

            } catch (JsonSyntaxException e) {
                System.err.println("Failed to parse command JSON: " + e.getMessage());
                table.getEntry("status").setString("Error: Failed to parse commands");
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to run command: " + e.getMessage());
                table.getEntry("status").setString("Error: " + e.getMessage());
            }

            table.getEntry("command").setString(""); // Clear the command after processing
        }
    }
}