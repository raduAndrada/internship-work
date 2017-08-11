drop table BATCH if exists; 
create table BATCH (
	BATCH_ID integer identity primary key,
);

drop table RESULT_METRICS if exists;

create table RESULT_METRICS (
  RESULT_METRICS_ID integer identity primary key,
  ODD_TO_EVEN_RATIO integer null,
  PASSED_REG_CHANGE_DUE_DATE int null ,
  BATCH_ID int not null,
  RESULT_PROCESS_TIME datetime
  );

create index RESULT_METRICS_ID_IDX on RESULT_METRICS(BATCH_ID asc);
alter table RESULT_METRICS add constraint RESULT_METRICS_BATCH_ID_FK
    foreign key (BATCH_ID)
    references BATCH (BATCH_ID);  
  
  
drop table RESULT_ERROR if exists;

create table RESULT_ERROR (
  RESULT_ERROR_ID integer identity primary key,
  TYPE integer null,
  VEHICLE_OWNER_ID integer null,
  RESULT_METRICS_ID integer not null);

 
create index RESULT_ERROR_METRICS_ID_idx on RESULT_ERROR(RESULT_METRICS_ID asc);
 
 alter table RESULT_ERROR add constraint result_error_METRICS_fk
    foreign key (RESULT_METRICS_ID)
    references RESULT_METRICS (RESULT_METRICS_ID);

drop table RESULT_UNREG_CARS_COUNT_BY_JUD if exists;

create table RESULT_UNREG_CARS_COUNT_BY_JUD (
  RESULT_UNREG_CARS_COUNT_ID integer identity primary key,
  JUDET varchar(2) null,
  UNREG_CARS_COUNT integer null,
  RESULT_METRICS_ID integer not null
  );
  
create INDEX RESULT_UNREG_CARS_COUNT_BY_JUD_ID_IDX on RESULT_UNREG_CARS_COUNT_BY_JUD(RESULT_METRICS_ID asc);
 
 alter table RESULT_UNREG_CARS_COUNT_BY_JUD add constraint RESULT_UNREG_CARS_COUNT_BY_JUD_ID_IDX
    foreign key (RESULT_METRICS_ID)
    references RESULT_METRICS (RESULT_METRICS_ID);
   

drop table VEHICLE_OWNER if exists;

create table VEHICLE_OWNER (
  VEHICLE_OWNER_ID integer identity primary key,
  RO_ID_CARD varchar(20) null,
  REG_PLATE varchar(20) null,
  ISSUE_DATE date null,
  COMENTARIU varchar(20) null,
  BATCH_ID integer not null
  );

create index VEHICLE_OWNER_ID_IDX on VEHICLE_OWNER(BATCH_ID asc);
alter table VEHICLE_OWNER add constraint VEHICLE_OWNER_ID_IDX
    foreign key (BATCH_ID)
    references BATCH (BATCH_ID); 


