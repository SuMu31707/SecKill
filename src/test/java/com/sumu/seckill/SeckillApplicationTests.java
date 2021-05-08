package com.sumu.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Boolean> redisScript;

    @Test
    void contextLoads() {
    }

    /**
     * 在释放锁之前发生异常会造成死锁
     */
    @Test
    void testLock01() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 占位，当key不存在时才能设置成功
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1");
        // 如果设置成功（获取到锁）则进行操作
        if (isLock) {
            valueOperations.set("name", "sumu");
            String name = (String) valueOperations.get("name");
            System.out.println("name=" + name);
            // 制造异常，测试死锁
            Integer.parseInt("sumu");
            // 操作完成释放锁
            redisTemplate.delete("k1");
        } else {
            System.out.println("有线程正在使用，请稍后再试！");
        }
    }

    /**
     * 设置key超时时间防止死锁
     */
    @Test
    void testLock02() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 给锁添加一个超时时间，防止应用在运行过程中抛出异常导致锁无法正常释放
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        if (isLock) {
            valueOperations.set("name", "sumu");
            String name = (String) valueOperations.get("name");
            System.out.println("name=" + name);
            // 制造异常
            Integer.parseInt("sumu");
            redisTemplate.delete("k1");
        } else {
            System.out.println("有线程正在使用，请稍后再试！");
        }
    }

    /**
     * 将锁的值设置为随机值，防止在key失效时间内未完成操作导致锁被其他线程删除，使得每个线程只能删除自己的锁
     * 使用lua脚本，确保删除锁的三个步骤（获取锁，比对锁，删除所锁）是原子性的
     */
    @Test
    void testLock03() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        Boolean isLock = valueOperations.setIfAbsent("k1", value, 120, TimeUnit.SECONDS);
        if (isLock) {
            valueOperations.set("name", "sumu");
            String name = (String) valueOperations.get("name");
            System.out.println("name=" + name);
            System.out.println(valueOperations.get("k1"));
            Boolean result = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), value);
            System.out.println(result);
        } else {
            System.out.println("有线程正在使用，请稍后再试！");
        }
    }

}
