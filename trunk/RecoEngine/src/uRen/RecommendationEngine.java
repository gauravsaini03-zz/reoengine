package uRen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookException;
import com.restfb.FacebookJsonMappingException;
import com.restfb.types.CategorizedFacebookType;
import com.restfb.types.User;




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
		//Purchase list = db.GetAlbumsPurchasedByCustomer(cust);
		ArrayList<Purchase> recoPool = db.GetPurchasesByArtist(cust.getIdCustomer(), artist);
		//get a list of all customers that have purchased an album by "artist" excluding the current customer
		//in that list of customers, find the largest common subset... [find everything common to all of them!?]

		return DoT3hRecommendationMagic(recoPool);
		//return list.getPurchases();
	}

	public ArrayList<Album> GetRecommendations_alg2a(Customer cust, String genre) {
		ArrayList<Purchase> recoPool = db.GetPurchasesByGenre(cust.getIdCustomer(), genre);
		return DoT3hRecommendationMagic(recoPool);
	}

	@SuppressWarnings("rawtypes")
	public ArrayList<Album> GetRecommendations_alg2b(Customer cust) {
		//get the most popular genre for this guy

		HashMap<String, Integer> genreRanks = new HashMap<String, Integer>();
		Purchase purchases = db.GetAlbumsPurchasedByCustomer(cust);

		for(Album alb : purchases.getPurchases()) {
			if (genreRanks.containsKey(alb.getGenre())) {
				Integer frequency = genreRanks.get(alb.getGenre());
				frequency++;
				genreRanks.put(alb.getGenre(), frequency);
			}
			else {
				genreRanks.put(alb.getGenre(), new Integer(0));
			}
		}
		//genre ranks now contains his genre list... find the highest number!
		String genreSelected = "";
		Integer maxFreq=new Integer(-1);
		Iterator it = genreRanks.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			if ( ((Integer)pairs.getValue()) > maxFreq) {
				genreSelected = (String) pairs.getKey();
				maxFreq = (Integer) pairs.getValue();
			}
		}//finished search
		Logger.Log("Heuristically selected genre = " + genreSelected);
		return GetRecommendations_alg2a(cust, genreSelected);
	}

	public ArrayList<Album> GetRecommendations_alg3(Customer cust) {
		ArrayList<Album> results = new ArrayList<Album>();
		String [] artist = new String[10];//currently has space for only 10 artists from the API query 
		artist=GetArtistsFromGigJunkie();	//gets an array of artist names

		for (int i=0;i<artist.length;i++ )
		{//for each artist get purchased albums by this artist, sorted desc by popularity.
			ArrayList<Purchase> recoPool = db.GetPurchasesByArtist(cust.getIdCustomer(), artist[i]);
			//results.addAll(db.GetAlbumsByArtist(artist[i]));
			//make a common result array for all artists
			results.addAll(DoT3hRecommendationMagic(recoPool)) ;
		}

		return results;
	}

	public ArrayList<Album> GetRecommendations_alg5(Customer cust) {
		//ArrayList<Album> results = new ArrayList<Album>();
		//results.add(new Album(99, "darkside of the moon", "pink floyd", "rock", 100));

		String Artist = fbinterests();
		if (Artist == null) {
			Logger.Log("FACEBOOK ALGO NOT WORKING!!!");
			return null;
		}
		
		ArrayList<Album> results = new ArrayList<Album>();

		results = db.GetAlbumsByArtist(Artist);	//gets an array of albumnames

		return results;

	}

	public ArrayList<Album> GetRecommendations_alg4(Customer cust) {
		//get purchases by cutomer
		Purchase customerPurchase = db.GetAlbumsPurchasedByCustomer(cust);
		//get purchase table (everyone except customer)
		ArrayList<Purchase> recoPool = db.GetAllPurchases(cust.getIdCustomer());
		//run the algorithm
		return SimilarityMatcher(customerPurchase, recoPool);
	}

	private ArrayList<Album> SimilarityMatcher(Purchase custPurchase, ArrayList<Purchase> purchasePool) {
		SimilarityIndexer indexer = new SimilarityIndexer(SessionSettings.SimilarUsersToHunt);

		for(Purchase candidate : purchasePool) {
			if (candidate == null) continue;
			int albumSimilarity = 0;
			int genreSimilarity = 0;
			int artistSimilarity = 0;
			Logger.Log(candidate.getCust().getEmailAddress());
			for (Album custAlbum : custPurchase.getPurchases()) {
				//check for this album in the candidate's list
				for (Album candAlbum : candidate.getPurchases()) {
					if (custAlbum.getID() == candAlbum.getID() ) albumSimilarity++;
					if (custAlbum.getGenre().compareToIgnoreCase(candAlbum.getGenre())==0 ) genreSimilarity++;
					if (custAlbum.getArtistName().compareToIgnoreCase(candAlbum.getArtistName())==0 ) artistSimilarity++;
				}//end search
			}//end similarity matching

			//record albumSimilarity, genreSimilarity, artistSimilarity
			if (albumSimilarity != 0 ||
					genreSimilarity != 0 ||
					artistSimilarity != 0) {
				indexer.AddCandidatePurchase(candidate, artistSimilarity, genreSimilarity, albumSimilarity);
			}
		}

		return indexer.GetAllAlbumsFromHighestSimilarities();
	}

	//now for fun stuff! :)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList<Album> DoT3hRecommendationMagic(ArrayList<Purchase> data) {
		ArrayList<Album> recommendationPool = new ArrayList<Album>();

		ArrayList<ArrayList<Album>> sourcePool = new ArrayList<ArrayList<Album>>();
		for (Purchase purchase : data) {
			if (purchase != null) {
				sourcePool.add(purchase.getPurchases());
			}
		}

		//im gonna try to do this in O(n*m) first, and then see where i can improve... later
		//for class projects -> [first]make it work. [second]make it elegant. [third]make it fast.

		//i have 2 strategies in mind...
		//first is more efficient but may not be as accurate in its recommendations
		//second is inefficient but its recommendations will be more accurate
		//im implementing both, and commenting out the inefficient one.
		//maybe i can do some runtime switching based on some logic ? i dono... first [quick] one for now

		HashMap<Integer, Integer> ranks = new HashMap<Integer, Integer>(); //STUPID java does not support a hashmap with primitives!!!

		/*
		 * 
		 * STRATEGY 1: rank all the albums according to popularity...
		 * 
		 */
		for (int outer = 0; outer < sourcePool.size(); outer++) {
			ArrayList<Album> listToCompare = sourcePool.get(outer);
			for (Album album : listToCompare) {
				//found.. increase the rank for album.getID() by 1
				Integer AlbumID = new Integer(album.getID()); //STUPID java
				if (ranks.containsKey(AlbumID)) {
					Integer frequency = ranks.get(AlbumID);
					frequency++;
					ranks.put(AlbumID, frequency);
				}
				else {
					ranks.put(AlbumID, new Integer(0));
				}
			} //end for each album
		} //end for each list

		/*
		 * 
		 * STRATEGY 2: rank only the common albums according to popularity
		 * 
		 */
		/*
		for (int outer = 0; outer < sourcePool.size(); outer++) {
			ArrayList<Album> listToCompare = sourcePool.get(outer);
			for (Album album : listToCompare) {
				//check if this album is in any of the remaining lists...
				for (int inner = 0; inner < sourcePool.size(); inner++) {
					if (inner != outer) { //skip the current list (it will obviously exist!)
						ArrayList<Album> targetList = sourcePool.get(inner);
						for (Album targetAlbum : targetList) {
							if (album.getID() == targetAlbum.getID()) {
								//found.. increase the rank for album.getID() by 1
								Integer AlbumID = new Integer(album.getID()); //STUPID java
								if (ranks.containsKey(AlbumID)) {
									Integer frequency = ranks.get(AlbumID);
									frequency++;
									ranks.put(AlbumID, frequency);
								}
								else {
									ranks.put(AlbumID, 0);
								}
								break; //stop searching
							} // end if found
						} // end for each album in the inner list
					} //end if
				} // end for inner
			} // end for each album in outer list
		} // end for outer
		 */

		//at this point 'ranks' has all the information i need... i just need to pick the highest [or lowest ;)] X ranks,
		//and that, ladies and gentlemen, is THE recommendation pool! :D
		Logger.Log("hh");
		//sort the hashmap based on values
		ArrayList sortedArrayList = new ArrayList( ranks.entrySet() );  

		Collections.sort( sortedArrayList , new Comparator() {  
			public int compare( Object o1 , Object o2 )  
			{  
				Map.Entry e1 = (Map.Entry)o1 ;  
				Map.Entry e2 = (Map.Entry)o2 ;  
				Integer first = (Integer)e1.getValue();  
				Integer second = (Integer)e2.getValue();  
				return second.compareTo( first );  
			}  
		});

		LinkedHashMap<Integer, Integer> sortedRanks = new LinkedHashMap<Integer, Integer>();
		Iterator i = sortedArrayList.iterator();  
		while ( i.hasNext() )  
		{  
			//System.out.println( (Map.Entry)i.next() );
			Map.Entry entry = (Map.Entry)i.next();
			sortedRanks.put((Integer)entry.getKey(), (Integer)entry.getValue());
		}

		//now just search for the best ranked chaps and return them :)
		int forgiveness = SessionSettings.SonnyCorleone; 
		int oldFreq = -1;
		for(Integer key : sortedRanks.keySet()) {

			Album album = db.GetAlbumByID(key.intValue());
			int freq = sortedRanks.get(key).intValue();

			if (freq != oldFreq) {
				oldFreq = freq;
				forgiveness--;
				if (forgiveness < 0) break; //im done with recommendations
			}

			Logger.Log( album.getArtistName() + ", " +
					album.getAlbumName() + " - "+ freq);

			recommendationPool.add(album);
		}//end for

		return recommendationPool;
	}

	private String[] GetArtistsFromGigJunkie(){
		String head_artist[]= new String[SessionSettings.MaxGigjunkieResults];
		try 
		{
			URL url_str = new URL (SessionSettings.GigjukieURL);		
			//http://api.bandsintown.com/events/search.json?location=Atlanta,GA&radius=1&date=2010-11-23,2010-11-23&app_id=shwetapatira_gatech"

			BufferedReader in = new BufferedReader(new InputStreamReader(url_str.openStream()));
			//http://api.bandsintown.com/events/search.json?location=Atlanta,GA&radius=1&date=2010-11-23,2010-11-23&app_id=shwetapatira_gatech

			String str;
			StringBuilder builder = new StringBuilder();

			while((str = in.readLine())!=null)
				builder.append(str);

			Logger.Log("The size of the buffer is " + builder.length() );
			String result = builder.toString();
			Logger.Log("This is the result" + result );
			result = result.substring(result.indexOf('[',0));

			JSONArray my_res = new JSONArray(result);

			Logger.Log("The size of the array is " + my_res.length() );


			for(int i=0; i<my_res.length(); i++)
			{
				JSONObject entry = my_res.getJSONObject(i); 
				// String str_part = my_res.getString(i);
				head_artist[i]= entry.getJSONObject("Artists").getJSONArray("Artists").getJSONObject(0).getString("Name");
				//       int resultCount = json.getJSONObject("").getJSONObject("cursor").getInt("estimatedResu
				//  ltCount");
				Logger.Log("This is each element of the array" + head_artist);
			}
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return head_artist;	
	}



	public String fbinterests()
	{
		String [] artist = new String[10];
		FacebookClient facebookClient = new DefaultFacebookClient(SessionSettings.readFileAsString(SessionSettings.FileTokenVivek));        
		FacebookClient[] facebookClientlist = new DefaultFacebookClient[10] ;
		User user;int idx=0;

		/*
			//try getting access tokens dynamically

			try {
				URL url = new URL("https://graph.facebook.com/oauth/access_token?grant_type=authorization_code&client_id=162830627092777&client_secret=5851e94067fd91e54bc1667ff794f6dc");

			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		 */
		try {
			//Connection<Post> myFeed = facebookClient.fetchConnection("me/feed", Post.class);
			Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class);
			Connection <CategorizedFacebookType> myMusic = facebookClient.fetchConnection("me/music", CategorizedFacebookType.class);

			//get my music interests from fb
			for (int i =0; i < myMusic.getData().size(); i++)
			{	
				String Category = myMusic.getData().get(i).getCategory();
				String Name = myMusic.getData().get(i).getName();
				System.out.println("My category!!!"+Category+"  Name:"+Name);

				if (Category == "Musicians")//take only musicians not Genre n other such stuff
					artist[i] = Name;
			}

			String query = "SELECT uid, name FROM user WHERE uid=" + SessionSettings.UIDBuzz + " or uid=" + SessionSettings.UIDGaurav + " or uid =" + SessionSettings.UIDShweta;// or uid=661520015 //Get only vivek & gaurav 
			List<QueryResult> friends = facebookClient.executeQuery(query, QueryResult.class);
			System.out.println("Users: " + friends);

			Hashtable<String, Integer> pop_table = new Hashtable<String, Integer>();
			int i, length;

			for (QueryResult entry: friends)
			{	
				entry.idx=idx++;
				if (entry.uid.equals(SessionSettings.UIDBuzz))	
					entry.access_token =SessionSettings.readFileAsString(SessionSettings.FileTokenBuzz) ;
				if (entry.uid.equals(SessionSettings.UIDGaurav))	
					entry.access_token =SessionSettings.readFileAsString(SessionSettings.FileTokenGaurav) ;
				if (entry.uid.equals(SessionSettings.UIDShweta))	
					entry.access_token =SessionSettings.readFileAsString(SessionSettings.FileTokenShweta) ;

				/*  if (entry.uid.equals("516264396"))

				 *other friends .. this is very lame :P

				 */
				//use his access token to make a music query.
				facebookClientlist [entry.idx] = new DefaultFacebookClient(entry.access_token);
				String conn_str = entry.uid.concat("/music");
				Connection <CategorizedFacebookType> FMusic = facebookClientlist[entry.idx].fetchConnection(conn_str, CategorizedFacebookType.class);
				int count_artists=0;

				for (int f =0; f < FMusic.getData().size(); f++)
				{	
					String Category = FMusic.getData().get(f).getCategory();
					String Name = FMusic.getData().get(f).getName();
					System.out.println("My category!!!"+Category+"  Name:"+Name);

					if (Category.equalsIgnoreCase("Musicians"))// only get musicians.. No need for music-genres right now
					{
						count_artists++;
						entry.Artists[f] = Name;
						System.out.println("Artists:"+entry.Artists[f]);
					}
				}

				System.out.println("Length of array"+entry.Artists.length);

				for (int f=0; f< count_artists;f++)			//need a new loop coz we only want "Musicians" (gone into artists)
				{
					String hkey = entry.Artists[f];
					System.out.println("This is the value"+hkey);
					//Integer artist_cnt_hash = null;
					if (hkey == null) continue;
					if (pop_table.containsKey(hkey) == true) {
						Integer count = (Integer)pop_table.get(hkey);
						pop_table.put(hkey, new Integer (count.intValue()+1)); System.out.println("Putting count in key"+hkey);
					} else {
						System.out.println("Putting 1 in the key"+hkey);
						pop_table.put(hkey, new Integer(1));
					}
					/*
					artist_cnt_hash = (Integer)pop_table.get(hkey);

					if (artist_cnt_hash  == null)					//if artist not found, add one
					{System.out.println("Putting 1 in the key"+hkey);
					pop_table.put(hkey, new Integer(1)); }
					else 									//if found increment the value
					{pop_table.put(hkey, new Integer (artist_cnt_hash.intValue()+1)); System.out.println("Putting count in key"+hkey);}
					*/
				}//for artists array
			}//for each friend

			Enumeration keys = pop_table.keys();

			int max = 0;
			String bestArtist = new String();
			while (keys.hasMoreElements()) 
			{
				Object key = keys.nextElement();
				Integer count =(Integer) pop_table.get(key);
				if (count > max)
				{  max = count; 
				bestArtist = (String) key;
				}
				System.out.println(" Best until now :" + bestArtist + " count: "+count  );
			}

			System.out.println("Most popular artist is:"+bestArtist);


			//for (each friend) {for users}

			return (String)bestArtist;

			//..every user has an artists list now. stored in friends[].Artists

		} catch (FacebookJsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public ArrayList<Album> GetAllAlbums() {
		return db.GetAllAlbums();
	}

	public Purchase GetPurchasesByCustomer(Customer cust) {
		return db.GetAlbumsPurchasedByCustomer(cust);
	}
	
	public Album GetAlbumByID(int id) {
		return db.GetAlbumByID(id);
	}
	
	public boolean InsertIntoPurchaseTable(Customer cust, Album alb) {
		int resultRows = db.InserIntoPurchaseTable(alb.getID(), cust.getIdCustomer());
		return resultRows == -1 ? false: true;
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
