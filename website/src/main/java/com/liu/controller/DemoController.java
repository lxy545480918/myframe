package com.liu.controller;

import com.liu.cache.RedisUtil;
import com.liu.core.dictionary.Dictionary;
import com.liu.core.dictionary.DictionaryController;
import com.liu.entity.Account;
import com.liu.entity.DemoEntity;
import com.liu.service.DemoService;
import com.liu.util.JSONUtils;
import com.liu.util.exception.CodedBaseRuntimeException;
import com.liu.util.converter.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

import static com.liu.controller.ResponseUtils.jsonView;
import static com.liu.controller.ResponseUtils.modelView;


@Controller
@RequestMapping(value = "demo")
public class DemoController {

    @Autowired
    DemoService demoService;

    @Autowired
    RedisUtil redisUtil;

    @RequestMapping(value = "/exception.html", method = RequestMethod.GET)
    public ModelAndView exception() {
        if("1".equals("1")) {
            throw new CodedBaseRuntimeException("随便抛出一个异常");
        }
        return jsonView("显示异常界面");
    }

    //测试数据库
    @RequestMapping(value = "/db.html", method = RequestMethod.GET)
    public ModelAndView db() {
        Account account = demoService.get(Account.class, 1L);
        return jsonView(account);
    }

    //测试字典
    @RequestMapping(value = "/dic.html", method = RequestMethod.GET)
    public ModelAndView dic() {
        Dictionary dic = DictionaryController.instance().get("dic.confirm");
        return jsonView(dic.getItems());
    }

    //测试缓存
    @RequestMapping(value = "/redis.html", method = RequestMethod.GET)
    public ModelAndView redis() {
        return jsonView(redisUtil.get("name3"));
    }

    //测试类型转换
    @RequestMapping(value = "/convert.html", method = RequestMethod.GET)
    public ModelAndView convert(ModelMap map) {
        DemoEntity demo = new DemoEntity();
        demo.setDate(new Date());
        demo.setNumber(10.22);
        demo.setStr("i like code");
        map.put("demo", JSONUtils.toString(demo));
        map.put("dateConvert", ConversionUtils.convert(new Date(), String.class));

        return modelView("demoEntity");
    }


}
