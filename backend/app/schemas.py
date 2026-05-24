from typing import Optional

from pydantic import BaseModel, ConfigDict


class SpecialtyOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    name: str
    budget_places: Optional[int] = None
    paid_places: Optional[int] = None
    passing_score: Optional[int] = None
    admission_info: Optional[str] = None


class CollegeOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    name: str
    type: str
    short_description: str
    district: Optional[str] = None
    address: Optional[str] = None
    website_url: Optional[str] = None
    specialties: list[SpecialtyOut] = []
    tuition_cost: Optional[str] = None
    installment_available: Optional[bool] = None
    special_admission_conditions: Optional[str] = None
    has_dormitory: Optional[bool] = None


class CollegesResponse(BaseModel):
    colleges: list[CollegeOut]


class DocumentOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    title: str
    content: str
    pdf_url: Optional[str] = None
    link_url: Optional[str] = None


class DocumentsResponse(BaseModel):
    documents: list[DocumentOut]


class FaqItemOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    question: str
    answer: str


class FaqResponse(BaseModel):
    items: list[FaqItemOut]
