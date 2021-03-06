package com.hzgc.collect.expand.processer;

import com.hzgc.collect.expand.conf.CommonConf;
import com.hzgc.collect.expand.log.DataProcessLogWriter;
import com.hzgc.collect.expand.log.LogEvent;
import com.hzgc.collect.expand.util.*;
import com.hzgc.common.ftp.Sharpness;
import com.hzgc.common.ftp.properties.CollectProperties;
import com.hzgc.collect.expand.util.KafkaProperties;
import com.hzgc.common.ftp.faceobj.FaceObject;
import com.hzgc.common.ftp.message.FtpPathMessage;
import com.hzgc.common.ftp.FtpUtils;
import com.hzgc.dubbo.dynamicrepo.SearchType;
import com.hzgc.jni.FaceAttribute;
import com.hzgc.jni.FaceFunction;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

public class ProcessThread implements Runnable, Serializable {

    private BlockingQueue<LogEvent> queue;
    private DataProcessLogWriter writer;

    public ProcessThread(CommonConf conf, BlockingQueue<LogEvent> queue, String queueID) {
        this.queue = queue;
        writer = new DataProcessLogWriter(conf, queueID);
    }

    @Override
    public void run() {
        LogEvent event;
        try {
            while ((event = queue.take()) != null) {
                Sharpness sharpness = CollectProperties.getSharpness();
                FaceAttribute attribute =
                        FaceFunction.featureExtract(event.getAbsolutePath(), sharpness.getWeight(), sharpness.getHeight());
                FtpPathMessage message = FtpUtils.getFtpPathMessage(event.getRelativePath());
                if (attribute.getFeature() != null) {
                    FaceObject faceObject = new FaceObject(message.getIpcid()
                            , message.getTimeStamp()
                            , SearchType.PERSON
                            , message.getDate()
                            , message.getTimeslot()
                            , attribute
                            , event.getTimeStamp() + "");
                    ProcessCallBack callBack = new ProcessCallBack(event.getFtpPath(),
                            System.currentTimeMillis(), this.writer, event);
                    ProducerKafka.getInstance().sendKafkaMessage(
                            KafkaProperties.getTopicFeature(),
                            event.getFtpPath(),
                            faceObject,
                            callBack);
                } else {
                    writer.countCheckAndWrite(event);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
