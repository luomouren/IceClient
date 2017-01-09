package com.weisi.Client.bean;

import java.io.Serializable;

//client_user_info
public class ClientUserInfo implements Serializable{
	private static final long serialVersionUID = -2682305557890221059L;
	private int id;
	private String userName;
	private String password;
	private String createTime;
	private Boolean isEnable;
	private String projectName;
	
	public ClientUserInfo() {
		super();
	}

    public int getId() {
      return id;
    }
  
    public void setId(int id) {
      this.id = id;
    }
  
    public String getUserName() {
      return userName;
    }
  
    public void setUserName(String userName) {
      this.userName = userName;
    }
  
    public String getPassword() {
      return password;
    }
  
    public void setPassword(String password) {
      this.password = password;
    }
  
    public String getCreateTime() {
      return createTime;
    }
  
    public void setCreateTime(String createTime) {
      this.createTime = createTime;
    }
  
    public Boolean getIsEnable() {
      return isEnable;
    }
  
    public void setIsEnable(Boolean isEnable) {
      this.isEnable = isEnable;
    }
  
    public String getProjectName() {
      return projectName;
    }
  
    public void setProjectName(String projectName) {
      this.projectName = projectName;
    }
  
    public ClientUserInfo(int id, String userName, String password, String createTime, Boolean isEnable,
        String projectName) {
      super();
      this.id = id;
      this.userName = userName;
      this.password = password;
      this.createTime = createTime;
      this.isEnable = isEnable;
      this.projectName = projectName;
    }
    
}
