package uRen;

public abstract class SessionSettings {
	public static String ConnectorString = "com.mysql.jdbc.Driver";
	public static String ServerUrl = "jdbc:mysql://localhost:3306/";
//	public static String Database = "tempDB";
//	public static String Username = "aero9";
//	public static String Password = "password";
	
	public static String Database = "uRen";
	public static String Username = "dbAccessor";
	public static String Password = "dbpasswd";
	
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
	
}
