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
 
  
  
drop table RESULT_ERROR if exists;

create table RESULT_ERROR (
  RESULT_ERROR_ID integer identity primary key,
  TYPE integer null,
  VEHICLE_OWNER_ID integer null,
  RESULT_METRICS_ID integer not null);


drop table RESULT_UNREG_CARS_COUNT_BY_JUD if exists;

create table RESULT_UNREG_CARS_COUNT_BY_JUD (
  RESULT_UNREG_CARS_COUNT_ID integer identity primary key,
  JUDET varchar(2) null,
  UNREG_CARS_COUNT integer null,
  RESULT_METRICS_ID integer not null
  );

drop table VEHICLE_OWNER if exists;

create table VEHICLE_OWNER (
  VEHICLE_OWNER_ID numeric identity primary key,
  RO_ID_CARD varchar(20) null,
  REG_PLATE varchar(20) null,
  ISSUE_DATE timestamp null,
  COMENTARIU varchar(20) null,
  BATCH_ID integer not null
  );




