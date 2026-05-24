from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.crud import list_faq
from app.database import get_db
from app.schemas import FaqResponse

router = APIRouter(tags=["faq"])


@router.get("/faq", response_model=FaqResponse)
def get_faq(db: Session = Depends(get_db)):
    return FaqResponse(items=list_faq(db))
