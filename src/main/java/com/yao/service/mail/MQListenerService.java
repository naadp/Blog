package com.yao.service.mail;

import com.yao.entity.Receiver;
import com.yao.entity.Subscriber;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MQListenerService {
	
	@Autowired
	MailService mailService;
	
	@RabbitListener(queues= {"Blog"})
	public void receive(Receiver receiver) {
		System.out.println("收到了通知, 开始发送邮件。");
		mailService.sendMailMessage(receiver);
	}

	@RabbitListener(queues= {"subscriber"})
	public void subReceive(List<Subscriber> list) {
		System.out.println("收到了通知, 开始发送邮件。");
		mailService.inform(list);
	}
	
	
	
}
