package com.yao.controller.admin;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.yao.entity.Blog;
import com.yao.entity.BlogType;
import com.yao.entity.Blogger;
import com.yao.entity.Link;
import com.yao.service.BlogService;
import com.yao.service.BlogTypeService;
import com.yao.service.BloggerService;
import com.yao.service.LinkService;
import com.yao.service.impl.InitComponent;
import com.yao.util.ResponseUtil;

/**
 * ����ԱϵͳController��
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/admin/system")
public class SystemAdminController {
	
	@Resource
	private InitComponent initComponent;
	
	@Resource
	private BloggerService bloggerService;
	
	@Resource
	private LinkService linkService;
	
	@Resource
	private BlogTypeService blogTypeService;
	
	@Resource
	private BlogService blogService;
	
	/**
	 * ˢ��ϵͳ����
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/refreshSystem")
	public String refreshSystem(HttpServletRequest request,HttpServletResponse response)throws Exception{

//		ServletContext application=RequestContextUtils.getWebApplicationContext(request).getServletContext();
		ServletContext application=request.getServletContext();
		/*Blogger blogger=bloggerService.find(); // ��ȡ������Ϣ
		blogger.setPassword(null);
		application.setAttribute("blogger", blogger);
		
		List<Link> linkList=linkService.list(null); // ��ѯ���е�����������Ϣ
		application.setAttribute("linkList", linkList);
		
		List<BlogType> blogTypeCountList=blogTypeService.countList(); // ��ѯ��������Լ����͵�����
		application.setAttribute("blogTypeCountList", blogTypeCountList);
		
		List<Blog> blogCountList=blogService.countList(); // �������ڷ����ѯ����
		application.setAttribute("blogCountList", blogCountList);*/
		
		initComponent.fillApplication(application);

		JSONObject result=new JSONObject();
		result.put("success", true);
		ResponseUtil.write(response, result);
		return null;
	}
	
	
}
