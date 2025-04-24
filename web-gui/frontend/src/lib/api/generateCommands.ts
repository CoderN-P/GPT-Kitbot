import { type Command, CommandSchema } from '$lib/types';

const API_URL = import.meta.env.VITE_BACKEND_URL;

export async function generateCommands(query: string): Promise<Command[]> {
    const response = await fetch(`${API_URL}/generate`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ query }),
    });
    
    if (!response.ok) {
        throw new Error('Failed to generate commands');
    }
    
    const data = await response.json();
    
    // Validate the response data against the Command schema
    return CommandSchema.array().parse(data);
}