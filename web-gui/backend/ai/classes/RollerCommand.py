from pydantic import BaseModel, Field


class RollerCommand(BaseModel):
    """
    A class to represent a roller command.

    Attributes:
        forward (float): The forward speed for roller drive.
        backward (float): The backward speed for roller drive.
    """

    forward: float = Field(..., description="Forward speed for roller drive")
    backward: float = Field(..., description="Backward speed for roller drive")