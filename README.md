# ScriptEngine
线上脚本执行引擎

---

0.1.1版本支持在服务器上跑**JavaScript**和**Groovy**两种脚本语言. 只要在项目环境中配置了一个SpringBean, Engine便会跟随应用一起启动一个RMI服务, 然后就可以在Engine的后台向这个应用发送JS、Groovy脚本执行, 为了提高框架的易用性, 在脚本执行前会将Spring内所有Bean作为全局变量导入到脚本环境供脚本调用, 因此非常适合做线上数据订正、手动加载等后门操作.

## 引入
- pom.xml
```
<dependency>
    <groupId>com.vdian.script</groupId>
    <artifactId>script-engine</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```
- Spring Context
```
<bean class="com.vdian.script.engine.ScriptServerImpl">
    <!-- 业务分组: 如果要区分测试/预发/线上环境, 请区分配置 -->
    <constructor-arg name="group" value="feedcenter"/>
    <!-- 加密salt, 用于保证数据传输的正确性 -->
    <constructor-arg name="salt" value="jldsjflsjkfj1"/>
    <!-- zk集群地址 -->
    <constructor-arg name="zkConnectString" value="10.1.101.60:2181,10.1.101.60:2182,10.1.101.60:2183"/>
</bean>
```
