package cn.smbms.controller.user;


import cn.smbms.beans.User;
import cn.smbms.service.UserService;
import cn.smbms.tools.Consts;
import cn.smbms.tools.PageSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    @Qualifier("userService")
    private UserService userService ;

    @RequestMapping("index.html")
    public String login() {
        System.out.println("登录操作");
        return "index" ;
    }


    @RequestMapping("dologin.html")
    public String doLogin(@RequestParam("userCode") String userName , @RequestParam("userPassword") String password
            , HttpSession session ) {
        // 到数据库中查找用户是否存在
        User user = userService.login(userName, password);
        System.out.println(user);
        if ( null != user) {
            System.out.println("查找到了用户");
            session.setAttribute(Consts.USER_SESSION, user );
            //  用户输入信息正确，跳转到功能页面
            return "redirect:/user/main.html";
        }else {
            // 输入信息不正确，重新登录
            return "index" ;
        }
    }

    @RequestMapping("main.html")
    public String main() {
        System.out.println("主体功能页面");
        return "frame";
    }

    // 退出
    @RequestMapping("logout.html")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }

    // 用户详情列表
    @RequestMapping("userList.html")
    public ModelAndView userList(HttpServletRequest request , HttpServletResponse response) throws IOException {

        String queryUserName = request.getParameter("queryname");
        String temp = request.getParameter("queryUserRole");
        String pageIndex = request.getParameter("pageIndex");
        int queryUserRole = 0; // 默认用户没有选择任何身份，表示查询所有身份的人
        int pageSize = Consts.pageSize; // 分页的每一页初始大小 5
        int currentPageNo = 1 ; // 当前页码

        System.out.println("queryUserName servlet--------"+queryUserName);
        System.out.println("queryUserRole servlet--------"+queryUserRole);
        System.out.println("query pageIndex--------- > " + pageIndex);
        if(queryUserName == null){
            queryUserName = "";
        }
        if(temp != null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp);
        }
        if(pageIndex != null){
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch(Exception e){
                response.sendRedirect("error.jsp");
            }
        }

        //总数量（表）
        int totalCount	= userService.getUserCount(queryUserName,queryUserRole);
        System.out.println("总数量" + totalCount );

        //总页数
        PageSupport pages=new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        pages.setTotalPageCountByRs();
        int totalPageCount = pages.getTotalPageCount();
        //控制首页和尾页
        if(currentPageNo < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }

        List<User> userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);


        /*List<User> userList = userService.getUserList();
        System.out.println(userList);*/

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("userlist");
        modelAndView.addObject("userList" , userList );
        return modelAndView;
    }












}
