package uRen;

public abstract class SessionSettings {
	public static String ConnectorString = "com.mysql.jdbc.Driver";
	public static String ServerUrl = "jdbc:mysql://localhost:3306/";
	public static String Database = "uRen";
	public static String Username = "dbAccessor";
	public static String Password = "dbpasswd";
	
	//recommendation settings
	public static int SonnyCorleone = 2;
	public static int SimilarUsersToHunt = 2;
	public static int AlbumWeight = 15;
	public static int ArtistWeight = 10;
	public static int GenreWeight = 5;
	
	//Customer DataBase
	public static String Customer_idCustomer = "idCustomer";
	public static String Customer_firstName = "firstName";
	public static String Customer_lastName = "lastName";
	public static String Customer_nickName = "nickName";
	public static String Customer_emailAddress = "emailAddress";
	
	//Album Database
  	public static String Album_idAlbum = "idAlbum";
  	public static String Album_albumName = "albumName";
  	public static String Album_artistName = "artistName";
  	public static String Album_genre = "genre";
  	public static String Album_price = "price";
  	
  	//Purchase Table
  	public static String Purchase_idPurchase = "idPurchase";
  	public static String Purchase_Customer_idCustomer = "Customer_idCustomer";
  	public static String Purchase_Album_idAlbum = "Album_idAlbum";
  	public static String Purchase_date = "date";
  	//Purchase table with the table join with Album
  	public static String Purchase_Join_idAlbum = "idAlbum";
  	public static String Purchase_Join_albumName = "albumName";
  	public static String Purchase_Join_artistName = "artistName";
  	public static String Purchase_Join_genre = "genre";
  	public static String Purchase_Join_price = "price";
  	
  	/*
  	 * GIGJUNKIE API CONSTANTS
  	 */
  	public static String GigjukieURL = "http://api.gigjunkie.net/v1.0/events?q=Atlanta&startDate=2010-11-27&endDate=2010-12-27&distance=20&consumerKey=00fc5a4e-94c1-4c57-b510-49651c68258b&format=JSON";
  	public static int MaxGigjunkieResults = 10;
  	
  	/*
  	 * Popularity Weights
  	 */
  	public static int DefaultGlobalPopularityWeight = 5;
  	public static int DefaultSocialPopularityWeight = 2;
  	public static int DefaultPersonalPopularityWeight = 3;
  	
  	/*
  	 * Debug Consts
  	 */
  	public static boolean DumpDebugData = true;
}
