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
	
	private ArrayList<Album> purchased;
	
	public Recommendation() {
		
	}
	
	public Recommendation (Customer cust) {
		user = new Customer(cust);
		recommendations = new ArrayList<Album>();
		purchased = new ArrayList<Album>();
	}
	
	public void AddAlbumsPurchased(ArrayList<Album> purchased) {
		for(Album album : purchased) {
			this.purchased.add(album);
		}
	}
	
	//this is the only function used to add recommendations...
	public void AddRecommendationList(ArrayList<Album> list, int weight) {
		for (Album alb : list) {
			AddRecommendation(alb, weight); //this will ensure, we dont have duplicates!
		}
	}
	
	public void AddRecommendation(Album alb, int weight) {
		
		//check if he's already bought it...
		for (Album album : purchased) {
			if (album.getID() == alb.getID()) {
				return; //stop! he's already bought this one, no point inserting it
			}
		}
		
		//check if he's already been recommended it
		boolean alreadyExists = false;
		for (Album album : recommendations) {
			if (album.getID() == alb.getID()) {
				
				//if the album already exists, increment its weight
				int albumWeight = alb.getRecommendationWeight();
				albumWeight += weight;
				alb.setRecommendationWeight(albumWeight);
				
				alreadyExists = true;
				break; //stop searching
			}
		}
		
		if (false == alreadyExists) {
			Album recommendation = new Album(alb);
			recommendation.setRecommendationWeight(weight);
			recommendations.add(
							recommendation //end new Album
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
