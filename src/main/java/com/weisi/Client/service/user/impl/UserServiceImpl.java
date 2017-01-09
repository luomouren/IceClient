package com.weisi.Client.service.user.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weisi.Client.bean.ClientUserInfo;
import com.weisi.Client.mapper.user.UserMapper;
import com.weisi.Client.service.user.UserService;

@Service
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class UserServiceImpl implements UserService {
	@Resource
	private UserMapper mapper;
	@Override
	public void save(ClientUserInfo user) {
		mapper.save(user);
	}

	@Override
	public boolean update(ClientUserInfo user) {
		return mapper.update(user);
	}

	@Override
	public boolean delete(int id) {
		return mapper.delete(id);
	}

	@Override
	public ClientUserInfo findById(int id) {
		return mapper.findById(id);
	}

	@Override
	public List<ClientUserInfo> findAll() {
		return mapper.findAll();
	}

    @Override
    public ClientUserInfo findByUserNamePwd(String userName, String pwd) {
      return mapper.findByUserNamePwd(userName,pwd);
    }

}
