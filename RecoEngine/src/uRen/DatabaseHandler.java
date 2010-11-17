package uRen;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseHandler {
	
	//invalidation flags
	boolean customerCacheValid = false;
	boolean albumCacheValid = false;
	boolean purchaseCacheValid = false;
	
	//memory cache
	ArrayList<Customer> customerCache;
	ArrayList<Album> albumCache;
	
	public DatabaseHandler() {
		customerCache = new ArrayList<Customer>();
		albumCache = new ArrayList<Album>();
	}
	
	public void ForceInvalidateCache() {
		customerCache.clear();
		customerCacheValid = false;
		
		albumCache.clear();
		albumCacheValid = false;
		
		//todo: i still need to finalize the purchase database design, and build it in memory
		purchaseCacheValid = false;
	}
	
	//insert into Users database
	public int InsertIntoToUserTable(String firstName, String lastName, String nickName, String emailAddress) { //+age?, nickName [unique]
		int resultRows=-1;
		
		try {
			Class.forName(SessionSettings.ConnectorString);
			Connection con = DriverManager.getConnection(SessionSettings.ServerUrl + SessionSettings.Database,
													SessionSettings.Username, SessionSettings.Password);
			Statement stmt = con.createStatement();

			String command = "insert into Customer (firstName, lastName, nickName, emailAddress) values (" +
			"\"" + firstName + "\"," +
			"\"" + lastName + "\"," +
			"\"" + nickName + "\"," +
			"\"" + emailAddress + "\")";
			Logger.Log("insert command: " + command);
			resultRows = stmt.executeUpdate(command);
			con.close();
			Logger.Log("Success!");
			customerCacheValid = false;
		}
		catch (Exception ex) {
			Logger.Log(ex.getMessage());
			resultRows = -1;
		}
		return resultRows;
	}
	
	//Insert into Albums Database 
	public int InsertIntoToAlbumDatabase(String artist, String name, String genre, int price) {
		int resultRows=-1;
		
		try {
			Class.forName(SessionSettings.ConnectorString);
			Connection con = DriverManager.getConnection(SessionSettings.ServerUrl + SessionSettings.Database,
													SessionSettings.Username, SessionSettings.Password);
			Statement stmt = con.createStatement();

			//artist, name, price, genre
			String command = "insert into Album (albumName, artistName, genre, price) values (" +
			"\"" + artist + "\"," +
			"\"" + name + "\"," +
			"\"" + genre + "\"," +
			"\"" + price + "\")";
			Logger.Log("insert command: " + command);
			resultRows = stmt.executeUpdate(command);
			con.close();
			Logger.Log("Success!");
			albumCacheValid = false;
		}
		catch (Exception ex) {
			Logger.Log(ex.getMessage());
			resultRows = -1;
		}
		return resultRows;
	}
	
	//Insert into Purchase History Database
	public int InsertIntoToPurchaseDatabase(int userID, int albumID, Date purchaseDate) {
		int resultRows=-1;
		
		try {
			Class.forName(SessionSettings.ConnectorString);
			Connection con = DriverManager.getConnection(SessionSettings.ServerUrl + SessionSettings.Database,
													SessionSettings.Username, SessionSettings.Password);
			Statement stmt = con.createStatement();

			//userID, albumID, genre, artist
			String command = "insert into Purchase (Customer_idCustomer, Album_idAlbum, date) values (" +
			"\"" + userID + "\"," +
			"\"" + albumID + "\"," +
			/*"\"" + purchaseDate + "\"," +*/
			"\"" + purchaseDate.toString() + "\")"; //BUG! convert java Date to sql DATETIME
			Logger.Log("insert command: " + command);
			resultRows = stmt.executeUpdate(command);
			con.close();
			Logger.Log("Success!");
			purchaseCacheValid = false;
		}
		catch (Exception ex) {
			Logger.Log(ex.getMessage());
			resultRows = -1;
		}
		return resultRows;
	}
	
	public ArrayList<Customer> ForceCustomerCacheUpdate(/*int id*/) {
		ResultSet results=null;
		
		/*
		if (true == customerCacheValid) {
			
			if (-1==id) return customerCache; //return complete list from cache
			
			ArrayList<Customer> matches = new ArrayList<Customer>();
			for (Customer cust: customerCache) {
				if (cust.getIdCustomer() == id) {
					matches.add(cust);
				}//end if
			}//end for (search)
			return matches;
		}//end if
		*/
		
		//cache is invalid, so delete what we have in memory...
		customerCache.clear();
		
		try {
			Class.forName(SessionSettings.ConnectorString);
			Connection con = DriverManager.getConnection(SessionSettings.ServerUrl + SessionSettings.Database,
													SessionSettings.Username, SessionSettings.Password);
			
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			String command = "select * from Customer;";
			//check id, if it is -1, send back all rows
			/*if (-1==id) {
				command = "select * from Customer;";
			} else {
				command = "select * from Customer where idCustomer=" + id + ";";
			}*/
			Logger.Log("command: " + command);
			results = stmt.executeQuery(command);
			
			while (results.next()) {
				int cid = results.getInt(SessionSettings.Customer_idCustomer);
				String fname = results.getString(SessionSettings.Customer_firstName);
				String lname = results.getString(SessionSettings.Customer_lastName);
				String nname = results.getString(SessionSettings.Customer_nickName);
				String email = results.getString(SessionSettings.Customer_emailAddress);
				
				Customer cust = new Customer(cid, fname, lname, nname, email);
				customerCache.add(cust);
				
				String output = "[" + cid + "] " +lname + ", " + fname + " (" + nname + ") " + " : " + email;
				Logger.Log(output);
			}
			
			con.close();
			Logger.Log("Success!");
			customerCacheValid = true;
		}
		catch (Exception ex) {
			Logger.Log(ex.getMessage());
			results = null;
		}
		return customerCache;
	}
	
	public ArrayList<Album> ForceAlbumCacheUpdate(/*int id*/) {
		ResultSet results=null;
		
		/*
		if (true == albumCacheValid) {
			
			if (-1==id) return albumCache; //return complete list from cache
			
			ArrayList<Album> matches = new ArrayList<Album>();
			for (Album alb: albumCache) {
				if (alb.getID() == id) {
					matches.add(alb);
				}//end if
			}//end for (search)
			return matches;
		}//end if
		*/
		
		//cache is invalid, so delete what we have in memory...
		albumCache.clear();
		
		try {
			Class.forName(SessionSettings.ConnectorString);
			Connection con = DriverManager.getConnection(SessionSettings.ServerUrl + SessionSettings.Database,
													SessionSettings.Username, SessionSettings.Password);
			
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			String command = "select * from Album;";
			//check id, if it is -1, send back all rows
			/*if (-1==id) {
				command = "select * from Album;";
			} else {
				command = "select * from Album where idAlbum=" + id + ";";
			}*/
			Logger.Log("command: " + command);
			results = stmt.executeQuery(command);
			
			while (results.next()) {
				int aid = results.getInt(SessionSettings.Album_idAlbum);
				String alname = results.getString(SessionSettings.Album_albumName);
				String arname = results.getString(SessionSettings.Album_artistName);
				String genre = results.getString(SessionSettings.Album_genre);
				int price = results.getInt(SessionSettings.Album_price);
				
				Album album = new Album(aid, alname, arname, genre, price);
				albumCache.add(album);
				
				String output = "[" + aid + "] " +alname + ", " + arname + " (" + genre + ") " + " : $" + price;
				Logger.Log(output);
			}
			
			con.close();
			Logger.Log("Success!");
			albumCacheValid = true;
		}
		catch (Exception ex) {
			Logger.Log(ex.getMessage());
			results = null;
		}
		return albumCache;
	}
	
	
	public Customer GetCustomerByEmailAddress(String emailAddress) {
		if (false == customerCacheValid) {
			ForceCustomerCacheUpdate(); //force read of customer table
		}
		
		Customer user = new Customer();
		boolean matchFound = false;
		for (Customer cust : customerCache) {
			if ( 0 == cust.getEmailAddress().compareToIgnoreCase(emailAddress) ) {
				//found a match!
				matchFound = true;
				user.Clone(cust);
				break; //stop searching
			}
		}
		
		return matchFound == true ? user : null; 
	}
	
	public Customer GetCustomerByID(int id) {
		if (false == customerCacheValid) {
			ForceCustomerCacheUpdate(); //force read of customer table
		}
		
		Customer user = new Customer();
		boolean matchFound = false;
		for (Customer cust : customerCache) {
			if ( id == cust.getIdCustomer() ) {
				//found a match!
				matchFound = true;
				user.Clone(cust);
				break; //stop searching
			}
		}
		
		return matchFound == true ? user : null; 
	}
	
	public Album GetAlbumByName(String artistName, String albumName) {
		if (false == albumCacheValid) {
			ForceAlbumCacheUpdate(); //force read of album table
		}
		
		Album alb = new Album();
		boolean matchFound = false;
		for (Album album : albumCache) {
			if ( artistName == album.getArtistName() && albumName == album.getAlbumName() ) {
				//found a match!
				matchFound = true;
				alb.Clone(album);
				break; //stop searching
			}
		}
		
		return matchFound == true ? alb : null; 
	}
	
	public Album GetAlbumByID(int id) {
		if (false == albumCacheValid) {
			ForceAlbumCacheUpdate(); //force read of album table
		}
		
		Album alb = new Album();
		boolean matchFound = false;
		for (Album album : albumCache) {
			if ( id == album.getID() ) {
				//found a match!
				matchFound = true;
				alb.Clone(album);
				break; //stop searching
			}
		}
		
		return matchFound == true ? alb : null; 
	}
	
	
}
