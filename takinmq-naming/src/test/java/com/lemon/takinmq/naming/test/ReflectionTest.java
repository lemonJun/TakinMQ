package com.lemon.takinmq.naming.test;

import java.lang.reflect.Method;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.lemon.takinmq.common.datainfo.TopicConfigSerializeWrapper;

public class ReflectionTest {

    private static final Logger logger = LoggerFactory.getLogger(ReflectionTest.class);

    public static void main(String[] args) {
        new ReflectionTest().testnormal();
    }

    private void testnormal() {
        try {
            PropertyConfigurator.configure("D:/log4j.properties");
            Class target = Class.forName("com.lemon.takinmq.naming.NamingServiceImpl");
            Method[] methods = target.getDeclaredMethods();
            for (Method m : methods) {
                logger.info("->" + m.getParameterTypes());
            }
            Class<?> mc[] = new Class[1];
            mc[0] = Long.TYPE;
            //            mc[1] = String.class;
            //            mc[2] = Long.class;
            //            mc[3] = TopicConfigSerializeWrapper.class;

            logger.info(String.format("invoke class:%s method:%s params:%s", target.toString(), "register", Joiner.on(",").join(mc)));
            Method method = target.getDeclaredMethod("getTopicsByLong", mc);
            method.setAccessible(true);
            logger.info(method.toString());
            Object result = method.invoke(target.newInstance(), 1);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testregister() {
        try {
            PropertyConfigurator.configure("D:/log4j.properties");
            Class target = Class.forName("com.lemon.takinmq.naming.NamingServiceImpl");
            Method[] methods = target.getDeclaredMethods();
            for (Method m : methods) {
                logger.info(m.toString());
            }
            Class<?> mc[] = new Class[5];
            mc[0] = String.class;
            mc[1] = String.class;
            mc[2] = String.class;
            mc[3] = Long.class;
            mc[4] = TopicConfigSerializeWrapper.class;

            logger.info(String.format("invoke class:%s method:%s params:%s", target.toString(), "register", Joiner.on(",").join(mc)));
            Method method = target.getDeclaredMethod("register", mc);
            method.setAccessible(true);
            logger.info(method.toString());
            Object result = method.invoke(target.newInstance(), "", "", "", 1L, new TopicConfigSerializeWrapper());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
