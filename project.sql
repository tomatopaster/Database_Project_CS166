-- CS 166 Project: Ray Wong, Jade Than

-- Car mechanic ER Model

DROP TABLE IF EXISTS Customers, Mechanics, Cars, ServiceRequests;

CREATE TABLE Customers (phone numeric (10,0), firstName text, lastName text, homeAddress text, 
  PRIMARY KEY (phone)
);

CREATE TABLE Cars (VIN numeric (9,0), carYear numeric (4,0), make text, model text,
  PRIMARY KEY (VIN),
  phone numeric (10, 0),  -- Owns
  FOREIGN KEY (phone)
    REFERENCES Customers
    ON DELETE SET NULL
);

CREATE TABLE Mechanics (ID numeric (9, 0), firstName text, lastName text, yearsExp numeric (3, 0),
  PRIMARY KEY (ID),
  VIN numeric (9, 0),  -- Works On
  FOREIGN KEY (VIN)
    REFERENCES Cars
    ON DELETE SET NULL
);

CREATE TABLE ServiceRequests (odometer numeric (10, 0), dateIn text, dateOut text, comments text, bill numeric (9, 2), isOpen boolean,
  VIN numeric (9, 0),
  PRIMARY KEY (VIN, dateIn),
  FOREIGN KEY (VIN)
    REFERENCES Cars
    ON DELETE SET NULL
);

COPY Customers(phone, firstName, lastName, homeAddress)
FROM '/home/csmajs/athan016/Projects/Database_Project_CS166/data/customers.csv'
WITH DELIMITER ',';

COPY Cars(VIN, phone, carYear, make, model)
FROM '/home/csmajs/athan016/Projects/Database_Project_CS166/data/cars.csv'
WITH DELIMITER ',';

COPY Mechanics(VIN, ID, firstName, lastName, yearsExp)
FROM '/home/csmajs/athan016/Projects/Database_Project_CS166/data/mechanics.csv'
WITH (DELIMITER ',', NULL '');

COPY ServiceRequests(VIN, odometer, dateIn, dateOut, comments, bill, isOpen)
FROM '/home/csmajs/athan016/Projects/Database_Project_CS166/data/serviceRequests.csv'
WITH (DELIMITER ',', NULL '');

\dt