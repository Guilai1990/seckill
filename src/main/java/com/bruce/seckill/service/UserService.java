package com.bruce.seckill.service;

import com.bruce.seckill.dao.UserDao;
import com.bruce.seckill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    //@Transactional
    public User getById(int id) {
        return userDao.getById(id);
    }

    public boolean tx() {
        User u1 = new User();
        u1.setId(2);
        u1.setName("Evelyn");
        userDao.insert(u1);

        User u2 = new User();
        u2.setId(1);
        u2.setName("James");
        userDao.insert(u2);

        return true;
    }

}
