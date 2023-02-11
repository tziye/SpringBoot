package com.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class OperateTest extends ZookeeperApplicationTest {

    @Test
    void transaction() throws Exception {
        log.info("Transaction==========");
        Transaction ts = zk.transaction();
        ts.create("/ts1", "v1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        ts.create("/ts2", "v2".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        List<OpResult> rs = ts.commit();
        rs.forEach(r -> log.info("{}", r.getType()));
        log.info("multi==========");
        rs = zk.multi(Arrays.asList(Op.delete("/ts1", -1), Op.delete("/ts2", -1)));
        rs.forEach(r -> log.info("{}", r.getType()));
    }

}