package com.vdian.script.engine;

import com.vdian.script.client.constant.ScriptType;
import com.vdian.script.client.service.IScriptService;
import com.vdian.script.client.utils.ScriptUtils;

import java.util.Iterator;

/**
 * @author jifang
 * @since 2016/11/19 上午11:54.
 */
public class EngineTest {

    private static final String zkAddr = "10.1.101.60:2181,10.1.101.60:2182,10.1.101.60:2183";

    private static final String salt = "jldsjflsjkfj1";

    public static void main(String[] args) throws Exception {
        Iterator<String> iterator = ScriptUtils.getEngineNodes(zkAddr, "/feedcenter");
        Iterator<IScriptService> serviceIterator = ScriptUtils.getEngineServices(iterator);
        while (serviceIterator.hasNext()) {
            IScriptService service = serviceIterator.next();
            System.out.println(service.invokeScript(ScriptType.Groovy, "1+1", ScriptUtils.encodeMd5("1+1", salt)));
        }
    }
}
