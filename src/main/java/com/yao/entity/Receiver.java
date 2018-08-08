package com.yao.entity;

public class Receiver {

    private String info;

    private String ip;

    private String subject = "博客网站通知";

    private String from = "网站后台系统";

    public String getInfo() {
        return info;
    }

    public String getIp() {
        return ip;
    }

    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Receiver() {

    }

    public Receiver(String info) {
        this.info = info;
    }

    public Receiver(String info, String subject, String from) {
        this.info = info;
        this.subject = subject;
        this.from = from;
    }

    public static void main(String[] args) {
        Receiver r = new Receiver("这里是内容啊啊啊啊啊");
        r.setFrom("您好吗");
        System.out.println(r.getFrom());
        System.out.println(r.getSubject());
        System.out.println("Hello你好啊！");
    }
}
