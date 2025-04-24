import { z } from 'zod';

export const DriveCommandSchema = z.object({
    speed: z.number().min(-1).max(1).describe("Speed for arcade drive"),
    rotation: z.number().min(-1).max(1).describe("Rotation for arcade drive"),
});

export type DriveCommand = z.infer<typeof DriveCommandSchema>;