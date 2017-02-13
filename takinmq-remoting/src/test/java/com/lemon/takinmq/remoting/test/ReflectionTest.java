package com.lemon.takinmq.remoting.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.lemon.takinmq.common.datainfo.TopicConfigSerializeWrapper;
import com.lemon.takinmq.common.util.SerializeUtil;

public class ReflectionTest {

    private static final Logger logger = LoggerFactory.getLogger(ReflectionTest.class);

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("D:/log4j.properties");
            Class target = Class.forName("com.lemon.takinmq.naming.NamingServiceImpl");
            Method[] methods = target.getClass().getDeclaredMethods();
            for (Method m : methods) {
                logger.info(m.toString());
            }
            Class<?> mc[] = new Class[4];
            mc[0] = String.class;
            mc[1] = String.class;
            mc[2] = Long.class;
            mc[3] = TopicConfigSerializeWrapper.class;
            logger.info(String.format("invoke class:%s method:%s params:%s", target.getClass(), "register", JSON.toJSONString(mc)));
            Method method = target.getClass().getDeclaredMethod("register", mc);
            method.setAccessible(true);
            logger.info(method.toString());
            Object result = method.invoke(target, args);
            logger.info(JSON.toJSONString(result));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}