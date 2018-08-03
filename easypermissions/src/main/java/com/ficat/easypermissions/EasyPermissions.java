package com.ficat.easypermissions;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ficat on 2018/5/12.
 */

public class EasyPermissions {
    static final String TAG = "PermissionFragment";
    private PermissionsFragment mPermissionsFragment;
    private static List<String> sRegisteredInManifestPermissions;

    public EasyPermissions(@NonNull Activity activity) {
        mPermissionsFragment = getPermissionsFragment(activity);
        if (sRegisteredInManifestPermissions == null) {
            sRegisteredInManifestPermissions = getRegisteredInManifestPermissions(activity);
        }
    }

    private List<String> getRegisteredInManifestPermissions(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = packageInfo.requestedPermissions;
            //permissions will be null if no permissions registered in Manifest.xml
            if (permissions == null || permissions.length == 0) {
                throw new IllegalStateException("there is no any permission registered in manifest.xml");
            }
            return Collections.unmodifiableList(Arrays.asList(permissions));
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError("package name not be found");
        }
    }

    private synchronized PermissionsFragment getPermissionsFragment(Activity activity) {
        PermissionsFragment permissionsFragment = (PermissionsFragment) activity.getFragmentManager().findFragmentByTag(TAG);
        if (permissionsFragment == null) {
            permissionsFragment = new PermissionsFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(permissionsFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return permissionsFragment;
    }

    public RequestPublisher<Boolean> request(String... permissions) {
        checkPermissions(permissions);
        return new RequestPublisher<Boolean>(permissions, mPermissionsFragment) {
            @Override
            public void subscribe(RequestSubscriber<Boolean> subscriber) {
                super.subscribe(subscriber);
                if (hasAllResult()) {
                    for (Permission p : mResults) {
                        if (!p.granted) {
                            subscriber.onPermissionsRequestResult(false);
                            return;
                        }
                    }
                    subscriber.onPermissionsRequestResult(true);
                }
            }

            @Override
            protected void onRequestResult(Permission permission) {
                if (hasAllResult()) {
                    for (Permission p : mResults) {
                        if (!p.granted) {
                            publish(false);
                            return;
                        }
                    }
                    publish(true);
                }
            }
        };
    }

    public RequestPublisher<Permission> requestEach(String... permissions) {
        checkPermissions(permissions);
        return new RequestPublisher<Permission>(permissions, mPermissionsFragment) {
            @Override
            public void subscribe(RequestSubscriber<Permission> subscriber) {
                super.subscribe(subscriber);
                for (Permission p : mResults) {
                    subscriber.onPermissionsRequestResult(p);
                }
            }

            @Override
            protected void onRequestResult(Permission permission) {
                publish(permission);
            }

        };
    }

    private void checkPermissions(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("the permissions is null or there are no input permissions");
        }
        for (String p : permissions) {
            if (!sRegisteredInManifestPermissions.contains(p)) {
                throw new IllegalStateException(String.format("the permission %1$s is not registered in manifest.xml", p));
            }
        }
    }
}
