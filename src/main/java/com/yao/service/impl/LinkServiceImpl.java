package com.yao.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yao.dao.LinkDao;
import com.yao.entity.Link;
import com.yao.service.LinkService;


@Service("linkService")
public class LinkServiceImpl implements LinkService{

	@Resource
	private LinkDao linkDao;

	public List<Link> list(Map<String, Object> map) {
		return linkDao.list(map);
	}

	public Long getTotal(Map<String, Object> map) {
		return linkDao.getTotal(map);
	}

	public Integer add(Link link) {
		return linkDao.add(link);
	}

	public Integer update(Link link) {
		return linkDao.update(link);
	}

	public Integer delete(Integer id) {
		return linkDao.delete(id);
	}

	
	
	

}
