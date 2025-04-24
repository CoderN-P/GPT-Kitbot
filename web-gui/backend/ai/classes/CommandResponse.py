from pydantic import BaseModel, Field
from typing import List
from .Command import Command


class CommandResponse(BaseModel):
    """
    A class to represent a GPT command response.
    """
    
    commands: List[Command] = Field(description="List of commands to be executed")