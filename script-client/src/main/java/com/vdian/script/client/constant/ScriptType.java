package com.vdian.script.client.constant;

/**
 * @author jifang
 * @since 2016/11/19 上午9:20.
 */
public enum ScriptType {
    JavaScript("JavaScript"),
    Groovy("Groovy");

    ScriptType(String type) {
        this.type = type;
    }

    private String type;

    public String getValue() {
        return this.type;
    }
}
