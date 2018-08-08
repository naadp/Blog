package com.yao.controller.admin;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.yao.entity.Blogger;
import com.yao.service.BloggerService;
import com.yao.util.CryptographyUtil;
import com.yao.util.DateUtil;
import com.yao.util.ResponseUtil;

/**
 * ����Ա����Controller��
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/admin/blogger")
public class BloggerAdminController {

	@Resource
	private BloggerService bloggerService;
	
	/**
	 * ��ѯ������Ϣ
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/find")
	public String find(HttpServletResponse response)throws Exception{
		Blogger blogger=bloggerService.find();
		JSONObject jsonObject=JSONObject.fromObject(blogger);
		ResponseUtil.write(response, jsonObject);
		return null;
	}



	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(Blogger blogger, @RequestParam(value="imageFile",required = false) MultipartFile imageFile,HttpServletRequest request,HttpServletResponse response)throws Exception{
		if(imageFile!=null){
			if(!imageFile.isEmpty()){
				String filePath=request.getServletContext().getRealPath("/");
				String imageName=DateUtil.getCurrentDateStr()+"."+imageFile.getOriginalFilename().split("\\.")[1];
				System.out.println(filePath+"\n"+imageName);
				imageFile.transferTo(new File(filePath+"/static/userImages/"+imageName));
				blogger.setImageName(imageName);
			}
		}


		int resultTotal=bloggerService.update(blogger);
		StringBuffer result=new StringBuffer();
		if(resultTotal>0){
			result.append("<script language='javascript'>alert('修改成功');</script>");
		}else{
			result.append("<script language='javascript'>alert('修改失败');</script>");
		}
		ResponseUtil.write(response, result);
		return null;
	}
	
	/**
	 * �޸Ĳ�������
	 * @param newPassword
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/modifyPassword")
	public String modifyPassword(String newPassword,HttpServletResponse response)throws Exception{
		Blogger blogger=new Blogger();
		blogger.setPassword(CryptographyUtil.md5(newPassword,CryptographyUtil.getSalt()));
		int resultTotal=bloggerService.update(blogger);
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
	 * ע��
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/logout")
	public String logout()throws Exception{
		SecurityUtils.getSubject().logout(); 
		return "redirect:/login.jsp";
	}
}
