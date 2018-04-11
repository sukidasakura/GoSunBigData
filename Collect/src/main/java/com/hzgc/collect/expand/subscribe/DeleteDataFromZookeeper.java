package com.hzgc.collect.expand.subscribe;

import com.hzgc.common.ftp.properties.CollectProperHelper;
import com.hzgc.common.util.zookeeper.ZookeeperClient;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 删除Zookeeper中保存的订阅及演示功能的数据
 * 删除ZK中“/ftp_subscribe”与“ftp_show”节点数据
 */
public class DeleteDataFromZookeeper extends ZookeeperClient {
    private static Logger LOG = Logger.getLogger(DeleteDataFromZookeeper.class);
    private DeleteDataFromZookeeper(int session_timeout, String zookeeperAddress, String path, boolean watcher) {
        super(session_timeout, zookeeperAddress, path, watcher);
    }

    private static void deleteData(String path) {
        DeleteDataFromZookeeper delete = new DeleteDataFromZookeeper(
                Integer.valueOf(CollectProperHelper.getZookeeperSessionTimeout()),
                CollectProperHelper.getZookeeperAddress(),
                path,
                Boolean.valueOf(CollectProperHelper.getZookeeperWatcher()));
        List<String> children = delete.getChildren();
        if (!children.isEmpty()) {
            for (String childrenPath : children) {
                delete.delete(path + "/" + childrenPath);
                LOG.info("Delete Znode successful! path :" + path + "/" + childrenPath);
            }
        }
    }

    public static void main(String[] args) {
        deleteData(CollectProperHelper.getZookeeperPathSubscribe());
    }

}
