package uRen;

import java.io.Serializable;

public class Album implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 3039163547024589861L;
/*
CREATE TABLE Album (
  	idAlbum INTEGER UNSIGNED  NOT NULL   AUTO_INCREMENT,
  	albumName VARCHAR(255)  NULL  ,
  	artistName VARCHAR(255)  NULL  ,
  	genre VARCHAR(45)  NULL  ,
  	price INTEGER UNSIGNED  NULL    ,
	PRIMARY KEY(idAlbum)); 
*/
		
	private int idAlbum;
	private String albumName;
	private String artistName;
	private String genre;
	private int price;
	
	/*
	 * Recommendation Weights
	 */
	private int recommendationWeight;
	
	
	public Album() {
		idAlbum = -1;
		albumName = "";
		artistName = "";
		this.genre = "";
		this.price = -1;
		
		recommendationWeight = -1;
	}
	
	public Album(int id, String album, String artist, String genre, int price) {
		idAlbum = id;
		albumName = album;
		artistName = artist;
		this.genre = genre;
		this.price = price;
		
		recommendationWeight = -1;
	}
	
	public Album(Album alb) {
		idAlbum = alb.getID();
		albumName = alb.getAlbumName();
		artistName = alb.getArtistName();
		this.genre = alb.getGenre();
		this.price = alb.getPrice();
		
		recommendationWeight = alb.getRecommendationWeight();
	}
	
	public void Clone(Album alb) {
		idAlbum = alb.getID();
		albumName = alb.getAlbumName();
		artistName = alb.getArtistName();
		this.genre = alb.getGenre();
		this.price = alb.getPrice();
		
		recommendationWeight = alb.getRecommendationWeight();
	}
	
	public int getID() {
		return idAlbum;
	}
	public void setID(int id) {
		idAlbum = id;
	}
	
	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String name) {
		albumName = name;
	}
	

	
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}

	public int getRecommendationWeight() {
		return recommendationWeight;
	}

	public void setRecommendationWeight(int recommendationWeight) {
		this.recommendationWeight = recommendationWeight;
	}
	

	
}
