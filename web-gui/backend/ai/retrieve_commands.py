from openai import AsyncOpenAI
import os
from typing import List
from . import CommandResponse, Command


client = AsyncOpenAI(api_key=os.environ.get("OPENAI_API_KEY"))


async def retrieve_commands(query: str) -> List[Command]:
    """
    Retrieve commands from the OpenAI API based on the given prompt.
    
    Args:
        query (str): The prompt to send to the OpenAI API.
    
    Returns:
        CommandResponse: A list of commands retrieved from the OpenAI API.
    """
    # Initialize OpenAI API client
    
    # Send the prompt to the OpenAI API and get the response
    response = await client.responses.parse(
        model="gpt-4o-mini",
        instructions=open("backend/ai/prompts/CommandsPrompt.txt").read(),
        input=query,
        text_format=CommandResponse,
    )
    
    event = response.output[0].content[0]
    
    print("Received response:", event.parsed)
    
    return event.parsed.commands