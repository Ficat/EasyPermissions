package com.ficat.easypermissions;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ficat on 2018-05-12.
 */

public abstract class RequestPublisher<T> {
    protected String[] mPermissions;
    protected PermissionsFragment mPermissionsFragment;
    protected List<RequestSubscriber<T>> mRequestSubscribers = new ArrayList<>();
    protected List<Permission> mResults = new ArrayList<>();

    RequestPublisher(@NonNull String[] permissions, @NonNull PermissionsFragment fragment) {
        this.mPermissions = permissions;
        this.mPermissionsFragment = fragment;
        request();
    }

    void onRequestPermissionsResult(Permission permission) {
        if (!contains(permission.name)) {
            return;
        }
        if (!hasResult(permission.name)) {
            mResults.add(permission);
        }
        onRequestResult(permission);
    }

    public void subscribe(RequestSubscriber<T> subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("RequestSubscriber is null");
        }
        mRequestSubscribers.add(subscriber);
    }

    protected abstract void onRequestResult(Permission permission);

    /**
     * 结果发布给所有订阅者
     */
    protected void publish(T t) {
        for (int i = 0; i < mRequestSubscribers.size(); i++) {
            RequestSubscriber<T> subscriber = mRequestSubscribers.get(i);
            if (subscriber != null) {
                subscriber.onPermissionsRequestResult(t);
            }
        }
    }

    /**
     * 所有请求权限是否都有了请求结果
     */
    protected boolean hasAllResult() {
        for (String p : mPermissions) {
            boolean hasResult = hasResult(p);
            if (!hasResult) {
                return false;
            }
        }
        return true;
    }

    /**
     * 权限是否已经有了请求结果
     */
    private boolean hasResult(String permission) {
        if (TextUtils.isEmpty(permission)) {
            return false;
        }
        for (int i = 0; i < mResults.size(); i++) {
            boolean hasResult = mResults.get(i).name.equals(permission);
            if (hasResult) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断权限是否包含在本次请求的权限中
     */
    private boolean contains(String permission) {
        if (TextUtils.isEmpty(permission)) {
            return false;
        }
        for (String p : mPermissions) {
            if (p.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 请求权限
     */
    private void request() {
        mPermissionsFragment.requestPermissions(mPermissions, this);
    }
}
