-- Примеры SQL для FastAPI (ориентир для эндпоинтов приложения)
-- Не выполнять как миграцию — только справочник.

-- GET /colleges?type=state
-- SELECT * FROM colleges WHERE type = 'state' ORDER BY name;

-- GET /colleges/{id} + specialties (вложенный JSON собирается в коде)
-- SELECT c.*, s.* FROM colleges c
-- LEFT JOIN specialties s ON s.college_id = c.id
-- WHERE c.id = :id;

-- GET /colleges/search?q=дизайн&budget=true&minScore=4&district=Центральный
/*
SELECT DISTINCT c.*
FROM colleges c
LEFT JOIN specialties s ON s.college_id = c.id
WHERE (
    :q IS NULL OR :q = '' OR
    c.name ILIKE '%' || :q || '%' OR
    s.name ILIKE '%' || :q || '%'
)
AND (:district IS NULL OR c.district ILIKE :district)
AND (
    :budget IS NOT TRUE OR EXISTS (
        SELECT 1 FROM specialties sx
        WHERE sx.college_id = c.id AND COALESCE(sx.budget_places, 0) > 0
    )
)
AND (
    :paid IS NOT TRUE OR EXISTS (
        SELECT 1 FROM specialties sx
        WHERE sx.college_id = c.id AND COALESCE(sx.paid_places, 0) > 0
    )
)
AND (
    :min_score IS NULL OR EXISTS (
        SELECT 1 FROM specialties sx
        WHERE sx.college_id = c.id AND COALESCE(sx.passing_score, 0) >= :min_score
    )
)
ORDER BY c.name;
*/

-- GET /documents
-- SELECT id, title, content, pdf_url, link_url FROM documents ORDER BY sort_order, id;

-- GET /faq  →  JSON { "items": [...] }
-- SELECT id, question, answer FROM faq_items ORDER BY sort_order, id;
