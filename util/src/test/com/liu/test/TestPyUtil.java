package com.liu.test;


import org.junit.Test;
import com.liu.util.PyConverter;

public class TestPyUtil {

    @Test
    public void test1() {
        System.out.println(PyConverter.getPinYinWithoutTone("中华人民共和国"));
        System.out.println(PyConverter.getFirstLetter("中华人民共和国"));
        System.out.println(PyConverter.getPinYin("中华人民共和国"));

    }
}
