package com.vdian.script.client.service;


import com.vdian.script.client.constant.ScriptType;

import javax.script.ScriptException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author jifang
 * @since 16/10/17 下午4:11.
 */
public interface IScriptService extends Remote {

    String invokeScript(ScriptType type, String script, byte[] md5) throws RemoteException, ScriptException;
}
