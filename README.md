#SopHix热修复技术
==========



##简介
----------
阿里云移动热修复方案（Sophix）基于阿里巴巴首创hotpatch技术，提供最细粒度热修复能力，无需等待，实时修复应用线上问题


##设计理念
----------
Sophix的核心设计理念，就是非侵入性。
我们的打包过程不会侵入到apk的build流程中。我们所需要的，只有已经生成完毕的新旧apk，而至于apk是如何生成的——是Android Studio打包出来的、还是Eclipse打包出来的、或者是自定义的打包流程，我们一律不关心。在生成补丁的过程中间既不会改变任何打包组件，也不插入任何AOP代码，我们极力做到了——不添加任何超出开发者预期的代码，以避免多余的热修复代码给开发者带来困扰。


##方案对比
----------
![image](https://github.com/kuang511111/SophixTest/tree/master/Sophix/pic/duibi.png)
![image](https://github.com/kuang511111/SophixTest/raw/master/Sophix/pic/duibi.png)



##部署详解
----------

###1.	创建应用
----------
登录移动热修复控制台：https://hotfix.console.aliyun.com/，点击右上角创建App，跳转到Mobile Hub App管理控制台，或者直接访问Mobile Hub控制台：https://mhub.console.aliyun.com/。如下图：
![image](https://github.com/kuang511111/SophixTest/raw/master/Sophix/pic/chuanjianapp.png)

###2.	集成
----------

####(1)	添加maven仓库地址：
----------
1.	repositories {
2.	   maven {
3.	       url "http://maven.aliyun.com/nexus/content/repositories/releases"
4.	   }
5.	}
6.
####(2)	添加gradle坐标版本依赖：
----------
1.	compile 'com.aliyun.ams:alicloud-android-hotfix:3.1.6'
####(3)	添加权限：
----------
Sophix SDK使用到以下权限
1.	<! -- 网络权限 -->
2.	<uses-permission android:name="android.permission.INTERNET" />
3.	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
4.	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
5.	<! -- 外部存储读权限，调试工具加载本地补丁需要 -->
6.	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
####(4)	添加认证秘钥
----------
在AndroidManifest.xml中间的application节点下添加如下配置：
1.	<meta-data
2.	android:name="com.taobao.android.hotfix.IDSECRET"
3.	android:value="App ID" />
4.	<meta-data
5.	android:name="com.taobao.android.hotfix.APPSECRET"
6.	android:value="App Secret" />
7.	<meta-data
8.	android:name="com.taobao.android.hotfix.RSASECRET"
9.	android:value="RSA密钥" />
####(5)	初始化模块
----------
	// initialize最好放在attachBaseContext最前面，初始化直接在Application类里面，切勿封装到其他类
	SophixManager.getInstance().setContext(this)
	.setAppVersion(appVersion)
	.setAesKey(null)
	.setEnableDebug(true)
	.setPatchLoadStatusStub(new PatchLoadStatusListener() {
	@Override
	public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
	// 补丁加载回调通知
	if (code == PatchStatus.CODE_LOAD_SUCCESS) {
	// 表明补丁加载成功
	} else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
	// 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
	// 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
	} else {
	// 其它错误信息, 查看PatchStatus类说明
	}
	}
	}).initialize();

	// queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
	SophixManager.getInstance().queryAndLoadNewPatch();



##生成补丁
----------
补丁工具地址：
https://help.aliyun.com/document_detail/53247.html?spm=5176.doc53248.6.548.mby9GB
打开如下图：
![image](https://github.com/kuang511111/SophixTest/raw/master/Sophix/pic/duibi.png)

运行完后会生成
![image](https://github.com/kuang511111/SophixTest/raw/master/Sophix/pic/patchbao.png)

第一个版本代码为
![image](https://github.com/kuang511111/SophixTest/raw/master/Sophix/pic/daima1.png)

第二个版本代码为
![image](https://github.com/kuang511111/SophixTest/raw/master/Sophix/pic/daima2.png)

分别编译，生成patch包。


##发布补丁
----------
进入阿里云热修复页面，点击上传补丁，然后发布。
![image](https://github.com/kuang511111/SophixTest/raw/master/Sophix/pic/fabuding.png)


然后当应用调用了
SophixManager.getInstance().queryAndLoadNewPatch();
这个接口，就会自动去下载并且加载安装。
后台加载完成后，立马就可以看到效果。
这样就完成修复啦。


官方文档地址：
https://help.aliyun.com/document_detail/53240.html?spm=5176.doc53240.6.546.q1ernk

