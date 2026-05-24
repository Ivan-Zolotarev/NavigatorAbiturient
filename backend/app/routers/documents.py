from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.crud import list_documents
from app.database import get_db
from app.schemas import DocumentsResponse

router = APIRouter(tags=["documents"])


@router.get("/documents", response_model=DocumentsResponse)
def get_documents(db: Session = Depends(get_db)):
    return DocumentsResponse(documents=list_documents(db))
