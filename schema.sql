-- ============================================================
-- schema.sql
-- Таблиця для збереження всіх типів працівників
-- ============================================================

CREATE TABLE IF NOT EXISTS employees (
    id               SERIAL          PRIMARY KEY,
    type             VARCHAR(30)     NOT NULL,           -- дискримінатор: Employee / FullTimeEmployee / ...

    -- Загальні поля базового класу Employee
    name             VARCHAR(255)    NOT NULL,
    age              INTEGER         NOT NULL,
    salary           NUMERIC(12, 2)  NOT NULL,
    experience       INTEGER         NOT NULL,
    position         VARCHAR(50)     NOT NULL,
    department       VARCHAR(50)     NOT NULL,

    -- Кількість таких працівників (з Company.EmployeeRecord)
    quantity         INTEGER         NOT NULL DEFAULT 1,

    -- FullTimeEmployee
    bonus_percentage NUMERIC(5, 2),                      -- % премії (nullable)

    -- ContractEmployee
    contract_months  INTEGER,                            -- тривалість контракту (nullable)

    -- RemoteEmployee
    country          VARCHAR(100),                       -- країна проживання (nullable)
    hourly_rate      NUMERIC(10, 2),                     -- погодинна ставка (nullable)

    -- InternEmployee
    university       VARCHAR(255),                       -- університет (nullable)
    intern_duration  INTEGER                             -- тривалість стажування в місяцях (nullable)
);
