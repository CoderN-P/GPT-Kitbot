import { z } from 'zod';

export const RollerCommandSchema = z.object({
    forward: z.number().min(0).max(1).describe("Forward speed for roller drive"),
    backward: z.number().min(0).max(1).describe("Backward speed for roller drive"),
});

export type RollerCommand = z.infer<typeof RollerCommandSchema>;