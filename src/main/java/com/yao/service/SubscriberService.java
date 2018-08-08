package com.yao.service;

import com.yao.entity.Subscriber;

import java.util.List;
import java.util.Map;

public interface SubscriberService {

    public Integer addSubscriber(Subscriber subscriber);

    public Integer deleteSubscriber(Map<String ,Object> map);

    public Integer updateSubscriber(Subscriber subscriber);

    public List<Subscriber> getAll();

    public Subscriber getSubscriberByEmail(String email);

    public Subscriber getSubscriberByUUID(String uuid);

    public void activeSubscriberByUUID(String uuid);

    public Integer modifyEmail(Map<String, Object> map);

    public Subscriber  getSubscriberByUUIDAndEmail(Map<String, Object> map);

}
