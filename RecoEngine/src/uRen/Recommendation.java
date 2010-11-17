package uRen;

import java.io.Serializable;
import java.util.ArrayList;

public class Recommendation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2389359431156438840L;
	
	private Customer user;
	private ArrayList<Album> recommendations;
	
	public Recommendation() {
		
	}
	
	public Recommendation (Customer cust) {
		user = new Customer(cust);
		recommendations = new ArrayList<Album>();		
	}
	
	public void AddRecommendationList(ArrayList<Album> list) {
		for (Album alb : list) {
			AddRecommendation(alb); //this will ensure, we dont have duplicates!
		}
	}
	
	public void AddRecommendation(Album alb) {
		
		boolean alreadyExists = false;
		for (Album album : recommendations) {
			if (album.getID() == alb.getID()) {
				alreadyExists = true;
				break; //stop searching
			}
		}
		
		if (false == alreadyExists) {
			recommendations.add(
							new Album(alb) //end new Album
							); //end add
		}
	}
	
	public Customer GetCustomer() {
		return user;
	}
	
	public ArrayList<Album> GetRecommendedAlbums() {
		return recommendations;
	}

	public Customer getUser() {
		return user;
	}

	public void setUser(Customer user) {
		this.user = user;
	}

	public ArrayList<Album> getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(ArrayList<Album> recommendations) {
		this.recommendations = recommendations;
	}
	
}
