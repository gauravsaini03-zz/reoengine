package uRen;

import java.util.ArrayList;




public class RecommendationEngine {
	
	DatabaseHandler db;

	public RecommendationEngine() {
		Logger.Log("recommendation engine created");
		db = new DatabaseHandler();
		if (db == null) {
			Logger.Log("db handler NOT created!");
		}
	}

	public ArrayList<Album> GetRecommendations_alg1(Customer cust, String artist) {
		//ArrayList<Album> results = new ArrayList<Album>();
		//results.add(new Album(99, "darkside of the moon", "pink floyd", "rock", 100));
		Purchase list = db.GetAlbumsPurchaseByCustomer(cust);
		return list.getPurchases();
	}
	
	public ArrayList<Album> GetRecommendations_alg2a(Customer cust, String genre) {
		ArrayList<Album> results = new ArrayList<Album>();
		//results.add(new Album(98, "darkside of the moon2a", "pink floyd", "rock", 100));
		return results;
	}
	
	public ArrayList<Album> GetRecommendations_alg2b(Customer cust) {
		ArrayList<Album> results = new ArrayList<Album>();
		//results.add(new Album(97, "darkside of the moon2b", "pink floyd", "rock", 100));
		return results;
	}
	
	public ArrayList<Album> GetRecommendations_alg3(Customer cust) {
		ArrayList<Album> results = new ArrayList<Album>();
		//results.add(new Album(96, "darkside of the moon3", "pink floyd", "rock", 100));
		return results;
	}
	
	public ArrayList<Album> GetRecommendations_alg4() {
		ArrayList<Album> results = new ArrayList<Album>();
		//results.add(new Album(95, "darkside of the moon4", "pink floyd", "rock", 100));
		return results;
	}
	
	
	public Recommendation GetRecommendationsForUser(String userName, String emailAdddress) {
		/*
		List<String> results = new ArrayList<String>();
		results.add("username=" + userName);
		results.add("vivek khurana item1");
		results.add("hello there viekww");
		*/
		Customer user = db.GetCustomerByEmailAddress(emailAdddress);
		Recommendation reco = new Recommendation(user);
		
		//we now have the user from the database
		
		/*
		 * Recommendation ENGINE ALGORITHM 1 (based on artist) 
		 */
		
		
		
		return reco;
	}
	
	
	public void TestDBInsertUser(String fname, String lname, String nname, String email) {
		db.InsertIntoCustomerTable(fname, lname, nname, email);
	}
	
	public Customer GetCustomerByEmailAddress(String email) {
		return db.GetCustomerByEmailAddress(email);
	}
	
	public Customer GetCustomerByID(int id) {
		return db.GetCustomerByID(id);
	}
	
}
