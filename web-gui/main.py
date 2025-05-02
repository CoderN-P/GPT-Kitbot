from quart import Quart
from dotenv import load_dotenv
load_dotenv()
import ntcore
from quart_cors import cors
from pydantic.json import pydantic_encoder
import os, json, asyncio
from quart_schema import validate_request, validate_response, QuartSchema
from typing import List
from backend import retrieve_commands, GenerateCommandsRequest, Command
import socketio
import threading

app = Quart(__name__)
app = cors(app, allow_credentials=True, allow_origin="http://localhost:3000")
QuartSchema(app)

sio = socketio.AsyncServer(async_mode='asgi',
                           cors_allowed_origins=[
                               'http://localhost:3000',
                           ])

main_app = socketio.ASGIApp(sio, app)

nt = ntcore.NetworkTableInstance.getDefault()
nt.startClient4("CLI")
# connect to localhost
nt.setServer("localhost")
table = nt.getTable("flask_gui")
table.getEntry("command").setString("")
table.getEntry("active_command").setString("")
table.getEntry("status").setString("")
# Initialize emergency stop entry
table.getEntry("emergency_stop").setBoolean(False)

loop = asyncio.get_event_loop()

def listen_for_active_change():
    def value_changed(table, key, event: ntcore.Event):
        if event.data.topic.getName() == "/flask_gui/active_command":
            print(f"Active command changed: {event.data.value.value()}")
            asyncio.run_coroutine_threadsafe(sio.emit("active_command", {"id": event.data.value.value()}), loop)
        if event.data.topic.getName() == "/flask_gui/status":
            print(f"Status changed: {event.data.value.value()}")
            asyncio.run_coroutine_threadsafe(sio.emit("status", {"status": event.data.value.value()}), loop)

    table.addListener("active_command", ntcore.EventFlags.kValueAll, value_changed)
    table.addListener("status", ntcore.EventFlags.kValueAll, value_changed)

# Run the listener in a separate thread
listener_thread = threading.Thread(target=listen_for_active_change, daemon=True)
listener_thread.start()

@app.route("/generate", methods=["POST"])
@validate_request(GenerateCommandsRequest)
@validate_response(List[Command], 200)
async def generate_commands(data: GenerateCommandsRequest):
    """
    Generate commands based on the given prompt.
    
    Args:
        data (GenerateCommandsRequest): The request object containing the prompt.
    
    Returns:
        CommandResponse: A list of commands generated from the OpenAI API.
    """
    
    # Retrieve commands using the OpenAI API
    commands = await retrieve_commands(data.query)
    
    commands_json = json.dumps(commands, default=pydantic_encoder)
    
    try:
        table.getEntry("command").setString(commands_json)
    except Exception as e:
        print(f"Error setting commands in NetworkTables: {e}")
    
    return commands

@app.route("/emergency_stop", methods=["POST"])
async def emergency_stop():
    """
    Trigger an emergency stop of all robot operations.
    
    Returns:
        dict: A status message indicating the emergency stop was triggered.
    """
    try:
        table.getEntry("emergency_stop").setBoolean(True)
        await sio.emit("status", {"status": "Emergency stop activated"})
        return {"status": "Emergency stop activated"}
    except Exception as e:
        print(f"Error triggering emergency stop: {e}")
        return {"status": f"Error: {str(e)}"}, 500

import uvicorn

if __name__ == "__main__":
    uvicorn.run("main:main_app", host="localhost", port=8080, reload=True)
