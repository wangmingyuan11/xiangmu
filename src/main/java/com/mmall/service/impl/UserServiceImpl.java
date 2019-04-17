package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount=userMapper.checkUsername(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectLogin(username,md5Password);
        if(user==null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user){
            ServerResponse vailResponse=this.checkValid(user.getUsername(),Const.USERNAME);
            if(!vailResponse.isSuccess()){
                return vailResponse;
            }
            vailResponse=this.checkValid(user.getEmail(),Const.EMAIL);
            if(!vailResponse.isSuccess()){
            return vailResponse;
            }
            user.setRole(Const.Role.ROLE_CUSTOMER);
            //MD5加密
            user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
            int resultCount=userMapper.insert(user);
            if(resultCount==0){
                return ServerResponse.createByErrorMessage("注册失败");
            }
            return ServerResponse.createByErrorMessage("注册成功");

    }

    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNoneBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount=userMapper.checkUsername(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount=userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }

        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");

    }
}
