package com.neuedu.controller.backend;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

//后台用户控制器类
@RestController
@RequestMapping(value = "/manage/user")
public class UserManageController {
    @Autowired
    IUserService userService;
    //管理员登录
    @RequestMapping(value = "/login.do")
    public ServerResponse login(HttpResponse response,HttpSession session, @RequestParam(value = "username")String username,
                                @RequestParam("password")String password){
        ServerResponse serverResponse = userService.login(username, password);
        if (serverResponse.isSucess()){//登录成功
            //保存管理员的登录状态，将管理员信息放在session的作用域里
            UserInfo userInfo = (UserInfo)serverResponse.getDate();
            if (userInfo.getRole()==Const.RoleEnum.ROLE_CUSTOMER.getCode()){
                return ServerResponse.createServerResponseByError("没有权限");
            }
            session.setAttribute(Const.CURRENTUSER,userInfo);
        }
        return serverResponse;
    }
}
