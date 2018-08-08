package com.yao.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.yao.entity.Receiver;
import com.yao.entity.Subscriber;
import com.yao.service.SubscriberService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yao.entity.Blog;
import com.yao.entity.PageBean;
import com.yao.lucene.BlogIndex;
import com.yao.service.BlogService;
import com.yao.util.ResponseUtil;
import com.yao.util.StringUtil;


@Controller
@RequestMapping("/admin/blog")
public class BlogAdminController {

	@Autowired
	RabbitTemplate reRabbitTemplate;

	@Resource
	private SubscriberService subscriberService;

	@Resource
	private BlogService blogService;
	
	private BlogIndex blogIndex=new BlogIndex();
	

	@RequestMapping("/save")
	public String save(Blog blog, HttpServletResponse response, HttpServletRequest request)throws Exception{
		int resultTotal=0; 
		if(blog.getId()==null){
			resultTotal=blogService.add(blog);
			System.out.println(blog);
			blogIndex.addIndex(blog);
			//这时添加进去了，应该给那些订阅者发送邮件！
			List<Subscriber> subscriberList = subscriberService.getAll();
			for(Subscriber subscriber : subscriberList){
				subscriber.setFrom("博客网站管理员");
				subscriber.setSubject("网站发布了新博客");
				String href="http://120.78.210.140/Blog/blog/articles/"+blog.getId()+".html";
				subscriber.setContent("<p>亲爱的"+subscriber.getName()+"您好！本网站发布了新博客，标题为："+blog.getTitle()+"，关键字为 " + blog.getKeyWord()+"</p><p>超链接为："+href+"</p>");
			}

			reRabbitTemplate.convertAndSend("exchange.direct", "subscriber", subscriberList);
			System.out.println("向队列中发送消息了");


		}else{
			resultTotal=blogService.update(blog);
			blogIndex.updateIndex(blog);
		}
		JSONObject result=new JSONObject();
		if(resultTotal>0){
			result.put("success", true);
		}else{
			result.put("success", false);
		}
		ResponseUtil.write(response, result);
		return null;
	}
	
	/**
	 * ��ҳ��ѯ������Ϣ
	 * @param page
	 * @param rows
	 * @param s_blog
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/list")
	public String list(@RequestParam(value="page",required=false)String page,@RequestParam(value="rows",required=false)String rows,Blog s_blog,HttpServletResponse response)throws Exception{
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("title", StringUtil.formatLike(s_blog.getTitle()));
		map.put("start", pageBean.getStart());
		map.put("size", pageBean.getPageSize());
		List<Blog> blogList=blogService.list(map);
		Long total=blogService.getTotal(map);
		JSONObject result=new JSONObject();
		JsonConfig jsonConfig=new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		JSONArray jsonArray=JSONArray.fromObject(blogList, jsonConfig);
		result.put("rows", jsonArray);
		result.put("total", total);
		ResponseUtil.write(response, result);
		return null;
	}
	
	/**
	 * ������Ϣɾ��
	 * @param ids
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/delete")
	public String delete(@RequestParam(value="ids",required=false)String ids,HttpServletResponse response)throws Exception{
		String []idsStr=ids.split(",");
		for(int i=0;i<idsStr.length;i++){
			blogService.delete(Integer.parseInt(idsStr[i]));
			blogIndex.deleteIndex(idsStr[i]);
		}
		JSONObject result=new JSONObject();
		result.put("success", true);
		ResponseUtil.write(response, result);
		return null;
	}
	
	/**
	 * ͨ��Id����ʵ��
	 * @param id
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/findById")
	public String findById(@RequestParam(value="id")String id,HttpServletResponse response)throws Exception{
		Blog blog=blogService.findById(Integer.parseInt(id));
		JSONObject result=JSONObject.fromObject(blog);
		ResponseUtil.write(response, result);
		return null;
	}
}
