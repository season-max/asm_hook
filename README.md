#plugin_handleThread 线程优化
## 使用方式
```
   //项目根目录
   buildscript {
      repositories {
        maven {
          url "https://plugins.gradle.org/m2/"
        }
      }
      dependencies {
        classpath "io.github.season-max:plugins:0.954"
      }
      
      // module 目录
      apply plugin: "io.github.season-max.optimizedThread"
      handle_thread_config {
            printLog = true
            enableThreadPoolOptimized = true
            enableScheduleThreadPoolOptimized = true
      }
   
   dependencies {
       implementation 'com.github.season-max.asm_hook:optimize_thread:v1.2'
   }
}
   ```



# plugin_privacy : 扫描隐私合规相关的方法调用并 hook

## 使用方式

### 引入工具类
工具类主要提供了 hook 隐私合规相关方法的工具类及提供写入文件的工具类。需要在 application 中的 attachBaseContext 时传入文件路径（或者在调用隐私合规方法之前），用来记录调用合规方法的堆栈信息。
   ```
    @Override
       protected void attachBaseContext(Context base) {
           super.attachBaseContext(base);
           ConfigGlobal.getInstance().setStoreDirectory(base.getExternalCacheDir().getAbsolutePath());
       }
   ```

### 接入插件
1.在你的根项目中添加依赖。有两种方式

一是添加远程依赖
   ```
   //项目根目录
   buildscript {
      repositories {
        maven {
          url "https://plugins.gradle.org/m2/"
        }
      }
      dependencies {
        classpath "io.github.season-max:plugins:0.954"
      }
      
      // module 目录
      apply plugin: "io.github.season-max.privacycheck"
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
   
   dependencies {
       implementation 'com.github.season-max.asm_hook:toolsLibrary:v1.2'
   }
}
   ```

二是将插件项目的仓库倒入本地 repo 中依赖。笔者建议第二种方式接入，每个项目的生产环境不同，可以针对自己的项目做修改。

2.项目编译时会扫描所有的方法，在根目录下生成 **replaceInsn.txt** 文件，记录隐私合规相关方法的位置。在项目运行期间会在 **getExternalCacheDir()**
   目录下生成记录调用隐私合规方法的调用栈。

## 实现思路

1. 定义**AsmMethodReplace**注解，用来标记想要 hook 的隐私合规的相关方法。
2. 在 **AnnotationParserTransform.groovy** 中扫描项目，将含有配置注解的方法指令记录下来，包括调用方法指令的 **opcode**、**owner**、**
   name**、**descriptor**
3. 在**PrivacyMethodReplaceTransform.groovy**
   中，在和记录下来的注解指令匹配的方法指令前插入写文件方法，用来在运行时记录在文件中，同时将包含隐私合规方法的类和调用方法也写入文件中，存储在项目的根目录下。
