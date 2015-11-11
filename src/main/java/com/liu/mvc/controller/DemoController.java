package com.liu.mvc.controller;

import com.liu.entity.Account;
import com.liu.service.support.DemoService;
import com.liu.util.exception.CodedBaseRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import static com.liu.mvc.ResponseUtils.jsonView;

/**
 * Created by lxy on 2015/11/3.
 */
@Controller
@RequestMapping(value = "demo")
public class DemoController {

    @Autowired
    DemoService demoService;

    @RequestMapping(value = "/exception.html", method = RequestMethod.GET)
    public ModelAndView exception() {
        if("1".equals("1")) {
            throw new CodedBaseRuntimeException("随便抛出一个异常");
        }
        return jsonView("显示异常界面");
    }

    @RequestMapping(value = "/db.html", method = RequestMethod.GET)
    public ModelAndView db() {
        Account account = demoService.get(Account.class, 1L);
        return jsonView(account);
    }




}
