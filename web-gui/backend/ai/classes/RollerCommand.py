from pydantic import BaseModel, Field


class RollerCommand(BaseModel):
    """
    A class to represent a roller command.

    Attributes:
        forward (float): The forward speed for roller drive.
        backward (float): The backward speed for roller drive.
    """

    forward: float = Field(ge=0, le=1, description="Forward speed for roller drive")
    backward: float = Field(ge=0, le=1, description="Backward speed for roller drive")