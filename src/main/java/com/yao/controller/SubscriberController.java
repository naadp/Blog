package com.yao.controller;

import com.yao.entity.Subscriber;
import com.yao.service.SubscriberService;
import com.yao.service.mail.MailService;
import com.yao.util.StringUtil;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

//注意啊, 这里加了这个注解, 所以返回的数据都会被当成 String或者Json返回,
//而不会进行页面的跳转, 这点要注意啊！
@RestController
@RequestMapping("/subscriber")
public class SubscriberController {

    @Resource
    private SubscriberService subscriberService;

    @Resource
    private MailService mailService;

    @Value(value = "${mail.href}")
    public String href;

    //我记得这里好像是有个规则吧，是说 @Valid修饰的实体类，必须要和BindingResult在一起，必须挨着
    //否则不行！
    @RequestMapping("/addSubscriber")
    public Map<String, String> addSubscriber(@Valid Subscriber subscriber, BindingResult result, String action){
        Map<String, String> map = new HashMap<>();

        //用户名为空或者邮箱格式不行, 获取错误信息, 直接将方法返回
        if(result.getErrorCount() != 0){
            String errorInfo = "";
            for(FieldError err : result.getFieldErrors()){
                errorInfo = errorInfo + err.getField() + " " + err.getDefaultMessage() + "\n";
            }
            map.put("info",errorInfo.trim());
            return map;
        }

        //查看此邮箱已经注册, 返回提示信息, 结束方法.
        Subscriber sub = subscriberService.getSubscriberByEmail(subscriber.getEmail());
        if(null==sub){
            //没有这个邮箱, 直接插入：需设置UUID和状态位
            String uuid = StringUtil.getSoleStr();
            subscriber.setUuid(uuid);
            subscriber.setState(0);
            subscriberService.addSubscriber(subscriber);
            //其实这里是MyBatis, 如果是SpringData啊, 它有些变态啊, 你在下面设置了一些值, 它会再发送一条SQL,
            // 给你设置进去. 当然, 这里的from等属性是不做映射的.
            subscriber.setFrom("博客网站管理员");
            subscriber.setSubject("激活邮件");
            //这里要有个后缀, 因为拦截的就是有 .do 和 .html后缀的嘛
            href = "http://120.78.210.140/Blog/subscriber/check/" + uuid + ".do";
            subscriber.setContent("<p>您好！感谢您订阅本网站的博客。当本网站的博客有更新时，我们会以博客的方式通知您！</p><p><a href='"+href+"'>点击激活订阅</a></p>"
            +"<p>这是你的身份验证码，当您更改订阅邮箱和取消订阅时需要此信息："+uuid+"</p>"
            +"<p>如非本人操作，请忽略此邮件！多有打扰，抱歉！</p>"
            );
            mailService.toActive(subscriber);
            map.put("info","感谢您的订阅！已向您的邮箱发送了激活邮件，请登录激活！谢谢！");
            return map;
        }else{
            //有这个邮箱, 此时应该再判断下标志位
            if(sub.getState()==0){
                //此时是已经注关注了, 但是还没有激活
                map.put("info","您已经关注，但是还未激活，请前往邮箱进行激活");
                return map;
            }else if(sub.getState()==1){
                map.put("info","李时珍的皮！该邮箱已订阅，再重复订阅打你屁屁！");
                return map;

            }else if(sub.getState()==2){
                //这是已经关注, 但是后来取消了. 设置一下状态位即可。状态位应直接设为 1 , 而不是 0.
                sub.setState(1);
                subscriberService.updateSubscriber(sub);
                //因为之前关注过, 此时不需要进行邮箱的验证了. 但是应该提示下他的uuid, 告诉他在取消订阅和修改密码时用得到
                map.put("info","感谢您再次订阅！此为您身份验证码，请牢记："+sub.getUuid());
                return map;
            }
        }
        return map;
    }

