from sqlalchemy import exists, func, or_, select
from sqlalchemy.orm import Session, selectinload

from app.models import College, Document, FaqItem, Specialty


def list_colleges(db: Session, college_type: str | None = None) -> list[College]:
    stmt = select(College).options(selectinload(College.specialties)).order_by(College.name)
    if college_type:
        stmt = stmt.where(College.type == college_type)
    return list(db.scalars(stmt).all())


def get_college(db: Session, college_id: int) -> College | None:
    stmt = (
        select(College)
        .options(selectinload(College.specialties))
        .where(College.id == college_id)
    )
    return db.scalars(stmt).first()


def search_colleges(
    db: Session,
    query: str = "",
    budget: bool | None = None,
    paid: bool | None = None,
    min_score: int | None = None,
    district: str | None = None,
) -> list[College]:
    stmt = select(College).options(selectinload(College.specialties))

    normalized = query.strip()
    if normalized:
        pattern = f"%{normalized}%"
        stmt = stmt.where(
            or_(
                College.name.ilike(pattern),
                College.id.in_(
                    select(Specialty.college_id).where(Specialty.name.ilike(pattern)),
                ),
            ),
        )

    if district:
        stmt = stmt.where(func.lower(College.district) == district.lower())

    if budget:
        stmt = stmt.where(
            exists(
                select(Specialty.id).where(
                    Specialty.college_id == College.id,
                    func.coalesce(Specialty.budget_places, 0) > 0,
                ),
            ),
        )

    if paid:
        stmt = stmt.where(
            exists(
                select(Specialty.id).where(
                    Specialty.college_id == College.id,
                    func.coalesce(Specialty.paid_places, 0) > 0,
                ),
            ),
        )

    if min_score is not None:
        stmt = stmt.where(
            exists(
                select(Specialty.id).where(
                    Specialty.college_id == College.id,
                    func.coalesce(Specialty.passing_score, 0) >= min_score,
                ),
            ),
        )

    stmt = stmt.order_by(College.name)
    return list(db.scalars(stmt).unique().all())


def list_documents(db: Session) -> list[Document]:
    stmt = select(Document).order_by(Document.sort_order, Document.id)
    return list(db.scalars(stmt).all())


def list_faq(db: Session) -> list[FaqItem]:
    stmt = select(FaqItem).order_by(FaqItem.sort_order, FaqItem.id)
    return list(db.scalars(stmt).all())
