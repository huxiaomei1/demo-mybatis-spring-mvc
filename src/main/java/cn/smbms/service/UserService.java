package cn.smbms.service;

import cn.smbms.beans.User;

import java.util.List;

public interface UserService {

    User login(String userName, String password);

    List<User> getUserList(String queryUserName, int queryUserRole ,
                           int  currentPageNo, int pageSize  );

    int getUserCount(String queryUserName, int queryUserRole);
}
