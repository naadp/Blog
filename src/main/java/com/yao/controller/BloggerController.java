package com.yao.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yao.entity.Receiver;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.yao.entity.Blogger;
import com.yao.service.BloggerService;
import com.yao.util.CryptographyUtil;
import com.yao.util.DateUtil;
import com.yao.util.IPUtils;
/**
 * ����Controller��
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/blogger")
public class BloggerController {

	@Autowired
	RabbitTemplate reRabbitTemplate;


	@Resource
	private BloggerService bloggerService;

	@RequestMapping("/login")
	public String login(Blogger blogger,HttpServletRequest request,HttpServletResponse response){

		Subject subject=SecurityUtils.getSubject();
		UsernamePasswordToken token=new UsernamePasswordToken(blogger.getUserName(), CryptographyUtil.md5(blogger.getPassword(), CryptographyUtil.getSalt()));
		try{

			subject.login(token); // 登录验证
			String ip = IPUtils.getIpAddress(request);
			final String content = "IP为 "+ip+" 的用户在 "+DateUtil.getCurrentDateStr2()+" 登录了后台管理系统";
			reRabbitTemplate.convertAndSend("exchange.direct", "Blog", new Receiver(content));
			System.out.println("向队列中发送消息了");

			return "redirect:/admin/main.jsp";
		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("blogger", blogger);
			request.setAttribute("errorInfo", "用户名或者密码错误！");
			return "login";
		}
	}

	/**
	 * 关于博主
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/aboutMe")
	public ModelAndView aboutMe()throws Exception{
		ModelAndView mav=new ModelAndView();
		mav.addObject("pageTitle", "关于博主_java开源博客系统");
		mav.addObject("mainPage", "foreground/blogger/info.jsp");
		mav.setViewName("mainTemp");
		return mav;
	}
	
}
