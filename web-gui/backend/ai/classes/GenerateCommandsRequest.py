from pydantic import BaseModel


class GenerateCommandsRequest(BaseModel):
    query: str