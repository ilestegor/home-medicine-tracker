--liquibase formatted sql

--changeset ilestegor:006-move-unit-to-medicine
--comment: Единица измерения — свойство препарата, а не упаковки и не схемы приёма.
-- «Нурофен таблетки» считается в штуках всегда, сколько бы пачек ни лежало. Пока unit
-- жил и в stock_item, и в regimen.dose_unit, модель допускала, что одна пачка того же
-- препарата в шт, а другая в мл — и списание вычитало таблетку из миллилитров.
-- Сверку единиц в сервисе приёма это делает ненужной: рассинхронизировать негде.
alter table medicine
    add column unit varchar(20);

-- Бэкфилл: берём единицу у любой упаковки препарата — до сих пор они и не могли
-- различаться осмысленно
update medicine m
set unit = (select s.unit
            from stock_item s
            where s.medicine_id = m.id
            limit 1);

-- У препаратов без упаковок взять неоткуда
update medicine
set unit = 'шт'
where unit is null;

alter table medicine
    alter column unit set not null;

alter table stock_item
    drop column unit;

alter table regimen
    drop column dose_unit;

comment on column medicine.unit is 'В чём измеряется препарат: шт, мл, г. Одна на все упаковки и схемы';
--rollback alter table stock_item add column unit varchar(20);
--rollback alter table regimen add column dose_unit varchar(20);
--rollback update stock_item s set unit = (select m.unit from medicine m where m.id = s.medicine_id);
--rollback update regimen r set dose_unit = (select m.unit from medicine m where m.id = r.medicine_id);
--rollback alter table stock_item alter column unit set not null;
--rollback alter table regimen alter column dose_unit set not null;
--rollback alter table medicine drop column unit;
