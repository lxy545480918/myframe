package com.liu.test;

import com.liu.util.exception.CodedBaseRuntimeException;
import org.junit.Test;


public class Test1 {

    @Test
    public void testControl() {
        if("1".equals("1")) {
            System.out.println("中文");
            throw new CodedBaseRuntimeException("随便抛出一个异常");
        }

    }
}
