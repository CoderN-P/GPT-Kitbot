from pydantic import BaseModel, Field
from typing import Optional
from .CommandEnum import CommandEnum
from .DriveCommand import DriveCommand
from .RollerCommand import RollerCommand
from uuid import uuid4


class Command(BaseModel):
    """
    A class to represent a command.

    Attributes:
        command (str): The command type
        command (str): The command that was executed.
        duration (float): The duration for command execution - used if there are no encoders for drive and always used for roller
        distance (float): The distance for command execution - used if there are encoders for more accurate drive
    """

    id: str = Field(description="Unique ID for the command")
    command_type: CommandEnum
    command: DriveCommand | RollerCommand
    duration: float = Field(description="Duration for command execution (seconds)")
    pause_duration: float = Field(description="Duration to pause after command execution (seconds)")
    
    def __init__(self, **data):
        super().__init__(**data)
        self.id = str(uuid4())

