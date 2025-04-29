from pydantic import BaseModel, Field


class DriveCommand(BaseModel):
    """
    A class to represent a drive command.

    Attributes:
        command (str): The command that was executed.
        params (str): The parameters for the command.
        status (str): The status of the command execution.
    """

    speed: float = Field(ge=-1, le=1, description="Speed for arcade drive")
    rotation: float = Field(ge=-1, le=1, description="Rotation for arcade drive")
    