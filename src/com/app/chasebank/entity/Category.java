package com.app.chasebank.entity;

public class Category {
	private String id, name, companyId;
	
	/**
	 * Constructor 
	 * 
	 * @param id The category Id
	 * @param name The name of the category
	 * @param companyId The company id
	 * @param createdAt Time and date this object was created
	 * @param updatedAt Time and date this object was updated
	 * 
	 * @return this
	 */
	public Category(String id, String name, String companyId) {
		this.id = id;
		this.name = name;
		this.companyId = companyId;
	}
	
	public Category() {
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

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
}
