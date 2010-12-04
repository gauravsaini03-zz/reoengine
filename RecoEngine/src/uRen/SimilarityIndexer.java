package uRen;

import java.util.ArrayList;


//this class massages the artist, genre and album similarity of people to come up with a
//an idea on who the most similar customers are
public class SimilarityIndexer {
	
	int topResultCount = 0;
	int fulfilledCount = 0;
	
	int minWeight = -1;
	int indexPtr = 0;
	
	//im making my own dictionary here... that's because i need random add/remove
	Purchase[] 	topPurchases = null;
	int[] 		topWeights = null;
	
	public SimilarityIndexer(int topCount) {
		
		topResultCount = topCount;
		fulfilledCount = 0;
		
		if (topCount <= 0) {
			Logger.Log("cant be negative or zero, using 1 instead");
			topResultCount = 1;
		}
		
		topPurchases = new Purchase[topCount];
		topWeights = new int[topCount];
		
		//init
		for (int i = 0; i < topCount; i++) {
			topPurchases[i] = null;
			topWeights[i] = -1;
		}
		
	}

	public void AddCandidatePurchase(
			Purchase newCandidate,
			int artistSimilarity,
			int genreSimilarity,
			int albumSimilarity) 
	{
		int weight 	= 	artistSimilarity * SessionSettings.ArtistWeight +
						genreSimilarity * SessionSettings.GenreWeight +
						albumSimilarity * SessionSettings.AlbumWeight;
		
		
		//the first topResultCount times, just insert them in the internal index
		if (fulfilledCount < topResultCount) {
			topWeights[indexPtr] = weight;
			topPurchases[indexPtr] = newCandidate;
			
			indexPtr++;
			fulfilledCount++;
			return;
		}
		
		//if we have a base index ready, we need to search for the smallest weight, and replace it
		int smallestWeight = topWeights[0]; //assume the first one is the smallest
		int smallestIndex = 0; //part of the same assumption
		
		//do the search
		for (int i=0; i<topResultCount; i++) {
			if (topWeights[i]<smallestWeight) {
				//found something smaller... record it's location
				smallestIndex = i;
				smallestWeight = topWeights[i];
			}
		}//end search
		
		if (weight < smallestWeight) return; //the weight is smaller than the smallest, its irrelevant
		
		//save the new guy
		topWeights[smallestIndex] = weight;
		topPurchases[smallestIndex] = newCandidate;
	}
	
	public ArrayList<Album> GetAllAlbumsFromHighestSimilarities() {
		ArrayList<Album> results = new ArrayList<Album>();
		
		for (int i=0; i<fulfilledCount; i++) {
			for(Album alb : topPurchases[i].getPurchases()) {
				results.add(alb);
			}
		}
		
		return results;
	}

}
