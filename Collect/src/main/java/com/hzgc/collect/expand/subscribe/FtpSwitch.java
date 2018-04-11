package com.hzgc.collect.expand.subscribe;

import com.hzgc.common.ftp.properties.CollectProperHelper;

import java.io.Serializable;

/**
 * FTP接收处理数据总开关
 */
public class FtpSwitch implements Serializable{

    private static boolean ftpSwitch;

    public FtpSwitch(){
        ftpSwitch = Boolean.parseBoolean(CollectProperHelper.getFtpSwitch());
    }

    public static boolean isFtpSwitch() {
        return ftpSwitch;
    }
}
