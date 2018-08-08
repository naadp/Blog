package com.yao.service.impl;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.yao.entity.Blog;
import com.yao.entity.BlogType;
import com.yao.entity.Blogger;
import com.yao.entity.Link;
import com.yao.service.BlogService;
import com.yao.service.BlogTypeService;
import com.yao.service.BloggerService;
import com.yao.service.LinkService;

@Component("initCom")
public class InitComponent implements ServletContextListener,ApplicationContextAware{

	private static ApplicationContext applicationContext;
	
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext application=sce.getServletContext();
		fillApplication(application);
	}
	
	public void fillApplication(ServletContext application){
//		ServletContext application=sce.getServletContext();
		BloggerService bloggerService=(BloggerService) applicationContext.getBean("bloggerService");
		Blogger blogger=bloggerService.find();
		blogger.setPassword(null);
		application.setAttribute("blogger", blogger);
		
		LinkService linkService=(LinkService) applicationContext.getBean("linkService");
		List<Link> linkList=linkService.list(null);
		application.setAttribute("linkList", linkList);
		
		BlogTypeService blogTypeService=(BlogTypeService) applicationContext.getBean("blogTypeService");
		List<BlogType> blogTypeCountList=blogTypeService.countList();
		application.setAttribute("blogTypeCountList", blogTypeCountList);
		
		BlogService blogService=(BlogService) applicationContext.getBean("blogService");
		List<Blog> blogCountList=blogService.countList();
		application.setAttribute("blogCountList", blogCountList);
		System.out.println(blogCountList);
	}

	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}

}
