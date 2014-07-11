package com.app.chasebank.entity;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.drawable.Drawable;


public class MyContact implements Serializable{
	private static final long serialVersionUID = 8195706554682222586L;
	private String name, phone;
	private ArrayList<String> emailArray = new ArrayList<String>();
	
	/**
	 * Class consttructor
	 * @param name The name of the Contact
	 * @param phone The phone Number associated with the contact
	 */
	public MyContact(String name, String phone) {
		this.name = name;
		this.phone = phone;
	}
	
	public MyContact() {		
	}
	
	/**
	 * Set the name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Return the name of the contact
	 * @return {@link #name(String) }
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the phone number
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	/**
	 * Return the phone Number of the contact
	 * @return {@link #phone(String)}
	 */
	public String getPhone() {
		return phone;
	}
	
	@Override
	public String toString() {
		return "Contact:{name="+name+", phone="+phone+"}";
	}

	public ArrayList<String> getEmail() {
		return emailArray;
	}

	public void setEmail(ArrayList<String> emailArray) {
		this.emailArray = emailArray;
	}
	
}
