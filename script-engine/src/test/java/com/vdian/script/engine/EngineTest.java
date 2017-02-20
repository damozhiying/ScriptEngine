package com.vdian.script.engine;

import com.vdian.script.client.constant.Constant;
import com.vdian.script.client.constant.ScriptType;
import com.vdian.script.client.service.IScriptService;
import com.vdian.script.client.utils.ScriptUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

import java.rmi.Naming;
import java.util.Iterator;

/**
 * @author jifang
 * @since 2016/11/19 上午11:54.
 */
public class EngineTest {

    private static final String zkAddr = "139.129.9.166:2181";

    private static final String salt = "∞¶∞∞¶§∞¶§∞¶∞§";

    public static void main(String[] args) throws Exception {
        // register zk
        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString(zkAddr)
                .retryPolicy(new RetryNTimes(5, 3000))
                .namespace(Constant.ENGINE_SERVICE_NAME_SPACE)
                .build();

        client.start();

        Iterator<String> iterator = client.getChildren().forPath("/feedcenter-push-daily").iterator();
        while (iterator.hasNext()) {
            String node = iterator.next();
            String url = String.format(Constant.SERVICE_URL_PATTERN, node);
            IScriptService service = (IScriptService) Naming.lookup(url);

            String script = "1+1";
            byte[] md5 = ScriptUtils.encodeMd5(script, salt);

            String s = service.invokeScript(ScriptType.Groovy, script, md5);
            System.out.println(s);
        }
    }
}
