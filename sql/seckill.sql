/*
 Navicat MySQL Data Transfer

 Source Server         : joryunMac
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : localhost
 Source Database       : seckill

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : utf-8

 Date: 05/23/2017 17:21:40 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `seckill`
-- ----------------------------
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill` (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品库存ID',
  `name` varchar(120) NOT NULL COMMENT '商品名称',
  `number` int(11) NOT NULL COMMENT '库存数量',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '秒杀开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- ----------------------------
--  Records of `seckill`
-- ----------------------------
BEGIN;
INSERT INTO `seckill` VALUES ('1000', '1000元秒杀iphone6', '98', '2017-05-07 19:55:10', '2017-05-08 00:00:00', '2017-03-21 13:48:11'), ('1001', '800元秒杀ipad', '197', '2017-05-07 00:15:06', '2017-05-08 00:00:00', '2017-03-21 13:48:11'), ('1002', '6600元秒杀mac book pro', '300', '2017-04-23 10:18:24', '2017-04-25 00:00:00', '2017-03-21 13:48:11'), ('1003', '7000元秒杀iMac', '400', '2017-04-23 10:18:26', '2017-04-25 00:00:00', '2017-03-21 13:48:11');
COMMIT;

-- ----------------------------
--  Table structure for `success_killed`
-- ----------------------------
DROP TABLE IF EXISTS `success_killed`;
CREATE TABLE `success_killed` (
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀商品ID',
  `user_phone` bigint(20) NOT NULL COMMENT '用户手机号',
  `state` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- ----------------------------
--  Records of `success_killed`
-- ----------------------------
BEGIN;
INSERT INTO `success_killed` VALUES ('1000', '10086100861', '0', '2017-04-23 10:27:36'), ('1000', '10086100862', '0', '2017-04-23 10:31:00'), ('1000', '10086861000', '-1', '2017-05-23 17:21:07'), ('1001', '10086100861', '0', '2017-05-06 23:30:49'), ('1001', '10086100862', '0', '2017-04-23 10:48:32'), ('1001', '10086100863', '0', '2017-05-07 00:15:06'), ('1001', '10086861001', '0', '2017-05-23 17:21:20');
COMMIT;

-- ----------------------------
--  Procedure structure for `execute_seckill`
-- ----------------------------
DROP PROCEDURE IF EXISTS `execute_seckill`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `execute_seckill`(IN v_seckill_id BIGINT, IN v_phone BIGINT,
    IN v_kill_time TIMESTAMP, OUT r_result INT)
BEGIN
    DECLARE insert_count INT DEFAULT 0;
    START TRANSACTION ;

    INSERT IGNORE INTO success_killed
    (seckill_id, user_phone, create_time)
      VALUES (v_seckill_id, v_phone, v_kill_time);
    SELECT row_count INTO insert_count;

    IF (insert_count = 0) THEN
      ROLLBACK ;
      SET r_result = -1;
    ELSEIF (insert_count < 0) THEN
      ROLLBACK ;
      SET r_result = -2;
    ELSE
      UPDATE seckill
        SET number = number - 1
      WHERE seckill_id = v_seckill_id
        AND end_time > v_kill_time
        AND start_time < v_kill_time
        AND number > 0;
      SELECT row_count INTO insert_count;

      IF (insert_count = 0) THEN
        ROLLBACK ;
        SET r_result = 0;
      ELSEIF (insert_count < 0) THEN
        ROLLBACK ;
        SET r_result = -2;
      ELSE
        COMMIT ;
        SET r_result = 1;
      END IF ;

    END IF ;

  END
 ;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
