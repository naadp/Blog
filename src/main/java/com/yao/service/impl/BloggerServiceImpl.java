package com.yao.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yao.dao.BloggerDao;
import com.yao.entity.Blogger;
import com.yao.service.BloggerService;


@Service("bloggerService")
public class BloggerServiceImpl implements BloggerService{

	@Resource
	private BloggerDao bloggerDao;
	
	public Blogger getByUserName(String userName) {
		return bloggerDao.getByUserName(userName);
	}

	public Blogger find() {
		return bloggerDao.find();
	}

	public Integer update(Blogger blogger) {
		return bloggerDao.update(blogger);
	}

}
