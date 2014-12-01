package servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.*;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Servlet implementation class cinema
 */
@WebServlet("/cinema")
public class cinema extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public cinema() {
        super();
    }
    
    String printit = "";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	String result = "";
    	String movieID = request.getParameter("movie"); // url?movie=MOVIEID
    	 
    	Connection c = null;
        Statement statement = null;
    	  
    	try {
    		// Check for existing db driver.
    		Class.forName("org.sqlite.JDBC");
			
    		// Get the database.
    		String path = new File(getServletContext().getRealPath("cinema.s3db")).getAbsolutePath();
			
    		c = DriverManager.getConnection("jdbc:sqlite:" + path);
			
    		System.out.println("Opened database successfully");
	
    		// Create statement.
    		statement = c.createStatement();
    		String sql = "SELECT seat FROM seats WHERE movie_id=" + String.valueOf(movieID);
    		
    		// Execute the sql query.
    		ResultSet resultSet = statement.executeQuery(sql);
		      
    		// Create a JSON string of the result set of already reserved seats.
    		// Note: No external library is used here to create the JSON string.
    		result = "[";
    		boolean firstEntry = true;
    		while(resultSet.next()){
    			if (!firstEntry) {
    				result += ",";
    			}
    			int id  = resultSet.getInt("seat");
    			result += "\"" + String.valueOf(id) + "\"";
    			firstEntry = false;
    		}
    		resultSet.close();
    		result += "]";
		      
    		statement.close();
    		c.close();
		  } catch (Exception e1) {
			System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
			e1.printStackTrace();
		  }
    	  
    	
      	  //System.out.println(" Response is: " + result); // Debugging :-)
      	  
    	  // Create the response which consists of the JSON string of reservated seats.
          response.setContentType("text/html");
          PrintWriter writer = response.getWriter();        
          writer.println(result); 
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// The gson bulder is needed to interpret the JSON string contained in the request. 
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        
        // Message digest generates a MD5 Hash (analogous to the PHP task) to get the
        // reservation id which consists of the inputs email, movie id and the current time.
        MessageDigest md = null;
        try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        
        // Read the request body to get the JSON string that is contained in it.
		InputStream inputStream = request.getInputStream();
		BufferedReader reader = null;
		StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null) {
        	reader = new BufferedReader(new InputStreamReader(inputStream));
            char[] charBuffer = new char[128];
            int bytesRead = -1;
            while ((bytesRead = reader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        } else {
            stringBuilder.append("");
        }
        String jsonRequest = stringBuilder.toString();
		System.out.println("Request is :" + jsonRequest);
		
		// gson allows to fill an object with JSON data. The object must have the corresponding 
		// JSON data as attributes (see ReservationRequest.java).
		ReservationRequest reqObj = gson.fromJson(jsonRequest, ReservationRequest.class);
		
		// Now that we got the ReservationRequest as an object we first generate the MD5 hash.
		md.reset();
		Calendar cal = Calendar.getInstance();
		String plaintext = reqObj.getEmail() + reqObj.getMovieId() + cal.getTime();
		md.update(plaintext.getBytes());
        byte[] hash = md.digest();
        BigInteger hashInt = new BigInteger(1,hash); // We work with this object
        String hashString = hashInt.toString(16); // This string is for debugging purposes.
		
        // Initialise some variable for further work.
    	int res_id = hashInt.intValue();
    	int movie_id = reqObj.getMovieId();
    	String name = reqObj.getName();
    	String email = reqObj.getEmail();
    	String seatnum = reqObj.getSeats().toString();
    	int[] seats = reqObj.getSeats();
    	
    	// Set up database connection.
    	Connection c = null;
        Statement statement = null;
    	  
    	try {
    		// Check for existing db driver.
    		Class.forName("org.sqlite.JDBC");
    		
    		// Get the database.
    		String path = new File(getServletContext().getRealPath("cinema.s3db")).getAbsolutePath();
			
    		c = DriverManager.getConnection("jdbc:sqlite:" + path);
    		System.out.println("Opened database successfully");
	
    		statement = c.createStatement();
    		
    		// Insert the reservation to the db. Each selected seat must execute its own statement to the db.
    		for (int seat : seats) {
	    		String sql = "INSERT INTO seats (res_id,movie_id,name,email,seat) VALUES ("
	    				+ String.valueOf(res_id) + "," + String.valueOf(movie_id) + "," + "'" + String.valueOf(name) + "'"
	    				+ "," + "'" + String.valueOf(email) + "'" + "," + String.valueOf(seat) + ")"; 
	    		System.out.println("SQL: " + sql);
	    		statement.execute(sql);
	    		System.out.println("Seat number " + String.valueOf(seat) + " was inserted to db");
    		}
		  } catch (Exception e1) {
			System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
			e1.printStackTrace();
		  }
    	
    	  // The response consists of the movie id.
    	  PrintWriter writer = response.getWriter();        
          writer.println(String.valueOf(res_id)); 
    	  
	}
}
