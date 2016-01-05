//package com.liu.test;
//
//
//import com.liu.rpc.service.TestRegistryService;
//import org.junit.Test;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//public class TestRpc {
//
//    @Test
//    public void testRpc() {
//        ApplicationContext context = new ClassPathXmlApplicationContext("spring-rpc.xml");
//        TestRegistryService tt = (TestRegistryService) context.getBean("testRegistryService");
//        String str = tt.hello("yfs");
//        System.out.println(str);
//
//    }
//
//}
