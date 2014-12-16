package com.webtech.cinema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.appidentity.AppIdentityServicePb.AppIdentityServiceError.ErrorCode;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class AppEngineCinemaServlet extends HttpServlet {
	
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		int movieId = 0;
		
		if(req.getParameter("movie")==null){
			
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No movie id specified...");
			return;
		}
		else{
			movieId=Integer.parseInt(req.getParameter("movie"));
		}
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		
		Filter movieIdFilter =
				  new FilterPredicate("movieId",
				                      FilterOperator.EQUAL,
				                      movieId);

		// Use class Query to assemble a query
		Query q = new Query("Reservation").setFilter(movieIdFilter);

		// Use PreparedQuery interface to retrieve results
		PreparedQuery preparedQuery = ds.prepare(q);
		
		ArrayList<Integer> allReservationSeats=new ArrayList<Integer>();
		ArrayList<Integer> seats;
		
		for (Entity reservationEntity : preparedQuery.asIterable()) {
  
		  seats=(ArrayList<Integer>) reservationEntity.getProperty("seats");
		  allReservationSeats.addAll(seats);
		  
		}
				
		resp.setContentType("application/json");
		resp.getWriter().println(new JSONArray(allReservationSeats).toString());
	}
	
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException  {
		
		
		String name=null;
		String email=null;
		JSONArray seatsJSONArray=null;
		int movieId=0;
		String reservationId=null;
		try {
			
			//get JSON string from request body
			String jsonString=getBody(req);
				
			JSONObject jsonObj=new JSONObject(jsonString);
	
			//get properties of received JSON object
			name=jsonObj.getString("name");		
			email = jsonObj.getString("email");
			seatsJSONArray=jsonObj.getJSONArray("seats");
			movieId = Integer.parseInt(jsonObj.getString("movieId"));
			
			//create unique key for this reservation
			reservationId=createReservationId(email+movieId+Calendar.getInstance().getTimeInMillis());	
			
		} catch (NumberFormatException e) {					
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		} catch (JSONException e) {			
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}
		
		
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		//entity group of Reservation kind
		Key reservationKey=KeyFactory.createKey("Reservation", reservationId);
		
		Entity reservation=new Entity(reservationKey);
		
		reservation.setProperty("name", name);
		reservation.setProperty("email", email);
		reservation.setProperty("seats", makeArrayList(seatsJSONArray));
		reservation.setProperty("movieId", movieId);
		
		ds.put(reservation);
	
		
  	  	PrintWriter writer = resp.getWriter();        
  	  	writer.println("Your reservation ID is: "+reservationId); 
	}
	
	
	
	
	
	
	private String createReservationId(String input){
		
	            MessageDigest md=null;
				try {
					md = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            md.reset();
	            byte[] buffer = input.getBytes();
	            md.update(buffer);
	            byte[] digest = md.digest();

	            String hexStr = "";
	            for (int i = 0; i < digest.length; i++) {
	                hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
	            }
	            return hexStr;
	     
	}
	
	
	public static String getBody(HttpServletRequest request) throws IOException {

	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;

	    try {
	        InputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	}
	
	private ArrayList<Integer>makeArrayList(JSONArray jsonArray){
		ArrayList<Integer> listdata = new ArrayList<Integer>();     
		JSONArray jArray = jsonArray;
		if (jArray != null) { 
		   for (int i=0;i<jArray.length();i++){ 
		    try {
				listdata.add(Integer.parseInt(jArray.get(i).toString()));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   } 
		} 
		
		return listdata;
	}
	
}
