from ollama import AsyncClient
import os
from typing import List
import json
from . import CommandResponse, Command


client = AsyncClient()


async def retrieve_commands(query: str) -> List[Command]:
    """
    Retrieve commands from the OpenAI API based on the given prompt.
    
    Args:
        query (str): The prompt to send to the OpenAI API.
    
    Returns:
        CommandResponse: A list of commands retrieved from the OpenAI API.
    """
    
    # Send the prompt to the OpenAI API and get the response
    response = await client.chat(
        model="mistral:7b-instruct",
        messages=[
            {"role": "system", "content": open("backend/ai/prompts/CommandsPrompt.txt").read()},
            {"role": "user", "content": query},
        ],
        format=CommandResponse.model_json_schema(),
    )
    
    data = response.message.content
    
    return CommandResponse.model_validate_json(data).commands
    
    