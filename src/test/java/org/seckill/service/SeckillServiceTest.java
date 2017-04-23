package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


/**
 * Created by joryun on 2017/4/18.
 */

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})

public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}", list);
//        System.out.println(list);

    }

    @Test
    public void getById() throws Exception {
        long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
    }

    /*
    注意：集成测试覆盖的完备性：
    （1）商品未开启或者已结束，测试
    （2）商品秒杀时间已开启，测试
    （3）商品重复秒杀，测试

    将两个单元测试块合并到一起测试：
    首先展示接口秒杀地址，当开启时执行秒杀，未开启则报出warn。且try catch包裹后允许重复执行！！！
     */
    @Test
    public void exportSeckillLogic() throws Exception {
        long id = 1001L;
        Exposer exposer = seckillService.exportSeckillUrl(id);

        if (exposer.isExposed()){
            logger.info("exposer={}", exposer);

//            long id = 1000L;
            long phone = 10086100862L;
//            String md5 = "784bb0a7963b3bf3cfc2fb3fdede630e";
            String md5 = exposer.getMd5();

            try {

                SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
                logger.info("result={}", execution);

            } catch (RepeatKillException e){
                logger.error(e.getMessage());

            } catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        } else {
            //秒杀未开启
            logger.warn("exposer={}", exposer);
        }


    }


//    @Test
//    public void executeSeckill() throws Exception {
//
//        /*
//        重复执行异常：
//            org.seckill.exception.RepeatKillException: seckill repeated
//         */

//        /*
//        result=SeckillExecution{
//        seckillId=1000,
//        state=1,
//        stateInfo='秒杀成功',
//        successKilled=SuccessKilled{
//            seckillId=1000,
//            userPhone=10086100862,
//            state=0,
//            createTime=Sun Apr 23 10:31:00 CST 2017
//            }
//        }
//         */
//    }

}