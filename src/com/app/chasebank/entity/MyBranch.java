package com.app.chasebank.entity;

import java.io.Serializable;

public class MyBranch implements Serializable{
	private static final long serialVersionUID = 1870293472822833051L;
	private String id;
	private String name, company, description;
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param company
	 * @param description
	 */
	public MyBranch(String id, String name, String company, String description) {
		this.id = id;
		this.setName(name);
		this.setCompany(company);
		this.setDescription(description);
	}
	
	/**
	 * Constructor
	 * @param id Branch ID 
	 * @param name Branch Name
	 * @param company Company associated with the Branch
	 */
	public MyBranch(String id, String name, String company) {
		this.setId(id);
		this.setName(name);
		this.setCompany(company);
		this.setDescription(null);
	}
	
	/**
	 * Constructor
	 * @param name Branch Name
	 * @param company Company Name
	 */
	public MyBranch(String name, String company) {
		this.setId(null);
		this.setName(name);
		this.setCompany(company);
		this.setDescription(null);
	}
	
	/**
	 * Empty Constructor 
	 */
	public MyBranch() {
	}
	
	public String getId() {
		return this.id;
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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "Branch:{id: "+id+" ,name:  "+name+",company:  "+company + ",desc:  "+description+"}";
	}

}
