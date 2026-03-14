/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class EmbeddedSQL {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of EmbeddedSQL
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public EmbeddedSQL (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end EmbeddedSQL

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            EmbeddedSQL.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      EmbeddedSQL esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the EmbeddedSQL object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new EmbeddedSQL (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("0. ADD Customer");
            System.out.println("1. ADD Car");
            System.out.println("2. ADD Mechanic");
            System.out.println("3. OPEN Service Request");
            System.out.println("4. CLOSE Service Request");
            System.out.println("5. List date, comment, and bill for all closed requests with bill lower than 100");
            System.out.println("6. List first and last name of customers having more than 20 different cars");
            System.out.println("7. List Make, Model, and Year of all cars build before 1995 having less than 50000 miles");
            System.out.println("8. List the make, model and number of service requests for the first k cars with the highest number of service orders");
            System.out.println("9. List the first name, last name and total bill of customers in descending order of their total bill for all cars brought to the mechanic");
            System.out.println("10. < QUIT MENU");

            switch (readChoice()){
               case 0: QueryExample(esql); break;
               case 1: Query1(esql); break;
               case 2: Query2(esql); break;
               case 3: Query3(esql); break;
               case 4: Query4(esql); break;
               case 5: Query5(esql); break;
               case 6: Query6(esql); break;
               case 7: Query7(esql); break;
               case 8: Query8(esql); break;
               case 9: Query9(esql); break;
               case 10: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
   
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   public static long QueryExample(EmbeddedSQL esql){
   long number = 0;
      try{
         String first = "";
         String last = "";
         String address = "";
         while("".equals(first)){
            System.out.println("Enter Customer first name: ");
            first = in.readLine();
            if ("".equals(first)){
               System.out.println("Please submit a nonempty first name.");
            }
         }
         while("".equals(last)){
            System.out.println("Enter Customer last name: ");
            last = in.readLine();
            if ("".equals(last)){
               System.out.println("Please submit a nonempty last name.");
            }
         }
         while(number < 1000000000L || number > 9999999999L){
            System.out.println("Enter Customer phone number: ");
            number = Long.parseLong(in.readLine());
            if (number < 1000000000L || number > 9999999999L){
               System.out.println("Please submit a valid number within bounds of 10 digits.");
            }
         }
         while("".equals(address)){
            System.out.println("Enter Customer address: ");
            address = in.readLine();
            if ("".equals(address)){
               System.out.println("Please submit a nonempty address.");
            }
         }

         esql.executeUpdate("INSERT INTO Customers (firstName, lastName, phone, homeAddress) VALUES (\'"+ first + "\', \'" + last + "\', \'" +  number + "\', \'" + address + "\');");

      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   return number;
   }//end QueryExample           esql.executeQuery("SELECT C.* FROM Customers C WHERE C.phone = " + number + ";") == 1
   
   public static void Query1(EmbeddedSQL esql){
      try{
         long ID = 0;
         String first = "";
         String last = "";
         int years = 0;
         while(ID < 100000000L || ID > 999999999L){
            System.out.println("Enter Mechanic ID: ");
            ID = Long.parseLong(in.readLine());
            if (ID < 100000000L || ID > 999999999L){
               System.out.println("Please submit a valid Mechanic ID within bounds of 9 digits.");
            }
         }
         while("".equals(first)){
            System.out.println("Enter Mechanic first name: ");
            first = in.readLine();
            if ("".equals(first)){
               System.out.println("Please submit a nonempty first name.");
            }
         }
         while("".equals(last)){
            System.out.println("Enter Mechanic last name: ");
            last = in.readLine();
            if ("".equals(last)){
               System.out.println("Please submit a nonempty last name.");
            }
         }
         while(years < 0 || years > 99){
            System.out.println("Enter Mechanic years experience: ");
            years = Integer.parseInt(in.readLine());
            if (years < 0 || years > 99){
               System.out.println("Please submit a valid years experience between 0 and 100 years.");
            }
         }

         esql.executeUpdate("INSERT INTO Mechanics (ID, firstName, lastName, yearsExp) VALUES (\'"+ ID + "\', \'" + first + "\', \'" +  last + "\', \'" + years + "\');");
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query1

   public static long Query2(EmbeddedSQL esql){
   long VIN = 0;
      try{
         int carYear = 0;
         String make = "";
         String model = "";
         long number = 0;
         boolean isValid = false;
         while(VIN < 100000000L || VIN > 999999999L){
            System.out.println("Enter Car VIN: ");
            VIN = Long.parseLong(in.readLine());
            if (VIN < 100000000L || VIN > 999999999L){
               System.out.println("Please submit a valid Vehicle ID within bounds of 9 digits.");
            }
         }
         while(carYear < 1800 || carYear > 2030){
            System.out.println("Enter Car year: ");
            carYear = Integer.parseInt(in.readLine());
            if (carYear < 1800 || carYear > 2030){
               System.out.println("Please submit a valid car year between 1800 and 2030.");
            }
         }
         while("".equals(make)){
            System.out.println("Enter Car make: ");
            make = in.readLine();
            if ("".equals(make)){
               System.out.println("Please submit a nonempty make.");
            }
         }
         while("".equals(model)){
            System.out.println("Enter Car model: ");
            model = in.readLine();
            if ("".equals(model)){
               System.out.println("Please submit a nonempty model.");
            }
         }
         
         while(number < 1000000000L || number > 9999999999L || !isValid){
            System.out.println("Enter Customer number: ");
            number = Long.parseLong(in.readLine());
            if (number < 1000000000L || number > 9999999999L){
               System.out.println("Please submit a valid Vehicle ID within bounds of 9 digits.");
            }
            else{
               System.out.println("Number matches ...");
               if(esql.executeQuery("SELECT C.* FROM Customers C WHERE C.phone = " + number + ";") == 1){
                  isValid = true;
               }
               else{
                  System.out.println("None. Please submit a Customer number in the database.");
               }
            }
         }

         esql.executeUpdate("INSERT INTO Cars (VIN, carYear, make, model, phone) VALUES (\'"+ VIN + "\', \'" + carYear + "\', \'" +  make + "\', \'" + model + "\', \'" + number + "\');");

      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   return VIN;
   }//end Query2

         //    while(VIN < 100000000L || VIN > 999999999L || !isValid){
         //    System.out.println("Enter Car VIN: ");
         //    VIN = Long.parseLong(in.readLine());
         //    if (VIN < 100000000L || VIN > 999999999L){
         //       System.out.println("Please submit a valid Vehicle ID within bounds of 9 digits.");
         //    }
         //    else{
         //       System.out.println("VIN matches ...");
         //       if(esql.executeQuery("SELECT C.* FROM Cars C WHERE C.VIN = " + VIN + ";") == 1){
         //          isValid = true;
         //       }
         //       else{
         //          System.out.println("None. Please submit a Vehicle ID in the database.");
         //       }
         //    }
         // }
         // isValid = false;

   // This function will allow you to add a service request for a customer into the database. 
   // Given a last name, the function should search the database of existing customers. 

   // If many customers match, a menu option should appear listing all customers with the given last name asking the user to choose which customer has initiated the service request. 
   
         // Otherwise, the client application should provide the option of adding a new customer. 
   
         // If an existing customer is chosen, the client application should list all cars associated with that client 
   // providing the option to initiate the service request for one of the listed cars, otherwise a new car should be added along with the service request information for it.
   public static void Query3(EmbeddedSQL esql){
      try{
         System.out.println("Enter Customer last name: ");
         String last = in.readLine();

         //find customer 
         int customersFound = esql.executeQuery("SELECT lastName FROM Customers WHERE lastName = \'" + last + "\'");
         long customerPhone = -1;

         // if no customer last name found, provide option of adding a new customer
         if(customersFound < 1){
            boolean addNewCustomer = true;
            while(addNewCustomer){
               System.out.println("Would you like to add another customer? Y or N");

               switch(in.readLine()){
                  case "Y":
                     addNewCustomer = false;
                     customerPhone = QueryExample(esql);
                     break;
                  case "N":
                     addNewCustomer = false;
                     System.out.println("No new customer added!");
                     break;
                  default: 
                     System.out.println("Try again!");
               }
            }
         }else {
            
            if(customersFound > 1){
               //list customers with same last name 
               String query = "SELECT firstName, lastName, phone FROM Customers WHERE lastName = \'" + last + "\'";

               int rowCount = esql.executeQuery(query);
               System.out.println ("total row(s): " + rowCount);
               
               //TODO: choose which customer for service request by updating customerPhone 
               //System.out.println("Which customer is making the service request? Answer with phone number ");
            }else{
               //TODO: phone number for customer with only last name 
               //customerPhone = 

            }
         }

         //car business 
         long carVIN = -1;

         if(customerPhone != -1){
            int carsFound = esql.executeQuery("SELECT carYear, make, model FROM Cars WHERE phone = \'" + customerPhone + "\'");

            //if customer has no cars registered
            if(carsFound < 1){
               boolean addNewCar = true;
               while(addNewCar){
                  System.out.println("No cars found for customer. Add new Car? Y or N ");

                  switch(in.readLine()){
                     case "Y":
                        addNewCar = false;
                        carVIN = Query2(esql);
                        break;
                     case "N":
                        addNewCar = false;
                        System.out.println("No new car added!");
                        break;
                     default: 
                        System.out.println("Try again!");
                  }
               }
            }else {
               if(carsFound == 1){
                  //TODO: carVIN = 
               }else{
                  // list all cars associated with that client 
                  System.out.println("List of all cars associated with "+ last + ": ");
                  //TODO: print out all cars of customer 


                  //TODO: carVIN = 

               }
            }
         }

         //service request business 
         if(carVIN != -1){
            //initiate service request 
            //TODO: test this
            boolean addRequest = true;
            while(addRequest){
               System.out.println("Would you like to open a new service request for this car? Y or N");

               switch(in.readLine()){
                  case "Y":
                     addRequest = false;
                     System.out.println("Enter odometer reading: ");
                     long odometer = Long.parseLong(in.readLine());
                     System.out.println("Enter date in: ");
                     String dateIn = in.readLine();
                     System.out.println("Enter date out: ");
                     String dateOut = in.readLine();
                     System.out.println("Enter comments: ");
                     String comments = in.readLine();
                     System.out.println("Enter bill: ");
                     float bill = Float.parseFloat(in.readLine());
                     System.out.println("Enter if request is open (ture or false): "); //TODO: check input validation? 
                     boolean isOpen = Boolean.parseBoolean(in.readLine());
                     
                     //TODO: how to make optional parameters? 
                     esql.executeUpdate("INSERT INTO ServiceRequests (VIN, odometer, dateIn, dateOut, comments, bill, isOpen) VALUES (\'"+ carVIN + "\', \'" + odometer + "\', \'" +  dateIn + "\', \'" + dateOut + "\', \'" + comments + "\', \'" + bill + "\', \'" + isOpen + "\');");
                     break;
                  case "N":
                     addRequest = false;
                     System.out.println("No new request opened!");
                     break;
                  default: 
                     System.out.println("Try again!");
               }
            }
         }
         
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query3

   // This function will allow you to complete an existing service request. 
   // Given a service request number and an employee id, the client application should verify 
   // the information provided and attempt to create a closing request record. 
   // You should make sure to check for the validity of the provided inputs 
   // (i.e. does the mechanic exist, does the request exist, valid closing date after request date, etc.)
   public static void Query4(EmbeddedSQL esql){
      try{
         long ID = 0;
         boolean isValid = false;
         while (!isValid){
            System.out.println ("Input the ID of the mechanic with the Service Request you want to close:");
            ID = Long.parseLong(in.readLine());

            System.out.println ("Mechanic ID matches...");
            if (esql.executeQuery("SELECT M.firstName, M.lastName, M.ID FROM Mechanics M WHERE M.ID = " + ID +";") == 1){
               System.out.println ("With the VIN...");
               if(esql.executeQuery("SELECT C.carYear, C.Model, C.Make, M.VIN FROM Mechanics M, Cars C WHERE M.ID = " + ID +" AND C.VIN = M.VIN;") == 1){
                  isValid = true;
               }
               else{
                  System.out.println ("This mechanic is not working with any vehicles. Cannot close a Service Request.");
                  return;
               }
            }
            else{
               System.out.println ("None, please input again.");
            }
         }

         String dateOut = "";
         String comment = "";

         while(!isValid){
            System.out.println ("Enter closing date in the form mm/dd/yyyy:");
            dateOut = in.readLine();

            isValid = true;

            if(Integer.parseInt(dateOut.substring(0, 2)) > 12 || Integer.parseInt(dateOut.substring(0, 2)) < 1){
               System.out.println ("Enter a valid month.");
            }
            if(Integer.parseInt(dateOut.substring(3, 5)) > 31 || Integer.parseInt(dateOut.substring(3, 5)) < 1){
               System.out.println ("Enter a valid day.");
            }
            if(Integer.parseInt(dateOut.substring(6, 10)) > 2030 || Integer.parseInt(dateOut.substring(6, 10)) < 1){
               System.out.println ("Enter a valid day.");
            }
         }

         System.out.println ("Enter a comment (or not if you want):");
         comment = in.readLine();
         if(comment.equals("")){
            comment = "No comment";
         }

         esql.executeUpdate("UPDATE ServiceRequests SET dateOut = \'" + dateOut + "\', comments = \'" + comment +"\', isOpen = false WHERE VIN = (SELECT M.VIN FROM Mechanics M WHERE M.ID = " + ID +") AND isOpen = true;");
         esql.executeUpdate("UPDATE Mechanics SET VIN = NULL WHERE ID = " + ID +";");

      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query4

   public static void Query5(EmbeddedSQL esql){
      try{
         String query = "SELECT P.pname FROM Catalog C, parts P WHERE C.pid = P.pid AND cost < ";
         System.out.print("\tEnter cost: $");
         String input = in.readLine();
         query += input;

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query5

   public static void Query6(EmbeddedSQL esql){
      try{
         String query = "SELECT S.address FROM suppliers S, Catalog C, parts P WHERE S.sid = C.sid AND C.pid = P.pid AND P.pname = '";
         System.out.print("\tEnter Part Name: ");
         String input = in.readLine();
         query += input + "'";

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query6

   public static void Query7(EmbeddedSQL esql){
      try{
         String query = "SELECT S.sname, COUNT(C.pid) AS numParts FROM suppliers S, catalog C WHERE S.sid = C.sid GROUP BY S.sid";

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query7

   public static void Query8(EmbeddedSQL esql){
      try{
         String query = "SELECT S.sname, COUNT(C.pid) AS numParts FROM suppliers S, catalog C WHERE S.sid = C.sid GROUP BY S.sid";

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query8

   public static void Query9(EmbeddedSQL esql){
      try{
         String query = "SELECT S.sname, COUNT(C.pid) AS numParts FROM suppliers S, catalog C WHERE S.sid = C.sid GROUP BY S.sid";

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query9

}//end EmbeddedSQL

/*


    Add Mechanic

Add a new mechanic into the database. You should provide an interface that takes as input the information of a new mechanic (i.e. first, last name, specialty, experience) and checks if the provided information is valid based on the constraints of the database schema.

    Add Car

This function should allow for adding a new car into the database. You should provide an interface that takes as input the information of a new car (i.e. vin, make, model, year) checking if the provided information are valid based on the constrains of the database schema.

    Initiate a Service Request

This function will allow you to add a service request for a customer into the database. Given a last name, the function should search the database of existing customers. If many customers match, a menu option should appear listing all customers with the given last name asking the user to choose which customer has initiated the service request. Otherwise, the client application should provide the option of adding a new customer. If an existing customer is chosen, the client application should list all cars associated with that client providing the option to initiate the service request for one of the listed cars, otherwise a new car should be added along with the service request information for it.

    Close A Service Request

This function will allow you to complete an existing service request. Given a service request number and an employee id, the client application should verify the information provided and attempt to create a closing request record. You should make sure to check for the validity of the provided inputs (i.e. does the mechanic exist, does the request exist, valid closing date after request date, etc.)

    List date, comment, and bill for all clos
*/

   /**
    * Add a new customer into the database. 
    * You should provide an interface that takes as input the information of a new customer 
    * (i.e. first, last name, phone, address) 
    * and checks if the provided information are valid based on the constraints 
    * of the database schema
    *
    * @param customer information 
    */
   // public void addCustomer (String first, String last, int phone, String address) {
   //    //check if valid inputs 
   //    executeUpdate("INSERT INTO Customer VALUES (" + first + ", " + last + ", " + phone + ", " + address + ")");
   // }