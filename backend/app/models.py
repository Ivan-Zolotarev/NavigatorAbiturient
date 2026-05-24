from sqlalchemy import Boolean, ForeignKey, Integer, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database import Base


class College(Base):
    __tablename__ = "colleges"

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    name: Mapped[str] = mapped_column(Text, nullable=False)
    type: Mapped[str] = mapped_column(Text, nullable=False)
    short_description: Mapped[str] = mapped_column(Text, nullable=False)
    district: Mapped[str | None] = mapped_column(Text)
    address: Mapped[str | None] = mapped_column(Text)
    website_url: Mapped[str | None] = mapped_column(Text)
    tuition_cost: Mapped[str | None] = mapped_column(Text)
    installment_available: Mapped[bool | None] = mapped_column(Boolean)
    special_admission_conditions: Mapped[str | None] = mapped_column(Text)
    has_dormitory: Mapped[bool | None] = mapped_column(Boolean)

    specialties: Mapped[list["Specialty"]] = relationship(
        back_populates="college",
        cascade="all, delete-orphan",
    )


class Specialty(Base):
    __tablename__ = "specialties"

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    college_id: Mapped[int] = mapped_column(ForeignKey("colleges.id", ondelete="CASCADE"))
    name: Mapped[str] = mapped_column(Text, nullable=False)
    budget_places: Mapped[int | None] = mapped_column(Integer)
    paid_places: Mapped[int | None] = mapped_column(Integer)
    passing_score: Mapped[int | None] = mapped_column(Integer)
    admission_info: Mapped[str | None] = mapped_column(Text)

    college: Mapped[College] = relationship(back_populates="specialties")


class Document(Base):
    __tablename__ = "documents"

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    title: Mapped[str] = mapped_column(Text, nullable=False)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    pdf_url: Mapped[str | None] = mapped_column(Text)
    link_url: Mapped[str | None] = mapped_column(Text)
    sort_order: Mapped[int] = mapped_column(Integer, default=0)


class FaqItem(Base):
    __tablename__ = "faq_items"

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    question: Mapped[str] = mapped_column(Text, nullable=False)
    answer: Mapped[str] = mapped_column(Text, nullable=False)
    sort_order: Mapped[int] = mapped_column(Integer, default=0)
