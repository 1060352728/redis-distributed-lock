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

    public static final String REQUID = UUID.randomUUID().toString();

    @RequestMapping("/stevalues")
    public void setRedisValues(Jedis jedis){
        jedis.set("number","10000");
    }

    @RequestMapping("/test")
    public void testRedisDistributedLock(Jedis jedis) throws Exception {
        this.test(jedis);
    }

    public void test(Jedis jedis) throws Exception {
        boolean flag = RedisTool.tryGetDistributedLock(jedis,"123",REQUID,100000);
        if (flag) {
            System.out.println("获取锁成功+++++++++++++++++++++++++++++++++++++++++++++++++");
            Integer vaule = Integer.parseInt(jedis.get("number"));
            vaule = vaule -1;
            jedis.set("number",vaule.toString());
            boolean flag2 = RedisTool.releaseDistributedLock(jedis,"123",REQUID);
            if(flag2){
                System.out.println("解决成功+++++++++++++++++++++++++++++++++++++++++++++++++");
            }
        }else{
            Thread.sleep(3000);
            System.out.println("获取锁失败---------------------------------------------------");
        }
    }
}
