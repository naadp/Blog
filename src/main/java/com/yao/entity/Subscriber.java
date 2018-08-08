package com.yao.entity;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.beans.Transient;

public class Subscriber {

    //主键
    private Integer id;

    @NotNull(message = "请求填写邮箱")
    @Email(message = "请填写正确的邮箱")
    private String email;

    @NotNull(message = "请填写您的大名")
    private String name;

    //下面这三个是在发送邮件时, 我们自己填写的
    //而且这三项不应该映射到数据库表中, 如果是SpringData的话, 应该用 @Transient注解
    private String from;

    private String subject;

    private String content;

    //这个是用户在订阅时, 我们在后台生成的, 然后返回给客户.
    //这个在取消订阅时用得到.
    private String uuid;

    // 3种状态：
    // 0 代表 插入了数据, 但是没有通过邮箱的验证
    // 1 代表开通了订阅
    // 2 代表取消订阅状态
    private Integer state;


    public Subscriber() {
    }

    public Subscriber(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public Subscriber(Integer id, String email, String name, String from, String subject, String content, String uuid, Integer state) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.from = from;
        this.subject = subject;
        this.content = content;
        this.uuid = uuid;
        this.state = state;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getUuid() {
        return uuid;
    }

    public Integer getState() {
        return state;
    }

    @Override
    public String toString() {
        return "Subscriber{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", uuid='" + uuid + '\'' +
                ", state=" + state +
                '}';
    }
}
