package com.lemon.takinmq.naming.test;

import org.apache.log4j.PropertyConfigurator;

import com.lemon.takinmq.naming.NamingStartUp;

public class NameingTest {

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("D:/log4j.properties");
            NamingStartUp naming = new NamingStartUp();
            naming.init();
            naming.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
