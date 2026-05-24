-- Навигатор Абитуриента — схема PostgreSQL
-- Соответствует mock JSON в app/src/main/assets/ и контракту Retrofit API.
-- Запуск: psql или Query Tool в pgAdmin на БД navigator.

BEGIN;

-- ---------------------------------------------------------------------------
-- Типы
-- ---------------------------------------------------------------------------

CREATE TYPE college_type AS ENUM ('state', 'private', 'federal');

-- ---------------------------------------------------------------------------
-- Таблицы
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS colleges (
    id                          INTEGER PRIMARY KEY,
    name                        TEXT NOT NULL,
    type                        college_type NOT NULL,
    short_description           TEXT NOT NULL,
    district                    TEXT,
    address                     TEXT,
    website_url                 TEXT,
    tuition_cost                TEXT,
    installment_available       BOOLEAN,
    special_admission_conditions TEXT,
    has_dormitory               BOOLEAN,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS specialties (
    id              INTEGER PRIMARY KEY,
    college_id      INTEGER NOT NULL REFERENCES colleges(id) ON DELETE CASCADE,
    name            TEXT NOT NULL,
    budget_places   INTEGER CHECK (budget_places IS NULL OR budget_places >= 0),
    paid_places     INTEGER CHECK (paid_places IS NULL OR paid_places >= 0),
    passing_score   INTEGER CHECK (passing_score IS NULL OR passing_score >= 0),
    admission_info  TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS documents (
    id          INTEGER PRIMARY KEY,
    title       TEXT NOT NULL,
    content     TEXT NOT NULL,
    pdf_url     TEXT,
    link_url    TEXT,
    sort_order  INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS faq_items (
    id          INTEGER PRIMARY KEY,
    question    TEXT NOT NULL,
    answer      TEXT NOT NULL,
    sort_order  INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ---------------------------------------------------------------------------
-- Индексы (списки, фильтры, поиск)
-- ---------------------------------------------------------------------------

CREATE INDEX IF NOT EXISTS idx_colleges_type ON colleges(type);
CREATE INDEX IF NOT EXISTS idx_colleges_district ON colleges(district);
CREATE INDEX IF NOT EXISTS idx_colleges_name ON colleges(name);
CREATE INDEX IF NOT EXISTS idx_specialties_college_id ON specialties(college_id);
CREATE INDEX IF NOT EXISTS idx_specialties_name ON specialties(name);

-- ---------------------------------------------------------------------------
-- updated_at
-- ---------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_colleges_updated_at ON colleges;
CREATE TRIGGER trg_colleges_updated_at
    BEFORE UPDATE ON colleges
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_specialties_updated_at ON specialties;
CREATE TRIGGER trg_specialties_updated_at
    BEFORE UPDATE ON specialties
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_documents_updated_at ON documents;
CREATE TRIGGER trg_documents_updated_at
    BEFORE UPDATE ON documents
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_faq_items_updated_at ON faq_items;
CREATE TRIGGER trg_faq_items_updated_at
    BEFORE UPDATE ON faq_items
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ---------------------------------------------------------------------------
-- Seed: colleges + specialties (mock_colleges.json)
-- ---------------------------------------------------------------------------

INSERT INTO colleges (
    id, name, type, short_description, district, address, website_url,
    tuition_cost, installment_available, special_admission_conditions, has_dormitory
) VALUES
    (1, 'Колледж связи № 54', 'state',
     'Информационные технологии, телекоммуникации — 120 мест',
     'Центральный', 'Москва, ул. Большая Почтовая, 18', 'https://www.mos.ru',
     NULL, NULL, NULL, NULL),
    (2, 'Политехнический колледж № 8', 'state',
     'Инженерные специальности — 95 мест',
     'Юго-Западный', 'Москва, ул. Профсоюзная, 78', 'https://www.mos.ru',
     NULL, NULL, NULL, NULL),
    (3, 'Московский колледж дизайна', 'private',
     'Дизайн, реклама — 60 мест',
     'Центральный', 'Москва, ул. Тверская, 12', 'https://example.com',
     'от 180 000 ₽/год', TRUE, NULL, NULL),
    (4, 'Колледж пожарной безопасности', 'federal',
     'Пожарная безопасность — 40 мест',
     'Южный', 'Москва, Варшавское ш., 125', 'https://example.com',
     NULL, NULL, 'Медицинская справка, нормативы ГТО', TRUE)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    type = EXCLUDED.type,
    short_description = EXCLUDED.short_description,
    district = EXCLUDED.district,
    address = EXCLUDED.address,
    website_url = EXCLUDED.website_url,
    tuition_cost = EXCLUDED.tuition_cost,
    installment_available = EXCLUDED.installment_available,
    special_admission_conditions = EXCLUDED.special_admission_conditions,
    has_dormitory = EXCLUDED.has_dormitory;

INSERT INTO specialties (
    id, college_id, name, budget_places, paid_places, passing_score, admission_info
) VALUES
    (101, 1, 'Информационные системы и программирование', 25, 10, 4,
     'Приём по результатам ОГЭ и внутренних испытаний.'),
    (102, 1, 'Сетевое и системное администрирование', 20, 5, 4, NULL),
    (201, 2, 'Техническая эксплуатация летательных аппаратов', 15, 0, 5,
     'Требуется портфолио.'),
    (301, 3, 'Дизайн (по отраслям)', 0, 30, NULL,
     'Творческое испытание обязательно.'),
    (401, 4, 'Пожарная безопасность', 30, 0, 4,
     'Общежитие предоставляется иногородним.')
ON CONFLICT (id) DO UPDATE SET
    college_id = EXCLUDED.college_id,
    name = EXCLUDED.name,
    budget_places = EXCLUDED.budget_places,
    paid_places = EXCLUDED.paid_places,
    passing_score = EXCLUDED.passing_score,
    admission_info = EXCLUDED.admission_info;

-- ---------------------------------------------------------------------------
-- Seed: documents (mock_documents.json)
-- ---------------------------------------------------------------------------

INSERT INTO documents (id, title, content, pdf_url, link_url, sort_order) VALUES
    (1, 'Информация о поступлении',
     'Приём документов в колледжи Москвы проводится в электронной форме через mos.ru и лично в приёмных комиссиях. Следите за сроками на сайте выбранного колледжа.',
     NULL, 'https://www.mos.ru', 1),
    (2, 'Перечень документов',
     E'• Заявление\n• Паспорт (или свидетельство о рождении)\n• Аттестат или справка об обучении\n• Фотографии 3×4\n• Медицинская справка (при необходимости)',
     NULL, NULL, 2),
    (3, 'Сроки подачи',
     'Начало приёма — обычно с 20 июня. Зачисление на бюджет — поэтапно до августа. Уточняйте даты в колледже.',
     NULL, NULL, 3),
    (4, 'Этапы поступления',
     E'1. Подача заявления\n2. Вступительные испытания (если предусмотрены)\n3. Ранжирование списков\n4. Зачисление и заключение договора',
     NULL, NULL, 4)
ON CONFLICT (id) DO UPDATE SET
    title = EXCLUDED.title,
    content = EXCLUDED.content,
    pdf_url = EXCLUDED.pdf_url,
    link_url = EXCLUDED.link_url,
    sort_order = EXCLUDED.sort_order;

-- ---------------------------------------------------------------------------
-- Seed: FAQ (mock_faq.json) — API отдаёт ключ "items"
-- ---------------------------------------------------------------------------

INSERT INTO faq_items (id, question, answer, sort_order) VALUES
    (1, 'Нужна ли регистрация в приложении?',
     'Нет. На первом этапе приложение работает без авторизации. Избранное сохраняется только на вашем устройстве.',
     1),
    (2, 'Откуда берутся данные о колледжах?',
     'Информация загружается с сервера «Навигатора». При отсутствии сети показываются сохранённые на устройстве данные.',
     2),
    (3, 'Чем отличаются типы колледжей?',
     'Государственные — базовый набор полей. У частных отображается стоимость и рассрочка. У федеральных — особые условия поступления и наличие общежития.',
     3),
    (4, 'Как добавить колледж в избранное?',
     'Нажмите на иконку сердца в списке колледжей или на экране детальной информации.',
     4)
ON CONFLICT (id) DO UPDATE SET
    question = EXCLUDED.question,
    answer = EXCLUDED.answer,
    sort_order = EXCLUDED.sort_order;

COMMIT;

-- ---------------------------------------------------------------------------
-- Проверка
-- ---------------------------------------------------------------------------

-- SELECT type, COUNT(*) FROM colleges GROUP BY type;
-- SELECT c.name, COUNT(s.id) FROM colleges c LEFT JOIN specialties s ON s.college_id = c.id GROUP BY c.id, c.name;
