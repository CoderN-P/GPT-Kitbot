import { io } from './socketClient';

const API_URL = import.meta.env.VITE_BACKEND_URL;

// Interface for manual command parameters
interface ManualCommandParams {
    command_type: 'drive' | 'roller';
    duration?: number;
    speed?: number;
    rotation?: number;
    forward?: number;
    backward?: number;
}

/**
 * Sends a manual control command to the robot
 */
export async function sendManualCommand(params: ManualCommandParams): Promise<{status: string, command_id: string}> {
    const response = await fetch(`${API_URL}/manual_control`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(params),
    });
    
    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.status || 'Failed to send manual command');
    }
    
    return response.json();
}