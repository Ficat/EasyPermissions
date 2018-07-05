package com.ficat.easypermissions;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ficat on 2018/5/12.
 */

public class EasyPermissions {
    static final String TAG = "PermissionFragment";
    private PermissionsFragment mPermissionsFragment;
    private static List<String> mRegisteredInManifestPermissions;

    public EasyPermissions(@NonNull Activity activity) {
        mPermissionsFragment = getPermissionsFragment(activity);
        if (mRegisteredInManifestPermissions == null) {
            mRegisteredInManifestPermissions = getRegisteredInManifestPermissions(activity);
        }
    }

    private List<String> getRegisteredInManifestPermissions(Activity activity) {
        if (activity == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = packageInfo.requestedPermissions;
            //permissions will be null if no permissions registered in Manifest.xml
            if (permissions != null) {
                list.addAll(Arrays.asList(permissions));
            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return list;
    }

    private PermissionsFragment getPermissionsFragment(Activity activity) {
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
            if (!mRegisteredInManifestPermissions.contains(p)) {
                throw new IllegalStateException("the permission \"" + p + "\" is not registered in manifest.xml");
            }
        }
    }
}
