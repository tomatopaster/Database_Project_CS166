-- List date, comment, and bill for all closed requests with bill lower than 100
-- List the customers that have paid less than 100 dollars for repairs based on their previous service requests.
SELECT CU.firstName, CU.lastName, SR.dateIn, SR.dateOut, SR.comments, SR.bill
FROM Customers CU, Cars C, ServiceRequests SR
WHERE CU.phone = C.phone AND C.VIN = SR.VIN AND SR.bill < 100;

-- List first and last name of customers having more than 20 different cars
-- Find how many cars each customer has counting from the ownership relation and discover who has more than 20 cars.
SELECT CU.firstName, CU.lastName, COUNT(C.*) AS carCount
FROM Customers CU, Cars C
WHERE CU.phone = C.phone
GROUP BY CU.phone
HAVING COUNT(C.*) > 20;

-- List Make, Model, and Year of all cars build before 1995 having less than 50000 miles
-- Get the odometer from the service_requests and find all cars before 1995 having less than 50000 miles in the odometer.
SELECT C.make, C.model, MAX(SR.odometer) AS odometerMiles
FROM Cars C, ServiceRequests SR
WHERE C.VIN = SR.VIN AND C.carYear < 1995
GROUP BY C.VIN
HAVING MAX(SR.odometer) < 50000;

-- List the make, model and number of service requests for the first k cars with the highest number of service orders.
-- Find for all cars in the database the number of service requests. Return the make, model and number of service requests for the cars having the k highest number of service requests. The k value should be positive and larger than 0. The user should provide this value. Focus on the open service requests.
-- Replace 5 with your own variable _________________________________-
SELECT C.make, C.model, COUNT(SR.*) AS serviceRequestCount
FROM Cars C, ServiceRequests SR
WHERE C.VIN = SR.VIN
GROUP BY C.VIN
ORDER BY COUNT(SR.*) DESC
LIMIT 5;

-- List the first name, last name and total bill of customers in descending order of their total bill for all cars brought to the mechanic.
-- For all service requests find the aggregate cost per customer and order customers according to that cost. List their first, last name and total bill
SELECT CU.firstName, CU.lastName, SUM(SR.bill) AS totalBill
FROM Customers CU, Cars C, ServiceRequests SR
WHERE CU.phone = C.phone AND C.VIN = SR.VIN
GROUP BY CU.phone
ORDER BY SUM(SR.bill) DESC;