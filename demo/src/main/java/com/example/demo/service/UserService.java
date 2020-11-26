package com.example.demo.service;

import com.example.demo.vo.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User selectByPrimaryKey(Integer id);

    User selectByUsername(String username);

    int insertSelective(User record);

    int updateByPrimaryKeySelective(User record);
}
