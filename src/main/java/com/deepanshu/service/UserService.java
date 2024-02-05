package com.deepanshu.service;

import com.deepanshu.exception.UserException;
import com.deepanshu.modal.User;

public interface UserService {
	
	public User findUserById(Long userId) throws UserException;
	
	public User findUserProfileByJwt(String jwt) throws UserException;

}
