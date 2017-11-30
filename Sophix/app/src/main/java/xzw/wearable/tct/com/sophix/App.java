package xzw.wearable.tct.com.sophix;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Created by ${zhiwei.xu} on 2017/11/30.
 */

public class App extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();
        checkUpgrade();
    }

    private void checkUpgrade()
    {
        String version = getLocalVersion(this);
        SophixManager.getInstance().setContext(this)
                .setAppVersion(version)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener()
                {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion)
                    {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS)
                        {
                            // 表明补丁加载成功
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH)
                        {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                        } else
                        {
                            // 其它错误信息, 查看PatchStatus类说明
                        }
                    }
                }).initialize();
        // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
        SophixManager.getInstance().queryAndLoadNewPatch();
    }

    public static String getLocalVersion(Context ctx)
    {
        String localVersion = "";
        try
        {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
            Log.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return localVersion;
    }
}
