from quart import Quart
from dotenv import load_dotenv
load_dotenv()
#from pynetworktables import NetworkTableInstance
from quart_cors import cors
import os
from quart_schema import validate_request, validate_response, QuartSchema
from typing import List
from backend import retrieve_commands, GenerateCommandsRequest, Command

app = Quart(__name__)
app = cors(app, allow_credentials=True, allow_origin="http://localhost:3000")
QuartSchema(app)

nt = NetworkTableInstance.getDefault()
nt.startClient4("CLI")
nt.setServerTeam(int(os.environ.get("1351", 0)))
table = nt.getTable("flask_gui")



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
    
    table.getEntry("commands").setString(commands.model_dump_json())
    
    return commands


if __name__ == "__main__":
    app.run(port=8080, host='localhost')


