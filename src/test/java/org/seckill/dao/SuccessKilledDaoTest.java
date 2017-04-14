package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by joryun on 2017/4/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    //单元测试，连续两次插入时，第二次返回insertCount=0，由于SuccessKilledDao对应的xml文件设置了联合主键
    @Test
    public void testInsertSuccessKilled() throws Exception {

        long id = 1001L;
        long phone = 15521075723L;
        int insertCount = successKilledDao.insertSuccessKilled(id, phone);
        System.out.println("insertCount="+ insertCount);
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception {

        long id = 1001L;
        long phone = 15521075723L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);

        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }

}