package com.app.chasebank.entity;

import java.io.Serializable;

public class Product implements Serializable{
	private String id, name, categoryId, createdAt, updatedAt;

	/**
	 * Constructor for the products
	 * 
	 * @param id The id of the Product
	 * @param name The product name
	 * @param categoryId The category of the product
	 * @param createdAt The Time and date the Product was created
	 * @param updatedAt The time and date the Product was updated.
	 * 
	 * @return {@link #Product}
	 */
	public Product(String id, String name, String categoryId, String createdAt, String updatedAt) {
		this.id = id;
		this.name = name;
		this.categoryId = categoryId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;		
	}
	
	/**
	 * Default Constructor
	 */
	public Product() {
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	
}
