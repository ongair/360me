package com.app.chasebank.entity;

import java.io.Serializable;

public class Lead implements Serializable{
	private static final long serialVersionUID = 6130863951417004409L;
	private String id, company, branch, product, name, phone, email, details;
	private String status;
	private String submitted_by, assigned_to, verified, value;
	
	/**
	 * Constructor for Lead
	 * 
	 * @param id Lead ID 
	 * @param company Company ID
	 * @param branch Branch ID
	 * @param product Product ID
	 * @param name Name 
	 * @param phone Phone Number
	 * @param email Email
	 * @param details Details
	 */
	public Lead(String id, String company, String branch, String product, String name, String phone, String email, String details) {
		this.setId(id);
		this.setCompany(company);
		this.setBranch(branch);
		this.setProduct(product);
		this.setName(name);
		this.setPhone(phone);
		this.setEmail(email);
		this.setDetails(details);		
	}
	
	public Lead() {
		
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

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * @return the branch
	 */
	public String getBranch() {
		return branch;
	}

	/**
	 * @param branch the branch to set
	 */
	public void setBranch(String branch) {
		this.branch = branch;
	}

	/**
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getSubmitted_by() {
		return submitted_by;
	}

	public void setSubmitted_by(String submitted_by) {
		this.submitted_by = submitted_by;
	}

	public String getAssigned_to() {
		return assigned_to;
	}

	public void setAssigned_to(String assigned_to) {
		this.assigned_to = assigned_to;
	}

	public String getVerified() {
		return verified;
	}

	public void setVerified(String verified) {
		this.verified = verified;
	}

	@Override
	public String toString() {
		return "Lead: {id: "+id+", company: "+company+", product: "+product+", name: "+name+", phone: "+phone+", email: "+email+", status: "+status+"}";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
