--liquibase formatted sql

--changeset ilestegor:007-stock-item-optimistic-lock
--comment: Защита от потерянного обновления остатка. Два одновременных приёма из одной
-- упаковки оба прочитали бы остаток 10 и оба записали 9 вместо 8. Hibernate добавит
-- version в WHERE каждого UPDATE — второй не найдёт строку и получит
-- OptimisticLockingFailureException вместо молчаливой порчи остатка.
alter table stock_item
    add column version bigint not null default 0;
--rollback alter table stock_item drop column version;
