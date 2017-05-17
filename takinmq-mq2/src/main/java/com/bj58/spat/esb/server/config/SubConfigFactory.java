package com.bj58.spat.esb.server.config;

import com.bj58.spat.esb.server.util.FileLoad;

public class SubConfigFactory {

    public SubConfigFactory() {

    }

    /**
     * 加载sub_config.xml
     * @param confPath url
     * @throws Exception
     */
    public static void load(String confPath) throws Exception {
        SubjectFactory.getSubjectFactory().load(confPath);
        FileLoad fileLoad = FileLoad.getInstance();
        fileLoad.setFileInfo(confPath);
        fileLoad.start();
    }
}
