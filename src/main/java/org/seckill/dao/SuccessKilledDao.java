package org.seckill.dao;

import org.seckill.entity.SuccessKilled;

/**
 * Created by joryun on 2017/3/21.
 */
public interface SuccessKilledDao {

    /**
     * 插入购买明细，可过滤重复(数据表success_killed使用联合主键)
     *
     * @param seckillId
     * @param userPhone
     * @return 插入行数
     */
    int insertSuccessKilled(long seckillId, long userPhone);

    /**
     * 根据id查询SuccessKilled并携带秒杀商品对象实体
     *
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(long seckillId);
}
