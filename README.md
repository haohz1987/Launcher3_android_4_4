Launcher3
=========
[博客地址](http://blog.csdn.net/dingfengnupt88/article/details/51800057?locationNum=15)

>流程图

1.结构模块

![img](https://github.com/haohz1987/Launcher3_android_4_4/blob/modify/img/1%E7%BB%93%E6%9E%84%E6%A8%A1%E5%9D%97_launcher3.png)

2.launcher视图

![img](https://github.com/haohz1987/Launcher3_android_4_4/blob/modify/img/2%E8%A7%86%E5%9B%BE_launcher3.png)

3.状态

![img](https://github.com/haohz1987/Launcher3_android_4_4/blob/modify/img/3%E7%8A%B6%E6%80%81_launcher3.png)

4.启动主流程的加载

![img](https://github.com/haohz1987/Launcher3_android_4_4/blob/modify/img/4launcher%E5%90%AF%E5%8A%A8%E4%B8%BB%E6%B5%81%E7%A8%8B%E7%9A%84%E5%8A%A0%E8%BD%BD_launcher3.png)

5.workspace的加载绑定流程

![img](https://github.com/haohz1987/Launcher3_android_4_4/blob/modify/img/5workspace%E7%9A%84%E5%8A%A0%E8%BD%BD%E7%BB%91%E5%AE%9A%E6%B5%81%E7%A8%8B_launcher3.png)

6.AlApps和Widget的加载流程

![img](https://github.com/haohz1987/Launcher3_android_4_4/blob/modify/img/6AllApps%E5%92%8CWidget%E7%9A%84%E5%8A%A0%E8%BD%BD%E6%B5%81%E7%A8%8B_launcher3.png)

>类的定义

Launcher：主界面Activity，最核心且唯一的Activity。

LauncherAppState：单例对象，构造方法中初始化对象、注册应用安装、卸载、更新，配置变化等广播。这些广播用来实时更新桌面图标等，其receiver的实现在LauncherModel类中，LauncherModel也在这里初始化。

LauncherModel：数据处理类，保存桌面状态，提供读写数据库的API，内部类LoaderTask用来初始化桌面。

InvariantDeviceProfile：一些不变的设备相关参数管理类，其内部包涵了横竖屏模式的DeviceProfile。

WidgetPreviewLoader：存储Widget信息的数据库，内部创建了数据库widgetpreviews.db。

LauncherAppsCompat：获取已安装App列表信息的兼容抽象基类，子类依据不同版本API进行兼容性处理。

AppWidgetManagerCompat：获取AppWidget列表的兼容抽象基类，子类依据不同版本API进行兼容性处理。

LauncherStateTransitionAnimation：各类动画总管处理执行类，负责各种情况下的各种动画效果处理。

IconCache：图标缓存类，应用程序icon和title的缓存，内部类创建了数据库app_icons.db。

LauncherProvider：核心数据库类，负责launcher.db的创建与维护。

LauncherAppWidgetHost：AppWidgetHost子类，是桌面插件宿主，为了方便托拽等才继承处理的。

LauncherAppWidgetHostView：AppWidgetHostView子类，配合LauncherAppWidgetHost得到HostView。

LauncherRootView：竖屏模式下根布局，继承了InsettableFrameLayout，控制是否显示在状态栏等下面。

DragLayer：一个用来负责分发事件的ViewGroup。

DragController：DragLayer只是一个ViewGroup，具体的拖拽的处理都放到了DragController中。

BubblTextView：图标都基于他，继承自TextView。

DragView：拖动图标时跟随手指移动的View。

Folder：打开文件夹展示的View。

FolderIcon：文件夹图标。

DragSource/DropTarget：拖拽接口，DragSource表示图标从哪开始拖，DropTarget表示图标被拖到哪去。


ItemInfo：桌面上每个Item的信息数据结构，包括在第几屏、第几行、第几列、宽高等信息；
该对象与数据库中记录一一对应；该类有多个子类，
譬如FolderIcon的FolderInfo、BubbleTextView的ShortcutInfo等。

>类的加载过程

Launcher3的Activity加载其实和其他应用没啥区别的，也是一样的流程，只是我们需要特别注意上图中红色的两步。
在setContentView之后我们其实又进行了一次依据设备属性的layout操作，接着才进行异步数据加载的，
所以我们的重点会放在LauncherModel的loader方法中。

在启动Launcher时数据加载绑定其实分了两大类，workspace与allApps（widgets）的加载，
他们都是通过异步加载回调UI绑定数据的

>读取数据库，获取需要加载的应用快捷方式和AppWidget

 整个读取的过程是在一个同步代码块中，在此之前我们先看几个重要的全局变量，

  sBgWorkspaceItems--保存ItemInfo
  sBgAppWidgets--保存AppWidget
  sBgFolders--存放文件夹
  sBgItemsIdMap--保存ItemInfo和其Id
  sBgDbIconCache--应用图标
  sBgWorkspaceScreens--保存Workspace

  a)遍历cursor，读取每一个app信息，根据itemType不同类型，分类保存到刚才的几个变量中。分这几种类型：ITEM_TYPE_APPLICATION、ITEM_TYPE_SHORTCUT、ITEM_TYPE_SHORTCUT、ITEM_TYPE_APPWIDGET
  b)读取完数据库之后，将需要移除和更新的item进行移除和更新；
  c)读取workspace screen数据库信息，如果有未使用过的则将其从数据库中移除。

>内存信息分析

查看内存信息：adb shell dumpsys meminfo com.android.launcher3
Memory Tracker也称memtrack，是一个hal层的库，不同平台库的名称不同，实现方式也有差异。
Memory Tracker 主要目标是能够跟踪以任何其他方式无法跟踪的内存，例如由进程分配但未映射到进程地址空间的纹理内存。
第二个目标是能够将进程使用的内存分类为GL，graphics等。所有的内存大小应该在实际的内存使用情况下，考虑到stride，bit depth，page size等。
EGL mtrack/GL mtrack这两项的数据就是通过memtrack获取的。

adb shell dumpsys meminfo 查看总共的EGL mtrack数值。
adb shell dumpsys meminfo surfaceflinger 查看SurfaceFlinger的值
adb shell dumpsys meminfo system_server
adb shell dumpsys meminfo com.android.systemui
adb shell dumpsys meminfo com.meizu.flyme.launcher
adb shell dumpsys meminfo com.android.systemui:recents

分别查看system_server、launcher、systemui、recent的数据,4类加和即为总内存

adb shell dumpsys meminfo实现原理

![img](https://github.com/haohz1987/Launcher3_android_4_4/blob/modify/img/7adb%20shell%20dumpsys%20meminfo%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86.png)

