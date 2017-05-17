package com.bj58.spat.esbclient.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.spat.esbclient.ESBReceiveHandler;

public class ESBConfig {

    private List<ServerConfig> servConfList;
    private ESBReceiveHandler receiveHandler;
    private short clientID;
    private ExecutorService executor;
    private static final Log logger = LogFactory.getLog(ESBConfig.class);
    /**
     * 发送重试次数
     */
    public static int SEND_RETRY_COUNT = 100;

    /**
     * 
     * @param url tcp://10.58.120.110:12345,10.58.120.110:12345,10.58.120.110:12345?clientID=123&xxx=abc
     * @return
     */
    public static ESBConfig getConfigFromURL(String url) {
        //TODO
        ESBConfig esbConfig = new ESBConfig();
        String lower_url = url.toLowerCase();
        String protocal = lower_url.substring(0, lower_url.indexOf("//") + 2);
        if (!protocal.trim().equals("tcp://")) {
            logger.error("not supported protocal:" + protocal);
        }
        String ippairs = lower_url.substring(lower_url.indexOf("//") + 2, lower_url.indexOf("?"));
        String params = lower_url.substring(lower_url.indexOf("?") + 1);
        Map<String, String> paramMap = new HashMap<String, String>();
        List<ServerConfig> listServerConfig = new ArrayList<ServerConfig>();

        //parse ippair
        if (ippairs != null) {
            String[] s_ippairs = ippairs.split(",");
            for (String s_ippair : s_ippairs) {
                String[] ippair = s_ippair.split(":");
                ServerConfig severConfig = new ServerConfig();
                severConfig.setIp(ippair[0]);
                severConfig.setPort(Integer.parseInt(ippair[1]));
                listServerConfig.add(severConfig);
            }
        }

        //parse params
        if (params != null) {
            String[] s_params = params.split("&");
            for (String s_param : s_params) {
                String[] parampair = s_param.split("=");
                paramMap.put(parampair[0], parampair[1]);
            }
        }

        esbConfig.setClientID(Short.parseShort(paramMap.get("clientid")));
        esbConfig.setServConfList(listServerConfig);
        return esbConfig;
    }

    public static ESBConfig getConfigFromFile(String path) {
        //TODO
        return null;
    }

    public List<ServerConfig> getServConfList() {
        return servConfList;
    }

    public void setServConfList(List<ServerConfig> servConfList) {
        this.servConfList = servConfList;
    }

    public short getClientID() {
        return clientID;
    }

    public void setClientID(short clientID) {
        this.clientID = clientID;
    }

    public void setReceiveHandler(ESBReceiveHandler receiveHandler) {
        this.receiveHandler = receiveHandler;
    }

    public ESBReceiveHandler getReceiveHandler() {
        return receiveHandler;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }
}
