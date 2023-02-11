package com.zookeeper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class WatcherTest extends ZookeeperApplicationTest implements Watcher {

    @BeforeEach
    void before() throws Exception {
        zk.getChildren("/", this);
    }

    @SneakyThrows
    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeChildrenChanged) {
            List<String> changedChildrenList = zk.getChildren("/", this);
            changedChildrenList.forEach(c -> log.info("watch: {}", c));
        }
    }

    @Test
    void test() throws Exception {
        String result = zk.create("/p", "v1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        log.info("create: {}", result);

        Stat stat = new Stat();
        byte[] data = zk.getData("/p", true, stat);
        log.info("getData: {}, stat: {}", new String(data, StandardCharsets.UTF_8), stat);

        List<String> list = zk.getChildren("/", false);
        list.forEach(s -> log.info("getChildren: {}", s));

        stat = zk.setData("/p", "v2".getBytes(), zk.exists("/p", true).getVersion());
        log.info("setData: {}", stat);

        zk.delete("/p", zk.exists("/p", true).getVersion());
        stat = zk.exists("/p", true);
        log.info("delete-exists: {}", stat);
    }

}
