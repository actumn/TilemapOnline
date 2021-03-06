CREATE  TABLE if not EXISTS USERS (
  ID INT IDENTITY PRIMARY KEY,
  USER_ID VARCHAR UNIQUE,
  USER_PW VARCHAR,
  USER_NAME VARCHAR UNIQUE,
  LEVEL INT DEFAULT 1,
  JOB_ID INT,
  MAP_ID INT DEFAULT 1,
  X INT DEFAULT 100,
  Y INT DEFAULT 100,
  Current_Exp INT DEFAULT 0,
  LAST_LOGIN DATETIME
);

CREATE  TABLE if not EXISTS EXPS (
  LEVEL INT UNIQUE NOT NULL,
  EXP INT NOT NULL
);