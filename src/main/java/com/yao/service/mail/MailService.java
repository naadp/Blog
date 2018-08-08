package com.yao.service.mail;

import com.yao.entity.Receiver;
import com.yao.entity.Subscriber;
import com.yao.util.MailUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailService {

    @Value(value = "${mail.myEmailAccount}")
    public String myEmailAccount;

    @Value(value = "${mail.myEmailPassword}")
    public String myEmailPassword;

    @Value(value = "${mail.myEmailSMTPHost}")
    public String myEmailSMTPHost;

    @Value(value = "${mail.receiveMailAccount}")
    public String receiveMailAccount;

    @Async
    public void sendMailMessage(Receiver receiver) {
        try {
            MailUtils.send(receiver.getFrom(), receiver.getSubject(), receiver.getInfo(), myEmailAccount, myEmailPassword, myEmailSMTPHost, receiveMailAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Async
    public void toActive(Subscriber subscriber) {
        try {
            MailUtils.send(subscriber.getFrom(), subscriber.getSubject(), subscriber.getContent(), myEmailAccount, myEmailPassword, myEmailSMTPHost, subscriber.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void inform(List<Subscriber> list) {
        try {
            for(Subscriber subscriber : list){
                MailUtils.send(subscriber.getFrom(), subscriber.getSubject(), subscriber.getContent(), myEmailAccount, myEmailPassword, myEmailSMTPHost, subscriber.getEmail());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}
