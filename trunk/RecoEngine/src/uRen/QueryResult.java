package uRen;

import com.restfb.Facebook;

public class QueryResult {

	@Facebook
	  String uid;

	  @Facebook
	  String name;
	  
	  @Facebook
	  String access_token;
	  
	  @Facebook
	  int idx;

	  @Facebook
	  String Artists[];
	  
	  @Override
	  public String toString() {
	    return String.format("%s - %s", uid, name); 
	  }
	
	  
	  public QueryResult()
	  {
		  this.Artists = new String [512];
	  }
}