package uRen;

public class Music {

	
	public String Category;
	public String Name;
	
	Music(String Category, String Name)
	{
		this.Category=Category;
		this.Name=Name;
	}
	
	Music()
	{
		this.Category="";
		this.Name="";
	}
	
	public Music getMusic() {
		return this;
	}
	
	public String getCategory() {
		return this.Category;
	}
	
	public String getName() {
		return this.Name;
	}
}