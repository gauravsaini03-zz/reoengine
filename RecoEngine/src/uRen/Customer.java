package uRen;

import java.io.Serializable;

public class Customer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8551253586491190539L;
	
	/*
	CREATE TABLE Customer (
	  	idCustomer INTEGER UNSIGNED  NOT NULL   AUTO_INCREMENT,
	  	firstName VARCHAR(45)  NULL  ,
	  	lastName VARCHAR(45)  NULL  ,
	  	nickName VARCHAR(20)  NULL  ,
	  	emailAddress VARCHAR(255)  NULL    ,
		PRIMARY KEY(idCustomer));
	 */
	
	private int idCustomer;
	private String firstName;
	private String lastName;
	private String nickName;
	private String emailAddress;
	
	//constructors
	public Customer() {
		idCustomer = -1;
		firstName = "";
		lastName = "";
		nickName = "";
		emailAddress = "";
	}
	
	public Customer(Customer cust) {
		idCustomer = cust.getIdCustomer();
		firstName = cust.getFirstName();
		lastName = cust.getLastName();
		nickName = cust.getNickName();
		emailAddress = cust.getEmailAddress();
	}
	
	public Customer(int id, String fname, String lname, String nick, String email) {
		idCustomer = id;
		firstName = fname;
		lastName = lname;
		nickName = nick;
		emailAddress = email;
	}
	
	public void Clone(Customer cust) {
		idCustomer = cust.getIdCustomer();
		firstName = cust.getFirstName();
		lastName = cust.getLastName();
		nickName = cust.getNickName();
		emailAddress = cust.getEmailAddress();
	}

	public int getIdCustomer() {
		return idCustomer;
	}

	public void setIdCustomer(int idCustomer) {
		this.idCustomer = idCustomer;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

}
