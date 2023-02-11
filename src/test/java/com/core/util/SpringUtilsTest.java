package com.core.util;

import com.core.ApplicationTest;
import com.pojo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Slf4j
class SpringUtilsTest extends ApplicationTest {

    /**
     * 常用工具
     */
    @Test
    void stringUtils() {
        log.info("测试StringUtils==========");
        log.info("{}", StringUtils.hasText("  "));
    }

    @Test
    void objectUtils() {
        log.info("测试ObjectUtils=========");
        log.info("{}", ObjectUtils.isEmpty(""));
    }

    @Test
    void collectionUtils() {
        log.info("测试CollectionUtils==========");
        log.info("{}", (CollectionUtils.isEmpty(new HashMap<String, String>())));
    }

    @Test
    void numberUtils() {
        log.info("测试NumberUtils=========");
        log.info("{}", NumberUtils.parseNumber("123456", Integer.class));
    }

    @Test
    void beanUtils() {
        log.info("测试BeanUtils==========");
        User u1 = new User();
        u1.setId(1);
        u1.setName("tziye");
        u1.setAge(25);
        User u2 = new User();
        BeanUtils.copyProperties(u1, u2);
        log.info("user1: {}", u1);
        log.info("user2: {}", u2);
    }

    @Test
    void serializationUtils() {
        log.info("测试SerializationUtils=========");
        byte[] b = SerializationUtils.serialize(new User());
        Object o = SerializationUtils.deserialize(b);
        log.info("{}", o);
    }

    @Test
    void testAssert() {
        log.info("测试Assert=========");
        Assert.hasText("1", "字符串不能为空");
    }

    /**
     * 加解密
     */
    @Test
    void base64Utils() {
        log.info("测试Base64Utils==========");
        String encodedStr = Base64Utils.encodeToString("tziye".getBytes());
        log.info(encodedStr);
        log.info("{}", Base64Utils.decodeFromString(encodedStr));
    }

    @Test
    void digestUtils() {
        log.info("测试DigestUtils=========");
        log.info(DigestUtils.md5DigestAsHex("tyf".getBytes()));
    }

    /**
     * ID生成器
     */
    @Test
    void idGenerator() {
        log.info("测试IdGenerator=========");
        IdGenerator idGenerator = new JdkIdGenerator();
        log.info("JdkIdGenerator: {}", idGenerator.generateId());

        IdGenerator simpleIdGenerator = new SimpleIdGenerator();
        log.info("SimpleIdGenerator: {}", simpleIdGenerator.generateId());

        IdGenerator alternativeJdkIdGenerator = new AlternativeJdkIdGenerator();
        log.info("AlternativeJdkIdGenerator: {}", alternativeJdkIdGenerator.generateId());
    }

    /**
     * 集合
     */
    @Test
    void concurrentLruCache() {
        log.info("测试ConcurrentLruCache=========");
        ConcurrentLruCache<String, Object> cache = new ConcurrentLruCache<>(10, k -> "v-" + k);
        for (int i = 0; i < 20; i++) {
            log.info("key:{} value:{} size:{}", i, cache.get(i + ""), cache.size());
        }
    }

    @Test
    void linkedMultiValueMap() {
        log.info("测试LinkedMultiValueMap=========");
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        for (int i = 0; i < 5; i++) {
            map.add("k", i);
        }
        log.info("{}", map.get("k"));
    }

    /**
     * 正则
     */
    @Test
    void patternMatchUtils() {
        log.info("测试PatternMatchUtils=========");
        log.info("{}", PatternMatchUtils.simpleMatch("**f", "tyf"));
    }

    @Test
    void antPathMatcher() {
        log.info("测试AntPathMatcher=========");
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        matcher.setTrimTokens(true);
        log.info("{}", matcher.match("com/t?st.jsp", "com/test.jsp"));
        log.info("{}", matcher.match("com/*.jsp", "com/test.jsp"));
        log.info("{}", matcher.match("com/{filename:\\w+}.jsp", "com/test.jsp"));
    }

    /**
     * 反射
     */
    @Test
    void classUtils() throws ClassNotFoundException {
        log.info("测试ClassUtils==========");
        log.info(ClassUtils.forName("com.core.util.SpringUtilsTest", null).getName());
    }

    @Test
    void reflectionUtils() {
        log.info("测试ReflectionUtils=========");
        log.info("{}", ReflectionUtils.findMethod(SpringUtilsTest.class, "testReflectionUtils"));
    }

    /**
     * 文件
     */
    private static final String PATH = "src/test/resources/";

    @Test
    void fileCopyUtils() throws IOException {
        log.info("测试FileCopyUtils=========");
        log.info("{}", FileCopyUtils.copy(new File(PATH + "test.txt"), new File(PATH + "test1.txt")));
    }

    @Test
    void fileSystemUtils() throws IOException {
        log.info("测试FileSystemUtils=========");
        FileSystemUtils.copyRecursively(new File(PATH + "test.txt"), new File(PATH + "test2.txt"));
        FileSystemUtils.deleteRecursively(new File(PATH + "test2.txt"));
    }

    @Test
    void resourceUtils() throws IOException {
        log.info("测试ResourceUtils=========");
        byte[] b = FileCopyUtils.copyToByteArray(ResourceUtils.getFile("classpath:test.txt"));
        log.info(new String(b, StandardCharsets.UTF_8));
    }

    @Test
    void streamUtils() throws IOException {
        log.info("测试StreamUtils=========");
        log.info(StreamUtils.copyToString(new FileInputStream(PATH + "test.txt"), StandardCharsets.UTF_8));
    }

    /**
     * 系统
     */
    @Test
    void systemPropertyUtils() {
        log.info("测试SystemPropertyUtils==========");
        log.info(SystemPropertyUtils.resolvePlaceholders("${JAVA_HOME:default-value}"));
    }

    @Test
    void socketUtils() {
        log.info("测试SocketUtils=========");
        log.info("{}", SocketUtils.findAvailableTcpPort());
    }
}
