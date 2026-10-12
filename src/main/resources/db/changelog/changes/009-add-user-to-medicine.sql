--liquibase formatted sql

--changeset ilestegor:009-add-user-to-medicine
--comment: Владелец справочника. Упаковки, курсы и приёмы наследуют владение через medicine.
alter table medicine
    add column user_id bigint;

-- Существующим строкам нужен владелец: заглушка с невалидным хэшем, войти под ней нельзя.
insert into app_user (email, password_hash, display_name)
select 'legacy@local', 'x', 'Прежние данные'
where exists (select 1 from medicine);

update medicine
set user_id = (select id from app_user where email = 'legacy@local')
where user_id is null;

-- RESTRICT, а не CASCADE: удаление аккаунта потребует сознательной чистки данных.
alter table medicine
    alter column user_id set not null,
    add constraint fk_medicine_user foreign key (user_id)
        references app_user (id) on delete restrict;

-- Один и тот же «Нурофен» должен уметь существовать у разных людей.
-- Индекс этого ограничения начинается с user_id, поэтому отдельный под FK не нужен.
alter table medicine
    drop constraint uq_medicine_name_dosage,
    add constraint uq_medicine_user_name_dosage
        unique nulls not distinct (user_id, name, dosage);
--rollback alter table medicine drop constraint uq_medicine_user_name_dosage, add constraint uq_medicine_name_dosage unique nulls not distinct (name, dosage);
--rollback alter table medicine drop constraint fk_medicine_user;
--rollback alter table medicine drop column user_id;
--rollback delete from app_user where email = 'legacy@local';
