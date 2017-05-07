# 高并发秒杀系统
开发环境：IDEA，Tomcat，MySQL，Redis

项目构建：Maven

软件环境：SSM(SpringMVC，Spring，MyBatis) 

项目描述：一套以秒杀商品为目的而搭建制作的高并发系统。基本实现用户根据商家设定的库存量进行秒杀的过程。

技术描述：基于SpringMVC，Spring，MyBatis实现的高并发秒杀系统。代码设计风格基于RESTful，以c3p0作为连接池，Redis数据库为媒介实现高并发技术。其中，对于相关的DAO，Service操作，均添加了Junit单元测试实例。

### 开发文档

#### 一、业务分析

1.秒杀系统业务流程

![](https://github.com/Joryun/MarkdownPhotos/blob/master/seckillPhotos/seckill-operation.png)

2.秒杀业务的核心：库存的处理

3.针对库存业务分析：事务（1>.减内存 2>.记录购买明细）

![](https://github.com/Joryun/MarkdownPhotos/blob/master/seckillPhotos/seckill-operation2.png)

4.记录秒杀成功信息

（1）购买成功的对象

（2）成功的时间/有效期

（3）付款/发货信息

#### 二、异常情况分析

1. 减库存没有记录购买明细

2. 记录明细但没有减库存

3. 出现超卖/少卖

#### 三、难点分析

1. MySQL：事务 + 行级锁

2. 多用户秒杀 ——> Update库存数量

#### 四、功能模块

1. 秒杀接口暴露（Exposer，封装的DTO）

2. 执行秒杀

3. 相关查询

#### 五、开发流程

1. DAO设计编码

2. Service设计编码

3. Web设计编码（restful接口和前端交互等）

4. 高并发优化与分析

--------------

<font color=gray size=4>（一）DAO设计编码</font>

Package:

（1）org.seckill.dao

（2）org.seckill.entity

#### 1. 接口设计与SQL编写

注：Dao层不应夹杂着Service层的信息，Service层主要是对Dao层进行拼接，即为一系列逻辑！！！

#### 2. 数据库设计与编码

两张表：

（1）	seckill	//秒杀库存表

（2）	success_killed		//秒杀成功明细表


#### 3. DAO实体和接口编码

（1）	SeckillDao	

（2）	SuccesskilledDao		

#### 4. Mybatis整合Spring

（1）	编写mybatis-config.xml（全局配置）

（2）	编写spring-dao.xml（配置dataSource，sqlSessionFactory等）

#### 5. 完成Dao层集成测试（使用Junit4）


<font color=gray size=4>（二）Service设计编码
</font>


Package:

（1）org.seckill.service 存放服务，即为一系列逻辑

（2）org. seckill.exception	存放service接口所需要的异常，如重复秒杀，秒杀与关闭等

（3）org. seckill.dto 数据传输层，与entity类似，存放一些表示数据的类型，web与service间的数据传递

（4）org. seckill.enums 封装枚举类，表述常量字段-状态值（“秒杀成功”，“秒杀结束”等等）

#### 1. 接口设计与实现

（1）业务接口：站在“使用者”角度设计接口

（2）三个方面：方法定义粒度，参数，返回类型（return 类型/异常）


#### 2. 使用Spring托管Service依赖（Spring IOC）

![](https://github.com/Joryun/MarkdownPhotos/blob/master/seckillPhotos/service-2(1).png)

(1) 业务对象依赖图

![](https://github.com/Joryun/MarkdownPhotos/blob/master/seckillPhotos/service-2(2).png)

(2) 编写spring-service.xml

(3) 扩展

* Spring IOC

（1）	为对象创建统一托管

（2）	规范的生命周期管理

（3）	灵活的依赖注入

（4）	一致的获取对象

* Spring IOC注入方式及场景

（1）	XML：

一.Bean实现类来自第三方类库，如DataSource等；

二.需要命名空间配置，如context，aop，mvc等

（2）	注解：项目中自身开发使用的类，可直接在代码中使用注解，如：@Service，@Controller

（3）	Java配置类：需要通过代码控制对象创建逻辑的场景，如：自定义修改依赖类库


#### 3. 配置并使用Spring声明式事务

ProxyFactoryBean + XML   ——>   早期使用方式（2.0）

tx:advice + aop           ——>   一次配置永久生效

注解@Transactional       ——>   注解控制（推荐）

* 什么时候回滚事务？

抛出的是运行期异常（RuntimeException）
避免使用不当的try...catch...

* 使用注解控制事务方法的优点

(1) 开发团队达成一致约定，明确标注事务方法的编程风格

(2) 保证事务方法的执行时间尽可能短，不要穿插其它网络操作，RPC/HTTP请求或者剥离到事务方法外部

(3) 不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
	

#### 4. 完成Service集成测试（使用Junit4）



<font color=gray size=4>（三）Web设计编码
</font>

#### 1. 前端交互逻辑

(1) 秒杀系统前端页面流程

![](https://github.com/Joryun/MarkdownPhotos/blob/master/seckillPhotos/web-1(1).png)


(2) 详情页流程逻辑

![](https://github.com/Joryun/MarkdownPhotos/blob/master/seckillPhotos/web-1(2).png)

#### 2. 基于RestFul接口设计

秒杀API的URL设计

* GET	/seckill/list	（秒杀列表）
* GET	/seckill/{id}/detail	（详情页）
* GET	/seckill/time/now	（系统时间）
* POST	  /seckill/{id}/exposer	（暴露秒杀）
* POST  /seckill/{id}/{md5}/execution	（执行秒杀）

#### 3. 整合SpringMVC框架及相关配置

（1）编写web.xml

（2）添加spring-web.xml

#### 4. 实现秒杀相关的Restful接口

（1）编写SeckillController

（2）创建一个DTO类SeckillResult，封装所有ajax请求返回类型（json）


#### 5. 基于Bootstrap框架开发页面

(1) 抽取公共部分，开发common包下的内容

![](https://github.com/Joryun/MarkdownPhotos/blob/master/seckillPhotos/web-5(1).png)

* head.jsp  ->  bootstrap中包含于head标签之内的内容，一般为css，编码设置及主题文件
* tag.jsp   ->  引入库文件，例如jstl库中的fmt等

(2) 开发商品列表页list.jsp

(3) 开发商品详情页detail.jsp

#### 6. Cookie登录交互

（1）编写js代码（基于模块化的js代码），完成登录验证（重点：验证手机号）

（2）完成弹出层组件的逻辑设计


#### 7. 计时交互

(1) 判断时间（已开始，未开始，已结束）

(2) 根据时间判断，对应前端组件显示不同内容

#### 8. 秒杀交互

（1）绑定按钮点击事件（one click），预防用户连续点击

（2）考虑浏览器计时偏差

（3）显示秒杀的结果



<font color=gray size=4>（四）高并发优化与分析
</font>

###待续。。。



