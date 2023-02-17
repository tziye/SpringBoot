package com.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
class AclTest extends ZookeeperApplicationTest {

    @BeforeEach
    void before() {
        // 如果设置了ACL，则一切操作都要先添加AuthInfo
        zk.addAuthInfo("digest", "root:123456".getBytes());
    }

    @Test
    void createWithAcl() throws Exception {
        Stat stat = zk.exists("/acl", true);
        if (stat != null) {
            zk.delete("/acl", stat.getVersion());
        }

        List<ACL> acls = new ArrayList<>(1);
        Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest("root:123456"));
        ACL acl1 = new ACL(ZooDefs.Perms.ALL, id1);
        acls.add(acl1);
        String result = zk.create("/acl", "value".getBytes(), acls, CreateMode.PERSISTENT);
        log.info("createWithAcl: {}", result);

        stat = zk.setACL("/acl", acls, zk.exists("/acl", true).getVersion());
        log.info("setAcl: {}", stat);

        List<ACL> list = zk.getACL("/acl", null);
        String auth = list.get(0).getId().getId();
        log.info("getAcl: {}", auth);
    }

    @Test
    void digest() throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        String account = "root:123456";
        String[] parts = account.split(":", 2);
        byte[] digest = MessageDigest.getInstance("SHA1").digest(account.getBytes());
        String auth = parts[0] + ":" + new String(encoder.encode(digest), StandardCharsets.UTF_8);
        log.info("digest: {}", auth);
    }

}
