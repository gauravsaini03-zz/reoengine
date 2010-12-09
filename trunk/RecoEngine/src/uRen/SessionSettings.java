package uRen;

import java.io.BufferedReader;
import java.io.FileReader;

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
  	public static int MaxGigjunkieResults = 100;
  	
  	/*
  	 * Popularity Weights
  	 */
  	public static int DefaultGlobalPopularityWeight = 2;
  	public static int DefaultSocialPopularityWeight = 5;
  	public static int DefaultPersonalPopularityWeight = 3;
  	
  	/*
  	 * Debug Consts
  	 */
  	public static boolean DumpDebugData = true;
  	public static boolean EvaluationMode = true;
  	public static String EvalResultsFile = "c:\\temp\\evalResults.txt";
  	
  	/*
  	 * Param Names
  	 */
  	public static String ParamUserame = "username";
  	public static String ParamGenre = "genre";
  	public static String ParamArtist = "artist";
  	public static String ParamAlbumID = "albumid";
  	
  	/*
  	 * FACEBOOK STUFF
  	 */
  	//Me
  	public static String AccessTokenUserBuzz = "2227470867|2.3ZY_9XSVeugSaSrz7AVyUg__.3600.1291885200-661520015|c5UySD5DZVqbSitUx6qhayDJXco";
  	//Gaurav
  	public static String AccessTokenUserGaurav = "2227470867|2.BRyhqhINuKXIbPAzqyCX5w__.3600.1291881600-516264396|AX03MBySPGnjWsM4DYMu42t8KYM";
  	//Shweta
  	public static String AccessTokenUserShweta = "2227470867|2.mkEHaK9hSMMXzHu5cHIddQ__.3600.1291885200-100000236469693|zKv28jMf0Lrj-w1Td6zuOlQhZds";
  	//buzz
  	public static String AccessTokenMe = "2227470867|2.Efg04dxVlZCvE2TuKhACKA__.3600.1291885200-100001893684666|mFof8LNxx6nxUdmSc0Kr-o4fcQ8";
  	
  	public static String UIDBuzz = "661520015";
  	public static String UIDGaurav = "516264396";
  	public static String UIDShweta = "100000236469693";
  	public static String UIDVivek = "100001893684666";
  	
  	public static String FileTokenVivek = "C:\\Temp\\AccessTokens\\AccessTokenVivek.txt";
  	public static String FileTokenBuzz = "C:\\Temp\\AccessTokens\\AccessTokenBuzz.txt";
  	public static String FileTokenGaurav = "C:\\Temp\\AccessTokens\\AccessTokenGaurav.txt";
  	public static String FileTokenShweta = "C:\\Temp\\AccessTokens\\AccessTokenShweta.txt";
  	
    public static String readFileAsString(String filePath)
    {
        StringBuffer fileData = new StringBuffer(2000);
        try {
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        }
        catch (Exception ex) {
        	fileData.append("NOT FOUND!");
        }
        return fileData.toString();
    }

  	
}
