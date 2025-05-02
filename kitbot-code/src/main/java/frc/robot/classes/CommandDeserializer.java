package frc.robot.classes;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.RollerCommand;
import frc.robot.classes.CommandEnum;
import frc.robot.classes.GPTCommand;

public class CommandDeserializer implements JsonDeserializer<GPTCommand> {

    @Override
    public GPTCommand deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();
        CommandEnum type = null;
        for (CommandEnum enumValue : CommandEnum.values()) {
            if (enumValue.name().equalsIgnoreCase(obj.get("command_type").getAsString()) ||
                    enumValue.toString().equalsIgnoreCase(obj.get("command_type").getAsString())) {
                type = enumValue;
                break;
            }
        }

        if (type == null) {
            throw new JsonParseException("Unknown command_type: " + obj.get("command_type").getAsString());
        }

        GPTCommand cmd = new GPTCommand();
        cmd.command_type = type;
        cmd.id = obj.get("id").getAsString();
        cmd.duration = obj.get("duration").getAsDouble();
        cmd.pause_duration = obj.get("pause_duration").getAsDouble();

        JsonObject commandData = obj.get("command").getAsJsonObject();

        switch (type) {
            case DRIVE:
                cmd.command = context.deserialize(commandData, GPTDriveCommand.class);
                break;
            case ROLLER:
                cmd.command = context.deserialize(commandData, GPTRollerCommand.class);
                break;
            default:
                throw new JsonParseException("Unknown command_type: " + type);
        }

        return cmd;
    }
}