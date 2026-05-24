from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session

from app.crud import get_college, list_colleges, search_colleges
from app.database import get_db
from app.schemas import CollegeOut, CollegesResponse

router = APIRouter(prefix="/colleges", tags=["colleges"])


@router.get("", response_model=CollegesResponse)
def get_colleges(
    type: str | None = Query(default=None, pattern="^(state|private|federal)$"),
    db: Session = Depends(get_db),
):
    colleges = list_colleges(db, college_type=type)
    return CollegesResponse(colleges=colleges)


@router.get("/search", response_model=CollegesResponse)
def search(
    q: str = Query(default=""),
    budget: bool | None = Query(default=None),
    paid: bool | None = Query(default=None),
    minScore: int | None = Query(default=None, ge=0),
    district: str | None = Query(default=None),
    db: Session = Depends(get_db),
):
    colleges = search_colleges(
        db,
        query=q,
        budget=budget,
        paid=paid,
        min_score=minScore,
        district=district,
    )
    return CollegesResponse(colleges=colleges)


@router.get("/{college_id}", response_model=CollegeOut)
def get_college_by_id(college_id: int, db: Session = Depends(get_db)):
    college = get_college(db, college_id)
    if college is None:
        raise HTTPException(status_code=404, detail="College not found")
    return college
