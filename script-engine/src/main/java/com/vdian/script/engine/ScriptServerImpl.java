package com.vdian.script.engine;

import com.alibaba.fastjson.JSON;
import com.vdian.script.client.constant.Constant;
import com.vdian.script.client.constant.ScriptType;
import com.vdian.script.client.service.IScriptService;
import com.vdian.script.client.utils.Pair;
import com.vdian.script.client.utils.ScriptUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.script.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author jifang
 * @since 16/10/9 下午6:05.
 */
public class ScriptServerImpl extends UnicastRemoteObject implements IScriptService, InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("com.vdian.script.engine");
    private final Bindings bindings = new SimpleBindings();
    private String zkConnectString;
    private String group;
    private String salt;
    private Registry registry;

    private CuratorFramework client;

    private String path;

    public ScriptServerImpl(String zkConnectString, String group, String salt) throws RemoteException {
        ScriptUtils.checkSaltSecurity(salt);
        this.zkConnectString = zkConnectString;
        this.group = group.startsWith("/") ? group : "/" + group;
        this.salt = salt;
    }

    @Override
    @PostConstruct
    public void afterPropertiesSet() throws Exception {

        // <ip, port>
        Pair<String, Integer> pair = ScriptUtils.getServiceAddr();
        this.registry = LocateRegistry.createRegistry(pair.getRight());
        this.registry.bind(Constant.ENGINE_SERVICE_NAME, this);
        LOGGER.info("ScriptEngineService Started .... ip:{}, port:{}", pair.getLeft(), pair.getRight());

        // register zk
        this.client = CuratorFrameworkFactory
                .builder()
                .connectString(zkConnectString)
                .retryPolicy(new RetryNTimes(5, 3000))
                .namespace(Constant.ENGINE_SERVICE_NAME_SPACE)
                .build();

        this.client.start();

        this.path = this.group + "/" + pair.getLeft() + ":" + pair.getRight();
        this.client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path);

        LOGGER.info("ScriptEngineService Registered, path:{}", path);
    }

    @Override
    public String invokeScript(ScriptType type, String script, byte[] md5) throws RemoteException, ScriptException {
        if (ScriptUtils.isEqual(script, salt, md5)) {
            ScriptEngineManager manager = new ScriptEngineManager();
            manager.setBindings(loadScriptContext());

            ScriptEngine engine = manager.getEngineByName(type.getValue());
            Object result = engine.eval(script);

            LOGGER.info("script invoke success, result: {}", result);

            return JSON.toJSONString(result);
        } else {
            return "your script is not security";
        }
    }

    private Bindings loadScriptContext() {
        if (bindings.isEmpty()) {
            synchronized (bindings) {
                if (bindings.isEmpty()) {
                    WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
                    String[] beanNames = context.getBeanDefinitionNames();
                    for (String beanName : beanNames) {
                        bindings.put(beanName, context.getBean(beanName));
                    }
                }
            }
        }
        return bindings;
    }

    @Override
    @PreDestroy
    public void destroy() throws Exception {
        this.client.delete().forPath(this.path);
        this.client.close();
        LOGGER.info("ScriptEngineService un Registered, group:{}", group);

        this.registry.unbind(Constant.ENGINE_SERVICE_NAME);
        LOGGER.info("ScriptEngineService Stop .... name:{}", Constant.ENGINE_SERVICE_NAME);
    }
}
