package com.app.chasebank.entity;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class MyCompany implements Serializable{
	private static final long serialVersionUID = 8588075383912939L;
	private String description, name;
	private int logo = 0;
	private String image_url;
	private String id;
	
	/**
	 * Constructor
	 * @param name Company Name
	 * @param description The company description
	 * @param logo The company logo
	 */
	public MyCompany(String name, String description, int logo) {
		this.name = name;
		this.description = description;
		this.logo = logo;
	}
	
	/**
	 * Constructor
	 * @param id The companys id
	 * @param name The company's name
	 * @param description The description of the company
	 * @param image_url The image url of the logo
	 */
	public MyCompany(String id, String name, String description, String image_url) {
		this.setId(id);
		this.name = name;
		this.description = description;
		this.image_url = image_url;
	}
	
	/**
	 * Empty Constructor
	 */
	public MyCompany() {		
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getLogo() {
		return logo;
	}
	
	public void setLogo(int logo) {
		this.logo = logo;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	
	@Override
	public String toString() {
		return "Company:{id: "+id+" ,name:  "+name+",description:  "+description+",image_url:  "+image_url+"}";
	}
	
}
