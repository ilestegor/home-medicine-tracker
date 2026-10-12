--liquibase formatted sql

--changeset ilestegor:005-create-indexes
--comment: Индексы по внешним ключам и под рабочие выборки.
-- PostgreSQL создаёт индексы под PRIMARY KEY и UNIQUE, но под FOREIGN KEY — нет,
-- а джойны вверх по дереву идут именно по ним.
create index idx_stock_item_medicine_id      on stock_item (medicine_id);
create index idx_regimen_medicine_id         on regimen (medicine_id);
create index idx_intake_record_stock_item_id on intake_record (stock_item_id);
create index idx_intake_record_regimen_id    on intake_record (regimen_id);

-- История приёмов всегда читается от свежих к старым
create index idx_intake_record_taken_at on intake_record (taken_at desc);

create index idx_stock_item_active_expires_at
    on stock_item (expires_at)
    where status = 'ACTIVE';
--rollback drop index idx_stock_item_active_expires_at;
--rollback drop index idx_intake_record_taken_at;
--rollback drop index idx_intake_record_regimen_id;
--rollback drop index idx_intake_record_stock_item_id;
--rollback drop index idx_regimen_medicine_id;
--rollback drop index idx_stock_item_medicine_id;
