package com.weisi.Client.service.user;

import java.util.List;

import com.weisi.Client.bean.ClientUserInfo;

public interface UserService {
	void save(ClientUserInfo user) ;
	boolean update(ClientUserInfo user);
	boolean delete(int id);
	ClientUserInfo findById(int id);
	List<ClientUserInfo> findAll();
	ClientUserInfo findByUserNamePwd(String userName,String pwd);
}
