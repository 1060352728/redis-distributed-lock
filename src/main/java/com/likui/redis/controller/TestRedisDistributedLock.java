package com.likui.redis.controller;

import com.likui.redis.utils.RedisTool;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import java.util.UUID;

/**
 * @Auther: likui
 * @Date: 2019/2/28 19:24
 * @Description: 测试redis分布式锁
 */
@RestController
public class TestRedisDistributedLock {


    @RequestMapping("/stevalues")
    public void setRedisValues(Jedis jedis){
        jedis.set("number","1000");
    }

    @RequestMapping("/test")
    public void testRedisDistributedLock(Jedis jedis) {
        this.test(jedis);
    }

    public void test(Jedis jedis) {
        String requestId = UUID.randomUUID().toString();
        boolean flag = RedisTool.tryGetDistributedLock(jedis,"123",requestId,10000);
        if (flag) {
            System.out.println("获取锁成功+++++++++++++++++++++++++++++++++++++++++++++++++");
            Integer vaule = Integer.parseInt(jedis.get("number"));
            vaule = vaule -1;
            jedis.set("number",vaule.toString());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {

            }
            boolean flag2 = RedisTool.releaseDistributedLock(jedis,"123",requestId);
            if(flag2){
                System.out.println("解决成功+++++++++++++++++++++++++++++++++++++++++++++++++");
            }
        }else{
            System.out.println("获取锁失败---------------------------------------------------");
        }
    }
}
