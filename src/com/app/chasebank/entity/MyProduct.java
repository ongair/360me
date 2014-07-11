package com.app.chasebank.entity;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class MyProduct implements Serializable{
	private static final long serialVersionUID = 8910173195871661262L;
	private String id;
	private String description, name;
	private int logo;
	private String url;
	
	public MyProduct(String name, String description, int logo) {
		this.name = name;
		this.description = description;
		this.logo = logo;
	}
	
	public MyProduct(String id, String name, String description, String url) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.setUrl(url);
	}
	
	public MyProduct() {		
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		return "MyProduct: {Id:"+id+" , Name:"+name+" , Description:"+description+", ImageUrl:"+url+" }";
	}
	
	
}
