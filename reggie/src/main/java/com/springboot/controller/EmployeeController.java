package com.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.R;
import com.springboot.entity.Employee;
import com.springboot.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        /**
         * 1.将页面提交的密码password进行MD5加密处理
         * 2.根据页面提交的用户名username查询数据库
         * 3.如果没有查询到则返回登入失败结果
         * 4.密码比对，如果不一致则返回登入失败的结果
         * 5.查看员工的状态，如果为已禁用状态，则返回员工已经用结果
         * 6.登入成功，将员工id存入Session并返回登入成功结果
         */
        String password = employee.getPassword();
        password =  DigestUtils.md5DigestAsHex(password.getBytes());

         LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
         queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if(emp == null){
            return R.error("账号输入有误");
        }
        if (!emp.getPassword().equals(password)){
            return R.error("用户名或密码有误");
        }
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);

    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
       request.getSession().removeAttribute("employee");
       return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工,员工信息：{}",employee.toString());
        //设置初始密码123456,需要进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
      //  employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //获得当前登入用户的ID
        //Long empId = (Long) request.getSession().getAttribute("employee");
       // employee.setCreateUser(empId);
      //  employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page , int pageSize , String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        long id = Thread.currentThread().getId();
        log.info("线程id为:{}",id);
     //   Long empId = (Long) request.getSession().getAttribute("employee");
      // employee.setUpdateTime(LocalDateTime.now());
    //  employee.setUpdateUser(empId);
           employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

//    1.点击编辑按钮时,页面跳转到add.html,并在url中携带参数[员工id]
//    2.在add.html页面获取url中的参数[员工id]
//    3.发送ajax请求,请求服务端,同时提交员工参数
//    4.服务端接受请求,根据员工id查询员工信息,将员工信息以json形式响应给页面
//    5.页面接受服务端相应的json数据,通过VUE的数据绑定进行用户回显
//    6.点击保存按钮,发送ajax请求,将页面中的员工信息以Json方式提交给服务端
//    7.服务端接受员工信息,并进行处理,完成后给页面响应
//    8.页面接受到服务端响应信息后进行相应处理

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据Id来查询员工信息.....");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
           return R.error("没有查询到对应的员工信息");
    }
}
