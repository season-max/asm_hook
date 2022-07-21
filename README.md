# plugin_privacy : 扫描隐私合规相关的方法调用并 hook



## 使用方式

1. 在 **toolsLibrary ** moudle中，通过 assembleDebug 打出 aar，将 aar 拷贝到你需要使用的项目中，并依赖。该项目中主要提供了 hook 隐私合规相关方法的工具类及提供写入文件的工具类。需要在 application 中的 attachBaseContext 时注册 context。

   ```java
    @Override
       protected void attachBaseContext(Context base) {
           super.attachBaseContext(base);
           ConfigGlobal.getInstance().setContext(this);
       }
   ```

   

2. 在你的根项目中添加依赖。有两种方式，一是添加远程依赖

   ```groovy
   maven { url 'https://maven.pkg.github.com/season-max/asm_hook'
               credentials {
                   // 用户名和token
                   username = season-max
                   password = my_token
               }
           }
   ```

   ```groovy
   classpath 'com.sason-max.gradle.plugins:plugin_privacy:1.0.0'
   ```

   token 可以发送邮件到 seasonsnoe@gmail.com 申请。

   二是将插件项目的仓库倒入本地 repo 中依赖

3. 在 module 的 **build.gradle**中添加

   ```
   //隐私合规
   apply plugin: 'privacy.track'
   privacy_plugin_config {
   		//是否打印日志
       isDebug = false
       //注解类的全限定名
       handle_annotation_desc = 'com.zhangyue.ireader.asm_annotation.sentry_privacy.AsmMethodReplace'
       //白名单
       exclude = ['android.support', 'androidx']
       //用来写入文件的工具类，开发者可以自己定义，但是方法描述必须为 (Ljava/lang/String;)V
       recordOwner = "com.zhangyue.ireader.toolslibrary.Util"
       recordMethod = "writeToFile"
       recordDesc = "(Ljava/lang/String;)V"
   }
   
   ```

4. 项目编译时会扫描所有的方法，在根目录下生成 **replaceInsn.txt** 文件，记录隐私合规相关方法的位置。在项目运行期间会在 **getExternalCacheDir()** 目录下生成记录调用隐私合规方法的调用栈。



## 实现思路

1. 定义**AsmMethodReplace**注解，用来标记想要 hook 的隐私合规的相关方法。
2. 在 **AnnotationParserTransform.groovy** 中扫描项目，将含有配置注解的方法指令记录下来，包括调用方法指令的 **opcode**、**owner**、**name**、**descriptor**
3. 在**PrivacyMethodReplaceTransform.groovy** 中，在和记录下来的注解指令匹配的方法指令前插入写文件方法，用来在运行时记录在文件中，同时将包含隐私合规方法的类和调用方法也写入文件中，存储在项目的根目录下。