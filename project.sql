-- CS 166 Project: Ray Wong, Jade Than

-- Car mechanic ER Model

DROP TABLE IF EXISTS Customers, Mechanics, Cars, ServiceRequests;

CREATE TABLE Customers (phone numeric (10, 0), firstName text, lastName text, homeAddress text, 
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

CREATE TABLE ServiceRequests (odometer numeric (10, 0), dateIn text, dateOut text, comments text, bill numeric (9, 0), isOpen boolean,
  VIN numeric (9, 0),
  PRIMARY KEY (VIN, dateIn),
  FOREIGN KEY (VIN)
    REFERENCES Cars
    ON DELETE SET NULL
);

\dt