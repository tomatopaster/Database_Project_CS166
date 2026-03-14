DROP INDEX IF EXISTS SRbyVIN, CarbyPhone;

-- Helps with queries that group Service Requests by car VIN like for max odometer for a given car
CREATE INDEX SRbyVIN
ON ServiceRequests (VIN);

-- Helps with queries that group cars by customer like for checking the total bill for cars under a given customer
CREATE INDEX CarbyPhone
ON Cars (phone);

