from pydantic import BaseModel, Field


class DriveCommand(BaseModel):
    """
    A class to represent a drive command.

    Attributes:
        command (str): The command that was executed.
        params (str): The parameters for the command.
        status (str): The status of the command execution.
    """

    speed: float = Field(..., description="Speed for arcade drive")
    rotation: float = Field(..., description="Rotation for arcade drive")
    