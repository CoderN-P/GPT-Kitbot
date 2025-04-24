import { z } from 'zod';
import { CommandEnum } from "./CommandEnum";
import { DriveCommandSchema } from "./DriveCommand";
import { RollerCommandSchema } from "./RollerCommand";


export const CommandSchema = z.object({
    command_type: z.nativeEnum(CommandEnum),
    command: z.union([DriveCommandSchema, RollerCommandSchema]),
    duration: z.number().min(0).max(5).nullable().optional(),
    distance: z.number().min(0).max(5).nullable().optional(),
    pause_duration: z.number().min(0).default(0),
});

export type Command = z.infer<typeof CommandSchema>;