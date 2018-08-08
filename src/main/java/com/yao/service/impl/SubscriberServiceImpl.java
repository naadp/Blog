package com.yao.service.impl;

import com.yao.dao.SubscriberDao;
import com.yao.entity.Subscriber;
import com.yao.service.SubscriberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class SubscriberServiceImpl implements SubscriberService {

    @Resource
    private SubscriberDao subscriberDao;

    @Override
    public Integer addSubscriber(Subscriber subscriber) {
        return subscriberDao.addSubscriber(subscriber);
    }

    @Override
    public Integer deleteSubscriber(Map<String ,Object> map) {
        return subscriberDao.deleteSubscriber(map);
    }

    @Override
    public Integer updateSubscriber(Subscriber subscriber) {
        return subscriberDao.updateSubscriber(subscriber);
    }

    @Override
    public List<Subscriber> getAll() {
        return subscriberDao.getAll();
    }

    @Override
    public Subscriber getSubscriberByEmail(String email) {
        return subscriberDao.getSubscriberByEmail(email);
    }

    @Override
    public Subscriber getSubscriberByUUID(String uuid) {
        return subscriberDao.getSubscriberByUUID(uuid);
    }

    @Override
    public void activeSubscriberByUUID(String uuid) {
        subscriberDao.activeSubscriberByUUID(uuid);
    }

    @Override
    public Integer modifyEmail(Map<String, Object> map) {
        return subscriberDao.modifyEmail(map);
    }

    @Override
    public Subscriber getSubscriberByUUIDAndEmail(Map<String, Object> map) {
        return subscriberDao.getSubscriberByUUIDAndEmail(map);
    }
}
