# EasyPermissions
一个及其简洁的安卓runtime权限库<br>
特点如下：
* 链式操作
* 若请求的权限未在manifest中注册，将抛出明确的异常
* 自动重试（可配置），配置该选项后若请求被拒但用户未勾选不再提示框时会自动重试直到用户授权或勾选不再提示框

## Gradle依赖

1.在项目根build.gradle中添加

```gradle
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```

2.在需要依赖本库的build.gradle中添加

```gradle
dependencies {
    implementation 'com.github.Ficat:EasyPermissions:v2.1.0'
}
```
## 使用

```java
//requestEach方式
EasyPermissions
       .with(activity)
       .requestEach(Manifest.permission.CAMERA)
       .result(new RequestEachExecutor.ResultReceiver() {
           @Override
           public void onPermissionsRequestResult(Permission permission) {
               String name = permission.name;
               if (permission.granted) {
                   //name权限被授予
               } else {
                   if (permission.shouldShowRequestPermissionRationale) {
                       //name权限被拒绝但用户未勾选不再提示框，可继续请求
                   } else {
                       //name权限被拒绝且用户勾选了不再提示框
                       //此时不能再次请求了，而需要user前往设置界面手动授权
                       EasyPermissions.goToSettingsActivity(activity);
                   }
               }
           }
       });


//request方式，请求的所有权限被用户授权后返回true，否则返回false  
EasyPermissions
        .with(activity)
        .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .autoRetryWhenUserRefuse(true, new BaseRequestExecutor.RequestAgainListener() {//是否自动重试
            @Override
            public void requestAgain(String[] needAndCanRequestAgainPermissions) {
                //该监听回调中传入的是再次请求的权限，用以在重新请求时弹出说明框等信息（如
                //向用户说明为何要使用该权限）
                for (String s : needAndCanRequestAgainPermissions) {
                    Log.e("TAG", "request again permission = "+s);
                }
            }
        })
        .result(new RequestExecutor.ResultReceiver() {
            @Override
            public void onPermissionsRequestResult(boolean grantAll, List<Permission> results) {
                if (grantAll) {
                    Toast.makeText(MainActivity.this, "request permissions success!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "request permissions fail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
```

## 致谢
[RxPermissions](https://github.com/tbruyelle/RxPermissions "go to rxpermissions")

## License

```
Copyright 2018 Ficat

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


