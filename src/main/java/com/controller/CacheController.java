package com.controller;

import com.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cache")
public class CacheController {

    /**
     * 先从缓存中获取，没有就执行方法，将结果存入缓存
     */
    @Cacheable(cacheNames = "userCache", key = "#id", sync = true)
    @GetMapping("/userCache")
    public Result<String> cacheable(@RequestParam("id") long id, @RequestParam("name") String name) {
        log.info("没走缓存");
        String data = id + ":" + name;
        log.info("将数据：{}存入缓存", data);
        return Result.success(data);
    }

    /**
     * 每次调用都将符合条件的数据存入缓存
     */
    @CachePut(cacheNames = "userCache", key = "#id", condition = "#name.length()>1")
    @GetMapping("/userPut")
    public Result<String> cachePut(@RequestParam("id") long id, @RequestParam("name") String name) {
        String data = id + ":" + name;
        log.info("将数据：{}存入缓存", data);
        return Result.success(data);
    }

    /**
     * 删除缓存数据，可全部删除，也可指定key
     */
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    @GetMapping("/userEvict")
    public Result<String> cacheEvict() {
        return Result.success();
    }

}
