package uRen;

import java.util.ArrayList;

public class Purchase {

	private Customer cust;
	private ArrayList<Album> purchases;
	
	public Purchase(Customer cust, ArrayList<Album> purchases) {
		this.cust = cust;
		this.purchases = purchases;
	}

	public Customer getCust() {
		return cust;
	}

	public void setCust(Customer cust) {
		this.cust = cust;
	}

	public ArrayList<Album> getPurchases() {
		return purchases;
	}

	public void setPurchases(ArrayList<Album> purchases) {
		this.purchases = purchases;
	}
	
	
}
