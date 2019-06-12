package com.ficat.easypermissions;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ficat.easypermissions.bean.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ficat on 2018-05-12.
 */
public abstract class BaseRequestPublisher<T extends BaseRequestPublisher.Subscriber> {
    protected String[] mPermissions;
    protected PermissionsFragment mPermissionsFragment;
    protected List<T> mSubscribers;
    protected List<Permission> mResults;
    protected RequestAgainListener mRequestAgainListener;
    protected boolean mAutoRequestAgain;

    BaseRequestPublisher(@NonNull String[] permissions, @NonNull PermissionsFragment fragment) {
        this.mPermissions = permissions;
        this.mPermissionsFragment = fragment;
        this.mSubscribers = new ArrayList<>();
        this.mResults = new ArrayList<>();

        request();
    }

    void onRequestPermissionsResult(Permission permission) {
        if (!contains(permission.name)) {
            return;
        }
        if (!hasResult(permission.name)) {
            mResults.add(permission);
        }
    }

    public void subscribe(T subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("BaseRequestSubscriber is null");
        }
        if (!mSubscribers.contains(subscriber)) {
            mSubscribers.add(subscriber);
        }
    }

    /**
     * Return true if all permissions we request have the request results
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
     * Return true if the permission has the request result
     */
    private boolean hasResult(String permission) {
        if (TextUtils.isEmpty(permission)) {
            return false;
        }
        for (Permission p : mResults) {
            if (p.name.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if permissions we request contain specific permission
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
     * Request permissions
     */
    private void request() {
        mPermissionsFragment.requestPermissions(mPermissions, this);
    }

    public interface Subscriber {

    }

    public interface RequestAgainListener {
        void requestAgain(String[] needAndCanRequestAgainPermissions);
    }
}
