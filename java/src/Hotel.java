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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;//added this
import java.lang.Math;
import java.util.HashMap;
import java.sql.Timestamp; //added this
import java.util.Date; //added this
import java.text.SimpleDateFormat; //added this
import java.time.LocalDate; //added this

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Hotel {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Hotel 
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {

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
   }//end Hotel

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public static double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }

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
   public int executeQueryAndPrintResult (String query) throws SQLException {
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
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
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

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
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

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement ();

      ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   public int getNewUserID(String sql) throws SQLException {
      Statement stmt = this._connection.createStatement ();
      ResultSet rs = stmt.executeQuery (sql);
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }
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

   static String currentUserID = ""; //added this
   static String currentUserType = ""; //added this

   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Hotel.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Hotel esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Hotel object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Hotel (dbname, dbport, user, "");

         boolean keepon = true;

	System.out.println();
	System.out.print("Welcome to the MGM Hotel Chain Application.\n");
	System.out.print("To start, please choose one of the following options in our MAIN MENU.\n");
	System.out.println();

         while(keepon) {
            // These are sample SQL statements
            System.out.print("******************************************************************************\n\n");
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Are you a New User? Create a New User profile to access our application.");
            System.out.println("2. Are you an Existing User? Log in using this option.");
            System.out.println("3. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 3: keepon = false; break;
               default : System.out.println("\nUnrecognized choice! Please try again.\n"); break;
            }//end switch
            if (authorisedUser != null) {
		
	      if(currentUserType.trim().equals("Customer")){
              	boolean usermenu = true;
              	while(usermenu) {
			System.out.print("******************************************************************************\n\n");
                	System.out.println("CUSTOMER MAIN MENU");
                	System.out.println("-------------------");
                	System.out.println("1. View Hotels within 30 units");
                	System.out.println("2. View Rooms");
                	System.out.println("3. Book a Room");
                	System.out.println("4. View recent booking history");
			System.out.println(".........................");
			System.out.println("5. Log out");

			switch (readChoice()){
                   	 case 1: viewHotels(esql); break;
                   	 case 2: viewRooms(esql); break;
                   	 case 3: bookRooms(esql); break;
                   	 case 4: viewRecentBookingsfromCustomer(esql); break;
	           	 case 5: usermenu = false; break;
		   	 default : System.out.println("\nUnrecognized choice! Please try again!\n"); break;
			}	 
		}

		System.out.println();
		System.out.print("Logging you out...\n\n");
		System.out.print("Success.\n\n");
	      }
	      else{
		boolean usermenu = true;
		
		while(usermenu){

                	//the following functionalities basically used by managers
                	System.out.print("******************************************************************************\n\n");
                        System.out.println("MANAGER MAIN MENU");
                        System.out.println("------------------");
			System.out.println("1. View Hotels within 30 units");
                        System.out.println("2. View Rooms");
                        System.out.println("3. Book a Room");
                        System.out.println("4. View recent booking history");
                	System.out.println("5. Update Room Information");
                	System.out.println("6. View 5 recent Room Updates Info");
                	System.out.println("7. View booking history of the hotel");
                	System.out.println("8. View 5 regular Customers");
                	System.out.println("9. Place room repair Request to a company");
                	System.out.println("10. View room repair Requests history");

                	System.out.println(".........................");
                	System.out.println("11. Log out");
                	switch (readChoice()){
			 case 1: viewHotels(esql); break;
                         case 2: viewRooms(esql); break;
                         case 3: bookRooms(esql); break;
                         case 4: viewRecentBookingsfromCustomer(esql); break;
                   	 case 5: updateRoomInfo(esql); break;
                   	 case 6: viewRecentUpdates(esql); break;
                  	 case 7: viewBookingHistoryofHotel(esql); break;
                   	 case 8: viewRegularCustomers(esql); break;
                   	 case 9: placeRoomRepairRequests(esql); break;
                   	 case 10: viewRoomRepairHistory(esql); break;
                   	 case 11: usermenu = false; break;
                   	 default : System.out.println("\nUnrecognized choice! Please try again!\n"); break;
                	}
	    	}

		System.out.println();
                System.out.print("Logging you out...\n\n");
                System.out.print("Success.\n\n");
	    }
	

		currentUserID = "";
		currentUserType = "";
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
	       System.out.println();
	       System.out.print("******************************************************************************\n\n");	
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nThank you for using the MGM Hotel Chain Application. Goodbye!\n");
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
	 System.out.println();
         System.out.print("Please type in the number of the option you would like to select: ");
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

   /*
    * Creates a new user
    **/
   public static void CreateUser(Hotel esql){
      try{
	 System.out.println();
	 System.out.print("******************************************************************************\n\n");
	 System.out.println("We will now help you create your New User profile.\n");
         System.out.print("\tPlease enter your full name: ");
         String name = in.readLine();
         System.out.print("\tPlease enter a password for your profile: ");
         String password = in.readLine(); 
         String type="Customer";
			String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
         esql.executeUpdate(query);
	 System.out.println();
         System.out.println ("Your New User profile has been successfully created! Your userID is " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));
	 System.out.println("Please remember your userID and password, as you will need both to log in to our application.");
         System.out.println();

      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null if the user does not exist
    **/
   public static String LogIn(Hotel esql){
      try{
	 System.out.println();
	 System.out.print("******************************************************************************\n\n");
	 System.out.println("We will now help you log in to our application.\n");
         System.out.print("\tPlease enter your userID: ");
         String userID = in.readLine();
         System.out.print("\tPlease enter your password: ");
         String password = in.readLine();
	 System.out.println();

         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND password = '%s'", userID, password);
	 List<List<String>> typeUser = esql.executeQueryAndReturnResult(query);
         int userNum = esql.executeQuery(query);
         if (userNum > 0){
	    currentUserID = userID;
	    currentUserType = typeUser.get(0).get(3);
	    System.out.print("Logging you in...\n\n");
	    System.out.print("You have successfully logged in to our application!\n\n");	
            return userID;
	}

	System.out.print("Incorrect userID or password! Please try again.\n\n");
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewHotels(Hotel esql) {
	try{
		System.out.println();
		System.out.print("******************************************************************************\n\n");
		System.out.print("We will now give you a list of hotels that are within 30 units distance from your given input location.\n\n");
		System.out.print("\tPlease enter a positive value for the latitude of your input location: ");

		boolean checker = false;
		String latt = in.readLine();

		while(!checker){
			double lat1 = Double.parseDouble(latt);

			if(lat1 >= 0.0){

				checker = true;
			}
			else{
				System.out.println();
				System.out.print("Invalid input for latitude! Please type in a proper value for latitude: ");
				latt = in.readLine();
			}
			
		}

		double lat1 = Double.parseDouble(latt);
		checker = false;
		System.out.println();

		System.out.print("\tPlease enter a positive value for the longitude of your input location: ");
		String longg = in.readLine();

		while(!checker){

			double long1 = Double.parseDouble(longg);

			if(long1 >= 0.0){

				checker = true;
			}
			else{
				System.out.println();
                                System.out.print("Invalid input for longitude! Please type in a proper value for longitude: ");
                                longg = in.readLine();
			}
		}

		double long1 = Double.parseDouble(longg);
		System.out.println();

		String query = "SELECT * FROM Hotel";

		List<List<String>> retrieve = esql.executeQueryAndReturnResult(query);
		
		System.out.println();
		System.out.print("The following is a list of hotels that are within 30 units distance from your given input location.\n");
		System.out.println();

		for(int i = 0; i < retrieve.size(); i++){

			double lat2 = Double.parseDouble(retrieve.get(i).get(2));
			double long2 = Double.parseDouble(retrieve.get(i).get(3));

			
			double result = calculateDistance(lat1, long1, lat2, long2);

			if(result <= 30.0){
				System.out.print("Hotel Name: ");
				System.out.print(retrieve.get(i).get(1));
				System.out.print("\nHotel ID: ");
				System.out.print(retrieve.get(i).get(0));
				System.out.print("\nHotel Latitude: ");
				System.out.print(retrieve.get(i).get(2));
				System.out.print("\nHotel Longitude: ");
				System.out.print(retrieve.get(i).get(3).trim());
				System.out.print("\nDate Established: ");
				System.out.print(retrieve.get(i).get(4));
				System.out.println("\n");
			}
		}
	}

catch(Exception e){
	System.err.println(e.getMessage());
}
		
}
   public static void viewRooms(Hotel esql) {
	try{
		System.out.println();
                System.out.print("******************************************************************************\n\n");
                System.out.print("We will now allow you to browse each of our hotel's list of rooms and availability.\n\n");
		boolean checker = true;

		System.out.print("The following is a list of hotels with their corresponding hotelID.\n");
		System.out.println();
		String validIDS = "SELECT hotelName, hotelID FROM Hotel";

		System.out.println();
		List<List<String>> retrieve = esql.executeQueryAndReturnResult(validIDS);

		for(int r = 0; r < retrieve.size() / 2; r++){
		
			System.out.print("Hotel Name: ");
                        System.out.print(retrieve.get(r).get(0).trim());
                        System.out.print("\tHotel Name: ");
                        System.out.print(retrieve.get(r + 10).get(0));
			System.out.print("\nHotel ID: ");
			System.out.print(retrieve.get(r).get(1));
			System.out.print("\t        Hotel ID: ");
			System.out.print(retrieve.get(r + 10).get(1));
			System.out.println("\n");
				
		}
		
		System.out.print("Please enter the hotelID of the hotel you are interested in: ");
		String hotelID = in.readLine();

		while(checker){
			for(int i = 0; i < retrieve.size(); i++){
				if(hotelID.equals(retrieve.get(i).get(1))){
					checker = false;
				}
			}
			if(checker){
				System.out.println();
				System.out.print("The hotelID you typed does not exist. Please enter a valid hotelID: ");
				hotelID = in.readLine();
			}
		}

		System.out.println();

		boolean checkDate = false;
		String theDate = "";

		while(!checkDate){

		System.out.print("To view the list of room of for this hotel, please enter the date you wish to check in (yyyy-mm-dd): ");
                theDate = in.readLine();
		String dateFormat = "\\d{4}-\\d{2}-\\d{2}";

		if(theDate.matches(dateFormat)){
			checkDate = true;
		}
		else{
			System.out.println();
			System.out.print("The date you entered does not follow the correct format. Please enter the date using the correct format.");
			System.out.println("\n");
		}
		}

		System.out.println();

		String query = "SELECT rb.hotelID, rb.roomNumber, p.price, rb.bookingDate ";
		query += "FROM RoomBookings rb, Rooms p ";
		query += "WHERE rb.hotelID = ";
		query += hotelID;
		query += " AND p.price = (SELECT r.price FROM Rooms r WHERE r.hotelID = rb.hotelID AND r.roomNumber = p.roomNumber)";
		query += " ORDER BY rb.roomNumber";

		String query2 = "SELECT DISTINCT roomNumber, price FROM Rooms ";
		query2 += "WHERE hotelID = ";
		query2 += hotelID;
		query2 += " ORDER BY roomNumber";

		List<List<String>> test = esql.executeQueryAndReturnResult(query2);

		List<List<String>> retrieve2 = esql.executeQueryAndReturnResult(query);

		System.out.print("The following is a list of rooms that are available for the hotelID and date you entered.");
		System.out.println();
		System.out.print("-----------------------------------------------------------------------------------------");
		System.out.println();

		ArrayList<String> unavailableRooms = new ArrayList<String>();
		ArrayList<String> alreadyPrinted = new ArrayList<String>();
		ArrayList<Integer> indexPosition = new ArrayList<Integer>();
		ArrayList<String> notBookedButAvailable = new ArrayList<String>();
	//	ArrayList<String> noRoomsAvailable = new ArrayList<String>();
		
		notBookedButAvailable.add("1");
		notBookedButAvailable.add("2");
		notBookedButAvailable.add("3");
		notBookedButAvailable.add("4");
		notBookedButAvailable.add("5");
		notBookedButAvailable.add("6");
		notBookedButAvailable.add("7");
		notBookedButAvailable.add("8");
		notBookedButAvailable.add("9");
		notBookedButAvailable.add("10");

		for(int x = 0; x < retrieve2.size(); x++){
			
			if(theDate.equals(retrieve2.get(x).get(3))){
				
				unavailableRooms.add(retrieve2.get(x).get(1));
				notBookedButAvailable.remove(String.valueOf(retrieve2.get(x).get(1)));

			}
		}

		boolean checker2 = false;
		String currVal = retrieve2.get(0).get(1);


		for(int j = 0; j < retrieve2.size(); j++){

			if(!currVal.equals(retrieve2.get(j).get(1))){
				currVal = retrieve2.get(j).get(1);
				checker2 = false;
			}

			if(unavailableRooms.contains(retrieve2.get(j).get(1)) && !(checker2)){
						
			//	printUnavailable(retrieve2.get(j).get(1), retrieve2.get(j).get(2));
				checker2 = true;
				currVal = retrieve2.get(j).get(1);
				indexPosition.add(j);	
			}	
					
			else if(unavailableRooms.contains(retrieve2.get(j).get(1)) && checker2){

				continue;
			}
			else{

			if(alreadyPrinted.contains(retrieve2.get(j).get(1))){
				continue;
			}
			else{	

			System.out.print("Room Number: ");
			System.out.print(retrieve2.get(j).get(1));
			System.out.println();

			System.out.print("Room Price: $");
			int row = Integer.parseInt(retrieve2.get(j).get(1));
			System.out.print(test.get(row - 1).get(1));
			System.out.println();

			System.out.print("Status: Available");
			System.out.println();

			alreadyPrinted.add(retrieve2.get(j).get(1));
			notBookedButAvailable.remove(String.valueOf(retrieve2.get(j).get(1)));
			}
			
			}

			System.out.println();
		}

		for(int y = 0; y < notBookedButAvailable.size(); y++){
			
			String foundPrice = "";

			System.out.print("Room Number: ");
			System.out.print(notBookedButAvailable.get(y));
			System.out.println();
			
			System.out.print("Room Price: $");
			for(int z = 0; z < test.size(); z++){
				if(notBookedButAvailable.get(y).equals(test.get(z).get(0))){
					foundPrice = test.get(z).get(1);
					break;
				}
			} 
			System.out.print(foundPrice);
			System.out.println();

			System.out.print("Status: Available");
			System.out.println();
			System.out.println();
		}

		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.print("The following is a list of rooms that are unavailable for the hotelID and date you entered.");
		System.out.println();
		System.out.println();

		if(indexPosition.size() == 0){
			
			System.out.print("\tAll rooms for this hotel are currently available on the date you entered.");
			System.out.println();
		}else{
	
		for(int c = 0; c < indexPosition.size(); c++){
			
			System.out.print("Room Number: ");
			System.out.print(retrieve2.get(indexPosition.get(c)).get(1));
			System.out.println();

			System.out.print("Room Price: $");
			int row2 = Integer.parseInt(retrieve2.get(indexPosition.get(c)).get(1));
			System.out.print(test.get(row2 - 1).get(1));
			System.out.println();

			System.out.print("Status: Unavailable");
			System.out.println();
			System.out.println();

		}
		}
			System.out.println();
}

catch(Exception e){
	System.err.println(e.getMessage());
}
			
}
   public static void bookRooms(Hotel esql) {
	try{
		System.out.println();
                System.out.print("******************************************************************************\n\n");
                System.out.print("You will now have the ability to book a room in one of our hotels.\n\n");
                boolean checker = true;
		boolean overallCheck = false;

		
		while(!overallCheck){
                System.out.print("The following is a list of hotels with their corresponding hotelID.\n");
                System.out.println();
                String validIDS = "SELECT hotelName, hotelID FROM Hotel";
                List<List<String>> retrieveHotels = esql.executeQueryAndReturnResult(validIDS);

		for(int r = 0; r < retrieveHotels.size() / 2; r++){

                        System.out.print("Hotel Name: ");
                        System.out.print(retrieveHotels.get(r).get(0).trim());
                        System.out.print("\tHotel Name: ");
                        System.out.print(retrieveHotels.get(r + 10).get(0));
                        System.out.print("\nHotel ID: ");
                        System.out.print(retrieveHotels.get(r).get(1));
                        System.out.print("\t        Hotel ID: ");
                        System.out.print(retrieveHotels.get(r + 10).get(1));
                        System.out.println("\n");

                }
		
                System.out.print("Please enter the hotelID of the hotel you are interested in: ");
                String hotelID = in.readLine();

                while(checker){
                        for(int i = 0; i < retrieveHotels.size(); i++){
                                if(hotelID.equals(retrieveHotels.get(i).get(1))){
                                        checker = false;
                                }
                        }
                        if(checker){
				System.out.println();
                                System.out.print("The hotelID you typed does not exist. Please enter a valid hotelID: ");
                                hotelID = in.readLine();
                        }
                }
		System.out.println();
		System.out.print("--------------------------------------------------------------\n\n");
		System.out.print("The following is the list of room numbers for this hotel.\n ");
		System.out.println();

		String validRoomNums = "SELECT roomNumber ";
		validRoomNums += "FROM Rooms r ";
		validRoomNums += "WHERE hotelID = ";
		validRoomNums += hotelID;

		List<List<String>> retrieveRooms = esql.executeQueryAndReturnResult(validRoomNums);

		for(int q = 0; q < retrieveRooms.size() / 2; q++){

			System.out.print("Room Number: ");
			System.out.print(retrieveRooms.get(q).get(0));
			System.out.print("\tRoom Number: ");
			System.out.print(retrieveRooms.get(q + 5).get(0));
			System.out.println("\n");

		}

		System.out.print("Please select the room number of the room you wish to book: ");
                String roomNum = in.readLine();

		boolean checkRoom = false;
		
		while(!checkRoom){

			for(int i = 0; i < retrieveRooms.size(); i++){
				if(roomNum.equals(retrieveRooms.get(i).get(0))){
					checkRoom = true;
				}
			}

			if(!checkRoom){
				System.out.println();
				System.out.print("The room number you typed does not exist. Please enter a valid room number: ");
				roomNum = in.readLine();
			}
		}
		System.out.println();
		System.out.print("--------------------------------------------------------------\n");

		boolean checkDate = false;
                String theDate = "";

                while(!checkDate){
		
			System.out.println();
                	System.out.print("To view the availability of this room, please enter the date you wish to check in (yyyy-mm-dd): ");
                	theDate = in.readLine();
                	String dateFormat = "\\d{4}-\\d{2}-\\d{2}";

                	if(theDate.matches(dateFormat)){
                        	checkDate = true;
                	}	
                	else{
				System.out.println();
                        	System.out.print("The date you entered does not follow the correct format. Please enter the date using the correct format.");
                        	System.out.println();
                	}
                }

		System.out.println();

		String query = "SELECT rb.hotelID, rb.roomNumber, rb.bookingDate ";
		query += "FROM RoomBookings rb ";
		query += "WHERE rb.hotelID = ";
		query +=  hotelID;
		query += " AND rb.roomNumber = ";
		query += roomNum;
		query += " AND rb.bookingDate = '";
		query += theDate;
		query += "'";

		int booked = esql.executeQuery(query);

		if(booked > 0){
			System.out.print("This room is not available on the selected date. Please try again.");
			System.out.println();
			System.out.println();
			return;
		}

		boolean checkResp = false;
		String users = "";

                System.out.print("Congrats! This room is available on the date you chose! Would you like to book this room?\n\n");

                while(!checkResp){
                System.out.print("\tType '1' for 'yes'\n");
                System.out.print("\tType '2' for 'no'\n\n");
                System.out.print("Please enter your decision: ");
                users = in.readLine();

                if(users.equals("1") || users.equals("2")){

                        checkResp = true;

                }
                else{
                        System.out.println();
                        System.out.print("Invalid input! Please try again.");
                        users = in.readLine();

                }
                }

		System.out.println();

                if(users.equals("1")){

		String query2 = String.format("INSERT INTO RoomBookings (customerID, hotelID, roomNumber, bookingDate) VALUES ('%s','%s','%s','%s')", currentUserID, hotelID, roomNum, theDate);

		esql.executeUpdate(query2); 
	
		String query3 = "SELECT r.price ";
		query3 += "FROM Rooms r, RoomBookings rb ";
		query3 += "WHERE r.hotelID = ";
		query3 += hotelID;
		query3 += " AND r.roomNumber = ";
		query3 += roomNum;

		List<List<String>> retrieveResult = esql.executeQueryAndReturnResult(query3);

		System.out.println();
		System.out.print("Your room was booked successfully. The price of this room is: $");
		System.out.print(retrieveResult.get(0).get(0));
		System.out.println();
		System.out.println();

		overallCheck = true;
		}

		else{
			boolean checkMore = false;
			String theChoice = "";

			System.out.print("Would you like to attempt to book a different room?\n\n");

			while(!checkMore){
				System.out.print("\tType '1' for 'yes'\n");
				System.out.print("\tType '2' for 'no'\n\n");
				System.out.print("Please enter your decision: ");
				theChoice = in.readLine();

				if(theChoice.equals("1") || theChoice.equals("2")){
					
					checkMore = true;
					System.out.print("\n--------------------------------------------------------------\n");
				}
				else{

					System.out.println();
                        		System.out.print("Invalid input! Please try again.");
                        		theChoice = in.readLine();					
				}
			}
			System.out.println();

			if(theChoice.equals("1")){

				overallCheck = false;

			}
			else{
				System.out.print("Now returning you to MAIN MENU...\n");
				System.out.println();
				overallCheck = true;

			}
			

		}
	}


	}catch(Exception e){

		System.err.println(e.getMessage());
	}
		
}
   public static void viewRecentBookingsfromCustomer(Hotel esql) {

	try{

		System.out.println();
		System.out.print("******************************************************************************\n\n");
                System.out.print("The following is a list of up to 5 of your last most recent bookings.");
                System.out.println();
		System.out.println();

                String bookings = "SELECT rb.hotelID, rb.roomNumber, r.price, rb.bookingDate ";
                bookings += "FROM RoomBookings rb, Rooms r ";
                bookings += "WHERE rb.hotelID = r.hotelID AND rb.roomNumber = r.roomNumber AND rb.customerID = ";
                bookings += currentUserID;
                bookings += " ORDER BY rb.bookingDate";

                List<List<String>> retrieveBookings = esql.executeQueryAndReturnResult(bookings);

                int startLast5 = retrieveBookings.size() - 5;

                
		if(retrieveBookings.size() == 0){

                        System.out.println("You have no recent bookings on file.");
                        System.out.println();
                }

		else if(startLast5 < 0){

                        for(int i = 0; i < retrieveBookings.size(); i++){

				System.out.print("Hotel ID: ");
                                System.out.print(retrieveBookings.get(i).get(0));
                                System.out.print("\nRoom Number: ");
				System.out.print(retrieveBookings.get(i).get(1));
				System.out.print("\nRoom Price: $");
				System.out.print(retrieveBookings.get(i).get(2));
				System.out.print("\nBooking Date: ");
				System.out.print(retrieveBookings.get(i).get(3));
                                System.out.println();
				System.out.println();
                        }
                }

                else{

                        for(int i = startLast5; i < retrieveBookings.size(); i++){

				System.out.print("Hotel ID: ");
                                System.out.print(retrieveBookings.get(i).get(0));
                                System.out.print("\nRoom Number: ");
                                System.out.print(retrieveBookings.get(i).get(1));
                                System.out.print("\nRoom Price: $");
                                System.out.print(retrieveBookings.get(i).get(2));
                                System.out.print("\nBooking Date: ");
                                System.out.print(retrieveBookings.get(i).get(3));
                                System.out.println();
                                System.out.println();
                        }
                }

	}

	catch(Exception e){

		System.err.println(e.getMessage());
	}

}
   public static void updateRoomInfo(Hotel esql) {
	try{

		System.out.println();
                System.out.print("******************************************************************************\n\n");
                System.out.print("You will now have the ability to update the price and/or image URL of any room in a hotel you manage.\n\n");

		boolean checker = true;
		boolean contUpdate = true;

	while(contUpdate){

		System.out.print("The following is a list of hotels that you have access to edit its information.");
		System.out.println("\n");

		String validIDS = "SELECT DISTINCT h.hotelName, h.hotelID ";
		validIDS += "FROM Hotel h, Rooms r ";
		validIDS += "WHERE h.hotelID = r.hotelID AND managerUserID = ";
		validIDS += currentUserID;
		validIDS += " ORDER BY h.hotelID";
	
                List<List<String>> retrieveHotels = esql.executeQueryAndReturnResult(validIDS);

		for(int a = 0; a < retrieveHotels.size(); a++){

			System.out.print("Hotel Name: ");
			System.out.print(retrieveHotels.get(a).get(0));
			System.out.print("\nHotel ID: ");
			System.out.print(retrieveHotels.get(a).get(1));
			System.out.println("\n");
		}
                
		System.out.print("Please type in the hotelID to see information regarding its rooms: ");
                String hotelID = in.readLine();

                while(checker){
                        for(int i = 0; i < retrieveHotels.size(); i++){
                                if(hotelID.equals(retrieveHotels.get(i).get(1))){
                                        checker = false;
                                }
                        }
                        if(checker){
				System.out.println();
                                System.out.print("The hotelID you typed does not exist. Please enter a valid hotelID: ");
                                hotelID = in.readLine();
                        }
                }
		
		System.out.println();
		System.out.print("--------------------------------------------------------------\n\n");		

                String validRoomNums = "SELECT roomNumber, price, imageURL ";
                validRoomNums += "FROM Rooms r ";
                validRoomNums += "WHERE hotelID = ";
                validRoomNums += hotelID;

                List<List<String>> retrieveRooms = esql.executeQueryAndReturnResult(validRoomNums);

		System.out.print("The following is a list of rooms of the hotel you chose.");
		System.out.println("\n");

		for(int a = 0; a < retrieveRooms.size(); a++){

                        System.out.print("Room Number: ");
                        System.out.print(retrieveRooms.get(a).get(0));
                        System.out.print("\nRoom Price: $");
                        System.out.print(retrieveRooms.get(a).get(1));
			System.out.print("\nImage URL: ");
			System.out.print(retrieveRooms.get(a).get(2));
                        System.out.println("\n");
                }
                boolean checkRoom = false;

		System.out.print("Please type in the room number of the room you wish to update: ");
		String roomNum = in.readLine();

                while(!checkRoom){

                        for(int i = 0; i < retrieveRooms.size(); i++){
                                if(roomNum.equals(retrieveRooms.get(i).get(0))){
                                        checkRoom = true;
                                }
                        }

                        if(!checkRoom){
				System.out.println();
                                System.out.print("The room number you typed does not exist. Please enter a valid room number: ");
                                roomNum = in.readLine();
                        }
                }

		System.out.println();
		System.out.print("--------------------------------------------------------------\n\n");

		System.out.print("What would you like to update?\n\n");

		System.out.print("\tType '1' to update the price of the room you have chosen.\n");
		System.out.print("\tType '2' to update the image URL of the room you have chosen.\n\n");
		System.out.print("Please type in your response: ");
		String choice = in.readLine();
	
		boolean userChoice = false;
		
		while(!userChoice){
			
			if(choice.equals("1") || choice.equals("2") || choice.equals("3")){
			
				userChoice = true;	
			}
			else{
				System.out.println();		
				System.out.print("Invalid input. Please try again: ");
                                choice = in.readLine();
			}
		}

		if(choice.equals("1")){

			System.out.println();
			System.out.print("What would you like to change the room's price to?: $");
			String newPrice = in.readLine();

			System.out.println();
			
			String updatePrice = "UPDATE Rooms SET price = ";
			updatePrice += newPrice;
			updatePrice += " WHERE hotelID = ";
			updatePrice += hotelID;
			updatePrice += " AND roomNumber = ";
			updatePrice += roomNum;

			esql.executeUpdate(updatePrice);

			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			String updateTime = currentTime.toString();
			java.util.Date theDate = new java.util.Date();
	
			String newUpdate = String.format("INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedOn) VALUES ('%s', '%s', '%s', '%s');", currentUserID, hotelID, roomNum, theDate); 

			esql.executeUpdate(newUpdate);

			System.out.print("Price successfully updated.\n\n");
			System.out.print("--------------------------------------------------------------\n");	
		}
		else{

			System.out.println();
                        System.out.print("What would you like to change the room's image URL to?: ");
                        String newImage = in.readLine();
			
			System.out.println();

                        String updateImage = "UPDATE Rooms SET imageURL = '";
                        updateImage += newImage;
                        updateImage += "' WHERE hotelID = ";
                        updateImage += hotelID;
                        updateImage += " AND roomNumber = ";
                        updateImage += roomNum;

                        esql.executeUpdate(updateImage);

                        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                        String updateTime = currentTime.toString();
                        java.util.Date theDate = new java.util.Date();

                        String newUpdate = String.format("INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedOn) VALUES ('%s', '%s', '%s', '%s');", currentUserID, hotelID, roomNum, theDate);

                        esql.executeUpdate(newUpdate);

                        System.out.print("Image URL successfully updated.");
			System.out.print("\n\n--------------------------------------------------------------\n");
		}

		String moreUpdates = "";
		System.out.println();
		System.out.print("Would you like to make any more updates?\n\n");
		boolean updatesQ = false;

		while(!updatesQ){
		System.out.print("\tType '1' to make more updates.\n");
		System.out.print("\tType '2' to exit to MAIN MENU.\n\n");
		System.out.print("Please type in your response: ");
		moreUpdates = in.readLine();

		if(moreUpdates.equals("1") || moreUpdates.equals("2")){

			updatesQ = true;
		}
		else{
			System.out.println();
			System.out.print("Inavlid input. Please try again.\n\n");
		}
		}

		if(moreUpdates.equals("1")){
			
			contUpdate = true;
			System.out.print("\n--------------------------------------------------------------\n\n");
			
		}
		else if(moreUpdates.equals("2")){
			contUpdate = false;
		}
	}
	
	System.out.println();	

	}

	catch(Exception e){
		
		System.err.println(e.getMessage());
	}
}
   public static void viewRecentUpdates(Hotel esql) {
	
	try{
		
		System.out.println();
		System.out.print("******************************************************************************\n\n");
		System.out.print("The following is a list of hotels that you manage.");
                System.out.println();
		System.out.println();

                String validIDS = "SELECT DISTINCT h.hotelName, h.hotelID ";
                validIDS += "FROM Hotel h, Rooms r ";
                validIDS += "WHERE h.hotelID = r.hotelID AND managerUserID = ";
                validIDS += currentUserID;
		validIDS += " ORDER BY h.hotelID";

                List<List<String>> retrieveHotels = esql.executeQueryAndReturnResult(validIDS);

		for(int a = 0; a < retrieveHotels.size(); a++){

                        System.out.print("Hotel Name: ");
                        System.out.print(retrieveHotels.get(a).get(0));
                        System.out.print("\nHotel ID: ");
                        System.out.print(retrieveHotels.get(a).get(1));
                        System.out.println("\n");
                }

		String findUpdates = "SELECT * FROM RoomUpdatesLog ";
		findUpdates += "WHERE managerID = ";
		findUpdates += currentUserID;

                List<List<String>> retrieve = esql.executeQueryAndReturnResult(findUpdates);

		 System.out.print("--------------------------------------------------------------\n\n");

                System.out.print("Here are the last 5 updates of your hotels.\n");
		System.out.println();

		int startLast5 = retrieve.size() - 5;

		if(startLast5 < 0){
			
			for(int i = 0; i < retrieve.size(); i++){
			
				System.out.print("Update Number: ");
                                System.out.print(retrieve.get(i).get(0));
                                System.out.print("  Manager ID: ");
                                System.out.print(retrieve.get(i).get(1));
                                System.out.print("  Hotel ID: ");
                                System.out.print(retrieve.get(i).get(2));
                                System.out.print("  Room Number: ");
                                System.out.print(retrieve.get(i).get(3));
				System.out.print("  Date Updated: ");
				System.out.print(retrieve.get(i).get(4));
                                System.out.println();
                                System.out.println();
				
			}	
		}
		else{

                	for(int i = startLast5; i < retrieve.size(); i++){

				System.out.print("Update Number: ");
                                System.out.print(retrieve.get(i).get(0));
                                System.out.print("  Manager ID: ");
                                System.out.print(retrieve.get(i).get(1));
                                System.out.print("  Hotel ID: ");
                                System.out.print(retrieve.get(i).get(2));
                                System.out.print("  Room Number: ");
                                System.out.print(retrieve.get(i).get(3));
                                System.out.print("  Date Updated: ");
                                System.out.print(retrieve.get(i).get(4));
                                System.out.println();
                                System.out.println();
                	}
		}

	}
	catch(Exception e){

		System.err.println(e.getMessage());
	}
}
   public static void viewBookingHistoryofHotel(Hotel esql) {

	try{
		System.out.println();
		System.out.print("******************************************************************************\n\n");
		System.out.print("You will now have the ability to view the booking history of the hotels you manaage.\n\n");
		System.out.print("\tType '1' to view only the booking information of the hotel(s) you manage within a range of dates.\n");
		System.out.print("\tType '2' to view all the booking information of the hotel(s) you manage.\n\n");
		System.out.print("Please type in your response: ");
		
		String userInput = in.readLine();
		boolean userChoice = false;

                while(!userChoice){

                        if(userInput.equals("1") || userInput.equals("2")){

                                userChoice = true;
                        }
                        else{
				System.out.println();
                                System.out.print("Invalid input. Please try again: ");
                                userInput = in.readLine();
                        }
                }
		
		System.out.println();

		if(userInput.equals("1")){

			String firstDate = "";
                	String secondDate = "";
			boolean checkDate = false;

			System.out.print("--------------------------------------------------------------\n\n");
                	while(!checkDate){
				
				 System.out.print("Please type in the starting date of the range you desire (yyyy-mm-dd): ");
                        	 firstDate = in.readLine();
                		 String dateFormat = "\\d{4}-\\d{2}-\\d{2}";

                		 if(firstDate.matches(dateFormat)){
                        		checkDate = true;
                		 }
                		 else{
					System.out.println();
                        		System.out.print("The date you entered does not follow the correct format. Please enter the date using the correct format.");
                        		System.out.println();
					System.out.println();
                		 }
                        }

			System.out.println();
			checkDate = false;
			
			System.out.print("--------------------------------------------------------------\n\n");
			while(!checkDate){

                                 System.out.print("Please type in the ending date of the range you desire (yyyy-mm-dd): ");
                                 secondDate = in.readLine();
                                 String dateFormat = "\\d{4}-\\d{2}-\\d{2}";

                                 if(secondDate.matches(dateFormat)){
                                        
					if(firstDate.compareTo(secondDate) < 0){
						checkDate = true;
					}
					else{
						System.out.println();
						System.out.print("The ending date you entered does not fall after the starting date. Please enter a valid ending date.");
                                        System.out.println();
                                        System.out.println();
						
					}
                                 }
                                 else{
					System.out.println();
                                        System.out.print("The date you entered does not follow the correct format. Please enter the date using the correct format.");
                                        System.out.println();
                                        System.out.println();
                                 }
                        }

			System.out.print("\n--------------------------------------------------------------\n\n");
			System.out.print("The following is the booking information of the hotel(s) you manage between the dates " + firstDate + " and " + secondDate + ".\n");
                	System.out.println();
                	System.out.println();

			String query = "SELECT rb.bookingID, rb.bookingDate, rb.hotelID, rb.roomNumber, u.name ";
                	query += "FROM RoomBookings rb, Hotel h, Users u ";
                	query += "WHERE rb.customerID = u.userID AND rb.hotelID = h.hotelID AND h.managerUserID = ";
                	query += currentUserID;
			query += " AND rb.bookingDate BETWEEN '";
			query += firstDate;
			query += "' AND '";
			query += secondDate;
                	query += "' ORDER BY rb.bookingDate";

			List<List<String>> retrieve = esql.executeQueryAndReturnResult(query);

			if(retrieve.size() == 0){

                        	System.out.print("There is no booking information for the hotel(s) you manage for the dates you chose.");
                        	System.out.println();
                        	System.out.println();
                	}
                	else{

                        	for(int i = 0; i < retrieve.size(); i++){

                                	System.out.print("Booking ID: ");
                                	System.out.print(retrieve.get(i).get(0));
                                	System.out.print("  Booking Date: ");
                                	System.out.print(retrieve.get(i).get(1));
                                	System.out.print("  Hotel ID: ");
                                	System.out.print(retrieve.get(i).get(2));
                                	System.out.print("  Room Number: ");
                                	System.out.print(retrieve.get(i).get(3).trim());
                                	System.out.print("  Customer Name: ");
                                	System.out.print(retrieve.get(i).get(4).trim());
                                	System.out.println();
                                	System.out.println();
                        	}
                	}

		}
		else{
		System.out.print("--------------------------------------------------------------\n\n");
		System.out.print("The following is the booking information of the hotel(s) you manage.\n\n");
		System.out.println();

		String query = "SELECT rb.bookingID, rb.bookingDate, rb.hotelID, rb.roomNumber, u.name ";
		query += "FROM RoomBookings rb, Hotel h, Users u ";
		query += "WHERE rb.customerID = u.userID AND rb.hotelID = h.hotelID AND h.managerUserID = ";
		query += currentUserID;
		query += " ORDER BY rb.bookingDate";

		//esql.executeQueryAndPrintResult(query);
		List<List<String>> retrieve = esql.executeQueryAndReturnResult(query);

		if(retrieve.size() == 0){
			
			System.out.print("There is no booking information for the hotel(s) you manage.");
			System.out.println();
			System.out.println();
		}
		else{

			for(int i = 0; i < retrieve.size(); i++){
				
				System.out.print("Booking ID: ");
                                System.out.print(retrieve.get(i).get(0));
                                System.out.print("  Booking Date: ");
                                System.out.print(retrieve.get(i).get(1));
                                System.out.print("  Hotel ID: ");
                                System.out.print(retrieve.get(i).get(2));
                                System.out.print("  Room Number: ");
                                System.out.print(retrieve.get(i).get(3).trim());
                                System.out.print("  Customer Name: ");
                                System.out.print(retrieve.get(i).get(4).trim());
                                System.out.println();
                                System.out.println();
			}
		}

		}
		System.out.println();
	}

	catch(Exception e){

		System.err.println(e.getMessage());

	}
}
   public static void viewRegularCustomers(Hotel esql) {

	try{

		System.out.println();
		System.out.print("******************************************************************************\n\n");
                System.out.print("The following is a list of hotels that you manage.");
                System.out.println();
                System.out.println();

                String validIDS = "SELECT DISTINCT h.hotelName, h.hotelID ";
                validIDS += "FROM Hotel h, Rooms r ";
                validIDS += "WHERE h.hotelID = r.hotelID AND managerUserID = ";
                validIDS += currentUserID;
                validIDS += " ORDER BY h.hotelID";

		List<List<String>> retrieveHotels = esql.executeQueryAndReturnResult(validIDS);

		for(int a = 0; a < retrieveHotels.size(); a++){

                        System.out.print("Hotel Name: ");
                        System.out.print(retrieveHotels.get(a).get(0));
                        System.out.print("\nHotel ID: ");
                        System.out.print(retrieveHotels.get(a).get(1));
                        System.out.println("\n");
                }

		boolean checker = true;

                System.out.print("Please type in a valid hotelID to see the top 5 customers who made the most bookings in this hotel: ");
                String hotelID = in.readLine();

                while(checker){

                        for(int i = 0; i < retrieveHotels.size(); i++){
                                if(hotelID.equals(retrieveHotels.get(i).get(1))){
                                        checker = false;
                                }
                        }
                        if(checker){
				System.out.println();
                                System.out.print("The hotelID you typed is not valid. Please enter a valid hotelID: ");
                                hotelID = in.readLine();
                        }
                }

		String query = "SELECT COUNT(rb.customerID), u.name ";
		query += "FROM RoomBookings rb, Users u ";
		query += "WHERE rb.customerID = u.userID AND rb.hotelID = ";
		query += hotelID;
		query += " GROUP BY u.name";

		
		List<List<String>> retrieve = esql.executeQueryAndReturnResult(query);

		ArrayList<String> potentialNames = new ArrayList<String>();
		ArrayList<String> customerID = new ArrayList<String>();
		ArrayList<String> numBookings = new ArrayList<String>();
		ArrayList<String> top5 = new ArrayList<String>();

		int currMax = Integer.parseInt(retrieve.get(0).get(0));

		for(int k = 0; k < retrieve.size(); k++){

			int currCount = Integer.parseInt(retrieve.get(k).get(0));
			
			if(currCount >= currMax){

				potentialNames.add(retrieve.get(k).get(1));
				numBookings.add(retrieve.get(k).get(0));
				currMax = currCount;
			}
		}

		String query1 = "SELECT DISTINCT rb.customerID, u.name ";
                query1 += "FROM RoomBookings rb, Users u ";
                query1 += "WHERE rb.customerID = u.userID AND rb.hotelID = ";
                query1 += hotelID;
		query1 += " ORDER BY rb.customerID";

                List<List<String>> retrieve2 = esql.executeQueryAndReturnResult(query1);

		int endIndex = potentialNames.size() - 5;
		List<String> necessBookings = numBookings.subList(endIndex, numBookings.size());
	
		for(int l = endIndex; l < potentialNames.size(); l++){
			
			top5.add(potentialNames.get(l));
		}

		System.out.println();
	        System.out.print("--------------------------------------------------------------\n\n");
		System.out.print("The following is a list of the top 5 customers who made the most bookings in this hotel.\n\n\n");

		for(int e = 0; e < top5.size(); e++){

			for(int f = 0; f < retrieve2.size(); f++){

				if(top5.get(e).equals(retrieve2.get(f).get(1))){

					customerID.add(retrieve2.get(f).get(0));
				}

			}	

			
		}


		for(int p = top5.size() - 1; p >= 0; p--){

			System.out.print("Customer ID: " + customerID.get(p) + "  Customer Name: " + top5.get(p).trim() + "  Number Bookings: " + necessBookings.get(p));
			System.out.println();
			System.out.println();
		}
		
		System.out.println();		

	}

	catch(Exception e){
	
		System.err.println(e.getMessage());	
	}
}
   public static void placeRoomRepairRequests(Hotel esql) {

	try{

		System.out.println();
		System.out.print("******************************************************************************\n\n");
                System.out.print("The following is a list of hotels that you manage.");
                System.out.println();
                System.out.println();

                String validIDS = "SELECT DISTINCT h.hotelName, h.hotelID ";
                validIDS += "FROM Hotel h, Rooms r ";
                validIDS += "WHERE h.hotelID = r.hotelID AND managerUserID = ";
                validIDS += currentUserID;
                validIDS += " ORDER BY h.hotelID";

                List<List<String>> retrieveHotels = esql.executeQueryAndReturnResult(validIDS);

		for(int a = 0; a < retrieveHotels.size(); a++){

                        System.out.print("Hotel Name: ");
                        System.out.print(retrieveHotels.get(a).get(0));
                        System.out.print("\nHotel ID: ");
                        System.out.print(retrieveHotels.get(a).get(1));
                        System.out.println("\n");
                }

                boolean checker = true;

                System.out.print("Please type in a valid hotelID to place a room repair request: ");
                String hotelID = in.readLine();

                while(checker){

                        for(int i = 0; i < retrieveHotels.size(); i++){
                                if(hotelID.equals(retrieveHotels.get(i).get(1))){
                                        checker = false;
                                }
                        }
                        if(checker){
				System.out.println();
                                System.out.print("The hotelID you typed is not valid. Please enter a valid hotelID: ");
                                hotelID = in.readLine();
                        }
                }

		System.out.println();
		System.out.print("--------------------------------------------------------------\n\n");
		System.out.print("The following is a list of rooms of the hotel you chose.");
                System.out.println("\n");		

                String validRoomNums = "SELECT roomNumber, price ";
                validRoomNums += "FROM Rooms r ";
                validRoomNums += "WHERE hotelID = ";
                validRoomNums += hotelID;
		validRoomNums += " ORDER BY roomNumber";

                List<List<String>> retrieveRooms = esql.executeQueryAndReturnResult(validRoomNums);

		for(int a = 0; a < retrieveRooms.size() / 2; a++){

                        System.out.print("Room Number: ");
                        System.out.print(retrieveRooms.get(a).get(0));
                        System.out.print("\tRoom Number: ");
			System.out.print(retrieveRooms.get(a + 5).get(0));
			System.out.println("\n");
                }

                boolean checkRoom = false;
		System.out.print("Please select the room number of the room you wish to repair: ");
                String roomNum = in.readLine();

                while(!checkRoom){

                        for(int i = 0; i < retrieveRooms.size(); i++){
                                if(roomNum.equals(retrieveRooms.get(i).get(0))){
                                        checkRoom = true;
                                }
                        }

                        if(!checkRoom){
				System.out.println();
                                System.out.print("The room number you typed does not exist. Please enter a valid room number: ");
                                roomNum = in.readLine();
                        }
                }

		System.out.println();
		System.out.print("--------------------------------------------------------------\n\n");

		System.out.print("The following is a list of maintenance companies that are available for you to choose.");
                System.out.println();
                System.out.println();

                String validComp = "SELECT mc.name, mc.companyID ";
                validComp += "FROM MaintenanceCompany mc ";
                validComp += " ORDER BY mc.companyID";

                List<List<String>> retrieveComp = esql.executeQueryAndReturnResult(validComp);

		for(int a = 0; a < retrieveComp.size(); a++){

                        System.out.print("Company Name: ");
                        System.out.print(retrieveComp.get(a).get(0));
                        System.out.print("\nCompany ID: ");
                        System.out.print(retrieveComp.get(a).get(1));
                        System.out.println("\n");
                }

		System.out.print("Please select the companyID of the maintenance company that will handle this repair request: ");
                String companyID = in.readLine();

                boolean checkComp = false;

                while(!checkComp){

                        for(int i = 0; i < retrieveComp.size(); i++){
                                if(companyID.equals(retrieveComp.get(i).get(1))){
                                        checkComp = true;
                                }
                        }

                        if(!checkComp){
				System.out.println();
                                System.out.print("The companyID you typed does not exist. Please enter a valid companyID: ");
                                companyID = in.readLine();
                        }
                }

                System.out.println();

		LocalDate theDate = LocalDate.now();
		
		String query = String.format("INSERT INTO RoomRepairs (companyID, hotelID, roomNumber, repairDate) VALUES ('%s','%s','%s','%s')", companyID, hotelID, roomNum, theDate);

                esql.executeUpdate(query);

		String getRepairID = "SELECT repairID FROM RoomRepairs";
		
		List<List<String>> retrieve = esql.executeQueryAndReturnResult(getRepairID);

		String repairID = retrieve.get(retrieve.size() - 1).get(0);

		String query2 = String.format("INSERT INTO RoomRepairRequests (managerID, repairID) VALUES ('%s','%s')", currentUserID, repairID);
		esql.executeUpdate(query2);


		System.out.println();
		System.out.print("Successfully placed your room repair request.");
		System.out.println();
		System.out.println();

		

	}

	catch(Exception e){

		System.err.println(e.getMessage());
	}
}
   public static void viewRoomRepairHistory(Hotel esql) {

	try{

		System.out.println();
		System.out.print("******************************************************************************\n\n");
		System.out.print("The following is a list of hotels that you manage.");
                System.out.println();
                System.out.println();

                String validIDS = "SELECT DISTINCT h.hotelName, h.hotelID ";
                validIDS += "FROM Hotel h, Rooms r ";
                validIDS += "WHERE h.hotelID = r.hotelID AND managerUserID = ";
                validIDS += currentUserID;
                validIDS += " ORDER BY h.hotelID";

                List<List<String>> retrieveHotels = esql.executeQueryAndReturnResult(validIDS);

                for(int a = 0; a < retrieveHotels.size(); a++){

                        System.out.print("Hotel Name: ");
                        System.out.print(retrieveHotels.get(a).get(0));
                        System.out.print("\nHotel ID: ");
                        System.out.print(retrieveHotels.get(a).get(1));
                        System.out.println("\n");
                }

		System.out.print("--------------------------------------------------------------\n\n");		
		System.out.print("The following is a list of all the room repair requests history for the hotels you manage.\n");
		System.out.println();

		String query = "SELECT rr.companyID, rr.hotelID, rr.roomNumber, rr.repairDate ";
		query += "FROM RoomRepairs rr, RoomRepairRequests q ";
		query += "WHERE rr.repairID = q.repairID AND q.managerID = ";
		query += currentUserID;

		List<List<String>> retrieve = esql.executeQueryAndReturnResult(query);

		for(int i = 0; i < retrieve.size(); i++){

                        System.out.print("Company ID: ");
                        System.out.print(retrieve.get(i).get(0));
			System.out.println();
                        System.out.print("Hotel ID: ");
                        System.out.print(retrieve.get(i).get(1));
			System.out.println();
                        System.out.print("Room Number: ");
                        System.out.print(retrieve.get(i).get(2));
			System.out.println();
                        System.out.print("Repair Date: ");
                        System.out.print(retrieve.get(i).get(3));
                        System.out.println();
                        System.out.println();


                }

	}
	catch(Exception e){

		System.err.println(e.getMessage());
	}
}

}//end Hotel
