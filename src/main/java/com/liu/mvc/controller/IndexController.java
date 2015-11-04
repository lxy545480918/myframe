package com.liu.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import static com.liu.mvc.ResponseUtils.modelView;

/**
 * Created by lxy on 2015/11/3.
 */
@Controller
public class IndexController {


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index1(ModelMap map) {
        map.put("word", "Welcome to myFrame");
        return modelView("/index");
    }

    @RequestMapping(value = "/index.html", method = RequestMethod.GET)
    public ModelAndView index2(ModelMap map) {
        return index1(map);
    }

    /**
     * 所有html请求都转发成jsp页面
     * @param page
     * @return
     */
    @RequestMapping(value = "/{page}.html", method = RequestMethod.GET)
    public String allPage(@PathVariable String page) {
        return "/"+page;
    }




}