    @RequestMapping("/check/{uuid}")
    public String check(@PathVariable String uuid, @RequestParam(required = false) String newEmail, HttpServletRequest request){
        String url = "http://120.78.210.140/Blog/index.html";
        if(newEmail==null||"".equals(newEmail)){
            //说明这个是邮箱激活的，而非更改邮箱。

            StringBuilder sb = new StringBuilder();
            sb.append("<script type=\"text/javascript\">");
            //为防止用户重复点击此超链接，先判断此邮箱是否已激活。已激活的话，则给出提示信息。
            Subscriber subscriber = subscriberService.getSubscriberByUUID(uuid);
            //在这里，不管他是什么时候点击的这个超链接：是激活也好，是激活后再次点击也好，
            // 是修改邮箱成功前之前点击的也好，是修改邮箱成功后也好。subscriber肯定是已经存在的了，
            // 因为在发邮件之前就已经将数据插入了，而且uuid不会变：所以，这里不必进行null的判断。
            if(subscriber.getState()>0){
                sb.append("alert('您已经激活成功了！请勿重复激活！程序员也是有尊严的，请您尊重！')");
                sb.append(";window.location.href='"+url+"'");
                sb.append("</script>");
                return sb.toString();
            }


            //修改标志位.
            subscriberService.activeSubscriberByUUID(uuid);

            //返回提示信息：然后进行跳转

            sb.append("alert('恭喜您，激活成功！当本网站博客有更新时, 会邮箱通知您！谢谢！')");
            sb.append(";window.location.href='"+url+"'");
            sb.append("</script>");
            return sb.toString();

        }else{
            //不为空，说明这个是邮箱验证的。
            //根据UUID，将邮箱进行修改




            Map<String, Object> map = new HashMap<>(2);
            map.put("email",newEmail);
            map.put("uuid",uuid);
            if(null == subscriberService.getSubscriberByUUIDAndEmail(map)){
                //说明没有验证过；
                //其实这里会有这么一个现象：已经更改了一次邮箱，再次更改，但是还未验证，这时点击原来的那个验证链接，会将邮箱修改为原来的.
                subscriberService.modifyEmail(map);
                StringBuilder sb = new StringBuilder();
                sb.append("<script type=\"text/javascript\">");
                sb.append("alert('验证成功，邮箱已更改为 '"+newEmail+")");
                sb.append(";window.location.href='"+url+"'");
                sb.append("</script>");
                return sb+"";
            }else{
                //已经验证过了
                StringBuilder sb = new StringBuilder();
                sb.append("<script type=\"text/javascript\">");
                sb.append("alert('您已经验证过了！请勿重复验证！程序员也是有尊严的，请您尊重！')");
                sb.append(";window.location.href='"+url+"'");
                sb.append("</script>");
                return sb+"";
            }
        }
    }

