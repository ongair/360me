package com.app.chasebank.entity;

public class LoginUser {
	private String userId, userEmail, userRole;
	private boolean loginStatus;
	private String setup;
	
	/**
	 * Constructor, mainly for successfull login requests
	 * 
	 * @param id User id
	 * @param email User email address
	 * @param role User Role
	 * @param status User Login status
	 * 
	 * @return this
	 */
	public LoginUser(String id, String email, String role, boolean status) {
		this.userId =  id;
		this.userEmail = email;
		this.userRole = role;
		this.loginStatus = status;
	}
	
	/**
	 * Constructor, mainly for failed login requests
	 * 
	 * @param loginStatus User login status
	 * @return this
	 */
	public LoginUser(boolean loginStatus) {
		this.userEmail = null;
		this.userId = null;
		this.userRole = null;
		this.loginStatus = loginStatus;
	}
	
	/**
	 * 
	 * @param user_id
	 * @param user_email
	 * @param user_role
	 * @param login
	 * @param setup
	 */
	public LoginUser(String id, String email, String role, boolean status, String setup) {
		this.userId =  id;
		this.userEmail = email;
		this.userRole = role;
		this.loginStatus = status;
		this.setSetup(setup);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public boolean isLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(boolean loginStatus) {
		this.loginStatus = loginStatus;
	}

	public String getSetup() {
		return setup;
	}
	
	/**
	 * Check if a users profile has been setup already
	 * @return boolean whether the profile for this user has been setup
	 */
	public boolean isSetup() {
		if (setup == null) {
			return false;
		}else if(setup.equals("true")){
			return true;
		}else if(setup.equalsIgnoreCase("false")) {
			return false;
		}else return false;
	}

	public void setSetup(String setup) {
		this.setup = setup;
	}
	
}
