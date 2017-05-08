package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by joryun on 2017/5/8.
 */
public class RedisDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    //从缓存得到seckill
    public Seckill getSeckill(long seckillId) {

        //redis操作逻辑
        try {

            Jedis jedis = jedisPool.getResource();
            try {

                String key = "seckill:" + seckillId;

                //并没有实现内部序列化操作
                //get -> byte[] -> 反序列化 -> Object(Seckill)
                //采用自定义序列化
                //protostuff : pojo
                byte[] bytes = jedis.get(key.getBytes());

                //从缓存获取到
                if (bytes != null) {
                    //空对象
                    Seckill seckill = schema.newMessage();
                    ProtobufIOUtil.mergeFrom(bytes, seckill, schema);
                    return seckill;
                }

            } finally {
                jedis.close();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }


    //若缓存不存在，则存入缓存
    public String putSeckill(Seckill seckill) {

        //set Object(Seckill) -> 序列化 -> byte[]
        try {

            Jedis jedis = jedisPool.getResource();

            try {

                String key = "seckill:" + seckill.getSeckillId();
                //LinkedBuffer：缓存器
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

                //超时缓存
                int timeout = 60 * 60;  //1小时
                String result = jedis.setex(key.getBytes(), timeout, bytes);

                return result;

            } finally {
                jedis.close();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
