package com.android.launcher3;

import android.app.Application;

/**
 * 在launcher.java中定义了单例类LauncherAppState，调用
 * LauncherAppState.setApplicationContext(getApplicationContext());
 * LauncherAppState app = LauncherAppState.getInstance();
 * 由于launcher3z只有一个activity，LauncherApplication不需要调用
 */
public class LauncherApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LauncherAppState.setApplicationContext(this);
        LauncherAppState.getInstance();
    }
    /* 使用Application如果保存了一些不该保存的对象很容易导致内存泄漏。
    如果在Application的oncreate中执行比较 耗时的操作，将直接影响的程序的启动时间。
    不些清理工作不能依靠onTerminate完成，因为android会尽量让你的程序一直运行，
    所以很有可能 onTerminate不会被调用。
    当终止应用程序对象时调用，不保证一定被调用，当程序是被内核终止以便为其他应用程序释放
    资源，那么将不会提醒，并且不调用应用程序的对象的onTerminate方法而直接终止进程 */
    @Override
    public void onTerminate() {
        super.onTerminate();
        LauncherAppState.getInstance().onTerminate();
    }
    /* 当后台程序已经终止资源还匮乏时会调用这个方法。好的应用程序一般会在这个方法
    里面释放一些不必要的资源来应付当后台程序已经终止，前台应用程序内存还不够时的情况。 */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


}
