package com.liu.mvc;

import com.liu.util.exception.CodedBaseRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.liu.mvc.ResponseUtils.modelView;
import static com.liu.mvc.ResponseUtils.jsonView;

/**
 * Created by lxy on 2015/11/5.
 */
public class MyExceptionHandler implements HandlerExceptionResolver {

    private static final Logger log = LoggerFactory.getLogger(MyExceptionHandler.class);

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("ex", ex);
        //日志记录
        log.error("错误", ex);

        // 根据不同错误转向不同页面
        if (ex instanceof CodedBaseRuntimeException) {
            return jsonView(300, ex.getMessage());
        } else {
            return modelView("/error", model);
        }
    }
}