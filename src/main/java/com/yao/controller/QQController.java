package com.yao.controller;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.oauth.Oauth;
import com.yao.entity.Blogger;
import com.yao.entity.Receiver;
import com.yao.service.BloggerService;
import com.yao.util.CryptographyUtil;
import com.yao.util.DateUtil;
import com.yao.util.IPUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.qq.connect.javabeans.qzone.UserInfoBean;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.qq.connect.api.qzone.UserInfo;
@Controller
@RequestMapping("/qq")
public class QQController {

    @Autowired
    RabbitTemplate reRabbitTemplate;

    @Resource
    private BloggerService bloggerService;

    @Value(value = "${qq.qqName}")
    private String qqName;

    @Value(value = "${qq.qqId}")
    private String qqId;


    @RequestMapping("/login")
    public ModelAndView qqLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
        } catch (QQConnectException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping("/callback")
    public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
            String accessToken   = null,
                    openID        = null;
            long tokenExpireIn = 0L;
            if (accessTokenObj.getAccessToken().equals("")) {
                System.out.print("没有获取到响应参数");
            }else{
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();
                //2614BB84C3F055E0328F5EDAC59D492A
                //2614BB84C3F055E0328F5EDAC59D492A
                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                String name = userInfoBean.getNickname();
                //只有qq号码对应的openId和qq昵称全部对应上才可以让其登录
                System.out.println("openID         " + openID);
                System.out.println("name        " + name);
                System.out.println("qqId         " + qqId);
                System.out.println("qqName         " + qqName);
                if(openID.equals(qqId)&&qqName.equals(name)){
                    //这里可以让用户登录.
                    //注意：这个find()方法很危险，因为它查询的是管理员，直接查询的管理员，没有任何的验证！
                    //其实是我疏忽了，没有考虑到Shiro的认证这么强大，
                    //我一直认为只要用Shiro的工具类向Session中存放信息就行了，没想到不行，直接给我拦截到登录界面上去了
                    Blogger blogger = bloggerService.find();
                    Subject subject=SecurityUtils.getSubject();
                    UsernamePasswordToken token=new UsernamePasswordToken(blogger.getUserName(), blogger.getPassword());
                    subject.login(token);

                    String ip = IPUtils.getIpAddress(request);
                    final String content = "IP为 "+ip+" 的用户在 "+DateUtil.getCurrentDateStr2()+" 登录了后台管理系统";
                    reRabbitTemplate.convertAndSend("exchange.direct", "Blog", new Receiver(content));
                    System.out.println("向队列中发送消息了");

                    return "redirect:/admin/main.jsp";

                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