    @RequestMapping("/modifyEmail")
    public Map<String, Object> modifyEmail(@Valid Subscriber subscriber, BindingResult result, String newEmail){
        Map<String, Object> map = new HashMap<>();

        //邮箱格式不行, 获取错误信息, 直接将方法结束返回
        if(result.getErrorCount() != 0){
            String errorInfo = "";
            for(FieldError err : result.getFieldErrors()){
                errorInfo = errorInfo + err.getField() + " " + err.getDefaultMessage() + "\n";
            }
            map.put("info",errorInfo.trim());
            return map;
        }

        map.put("uuid",subscriber.getUuid());
        map.put("email",subscriber.getEmail());
        Subscriber sub = subscriberService.getSubscriberByUUIDAndEmail(map);
        if(null == sub){
            //说明根本这个邮箱根本就没有订阅
            map.put("info","抱歉，您输入的信息验证有误，无法修改邮箱！");
            return map;
        }else{
            //不为null, 说明确实有这个邮箱， 此时应根据state判断此邮箱的状态
            if(sub.getState()==0){
                //说明此邮箱已经注册了，但是还未激活！
                //这里就让他重新用新邮箱注册，我们就不帮他开通了，那样太麻烦了，非常不必要！很容易打消编写代码的积极性
                map.put("info","抱歉，你输入的老邮箱并未激活，无法修改！但是您可以用新邮箱重新订阅！");
                return map;
            }else if(sub.getState()==1){
                //说明此邮箱已经激活成功，并且正在使用，可以修改
                //应该向新邮箱内发送邮件，让他激活
                map.put("info","我们向您的新邮箱内发送了一封验证邮件，请您前去验证！");


                subscriber.setFrom("博客网站管理员");
                subscriber.setSubject("激活邮件");
                subscriber.setEmail(newEmail);

                //这里要有个后缀, 因为拦截的就是有 .do 和 .html后缀的嘛
                href = "http://120.78.210.140/Blog/subscriber/check/" + subscriber.getUuid() + ".do?newEmail="+newEmail;
                subscriber.setContent("<p><a href='"+href+"'>请点击进行邮箱验证</a></p>"
                        +"<p>如非本人操作，请忽略此邮件！多有打扰，抱歉！</p>"
                );
                mailService.toActive(subscriber);

                return map;

            }else if(sub.getState()==2){
                //说明此邮箱原来注册过，但是已经取消订阅了！记住啊：只有激活了才可以取消订阅！
                // 因为订阅的目的就是当博客更新时得到通知，但你没有激活，我不会给你发邮件，所以是否取消没有任何意义！
                map.put("info","抱歉，您已取消订阅，无法修改邮箱！您可以直接重新订阅！");
                return map;
            }
        }

        return map;
    }



    //当用户想修改邮箱时, 可以通过添加的方式, 这里不提供修改的方式.而且用户向修改的话, 只能修改邮箱.


    //在取消订阅时, 应该要求用户输入email和uuid进行校验: 输入那个用户名的话, 不太现实。而且，不删除此记录，只将标志位置为3.

    /**
     * 开始时总是想不到怎么弄这个注册, 因为Http请求又没有状态,
     * 而且本网站又没有提供注册功能(因为你不可能要求游客为了看几篇博客而去注册下你的网站), 不能从session中获取数据,
     * 但是, BUT, However, 突然想到了 : ↓
     * 1.   在添加时, 如果邮箱在数据库中不存在, 可以向数据库中插入这条记录, 但是状态应该是0
     *      如果数据库中存在, 但是为0, 就告诉用户 : 你已经订阅了, 但是没有激活, 请去登录邮箱进行激活.
     * 2.   后台应该向那个邮箱发一条验证消息, 就是一个超链接, 告诉用户, 点击下这个超链接,
     *      这个超链接应该是以 用户的邮箱为参数的Rest风格的URL. 而且在点击这个超链接隐形验证时, Subscriber的状态应该是0, 而且必须是0
     * 3.   编写对应的Controller, 获取到那个Rest风格的参数, 然后数据库设置下状态, 就可以了
     *
     */

    @RequestMapping("/notWatch")
    public Map<String, Object> notWatch(@Valid Subscriber subscriber, BindingResult result){
        Map<String, Object> map = new HashMap<>();

        //邮箱格式不行, 获取错误信息, 直接将方法结束返回
        if(result.getErrorCount() != 0){
            String errorInfo = "";
            for(FieldError err : result.getFieldErrors()){
                errorInfo = errorInfo + err.getField() + " " + err.getDefaultMessage() + "\n";
            }
            map.put("info",errorInfo.trim());
            return map;
        }

        map.put("email",subscriber.getEmail());
        map.put("uuid",subscriber.getUuid());
        Integer num = subscriberService.deleteSubscriber(map);

        if(1 == num){
            map.put("info","取消订阅成功");
        }else if(0 == num) {
            map.put("info","取消订阅失败，请验证输入信息是否有误！");
        }

        return map;

    }

}
