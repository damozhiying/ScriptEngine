package com.vdian.script.client.utils;

/**
 * @author jifang
 * @since 2016/11/19 上午10:25.
 */
public class Pair<L, R> {
    private L left;

    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
