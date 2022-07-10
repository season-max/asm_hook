- plugin_privacy : hook 隐私合规相关的方法调用

## 使用方式

1. 在 **toolsLibrary moudle**中，通过 assembleDebug 打出 aar，将 aar 拷贝到你需要使用的项目中，并依赖

2. ​	在你的根项目中添加
`
        // 该包所在的GitHub Repository地址
        maven { url 'https://maven.pkg.github.com/season-max/asm_hook'
            credentials {
                // 用户名和token
                println "--->username:${githubProperties.getProperty("username")},-->token_key:${githubProperties.getProperty("token_key")}"
                username = season-max
                password = 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
            }
        }` 
        ，并添加 `com.sason-max.gradle.plugins:plugin_privacy:1.0.0` 构建依赖

3. 在 module 中添加

   ```groovy
   apply plugin: 'privacy.track'
   privacy_plugin_config {
       isDebug = false
       handle_annotation_desc = 'Lcom/zhangyue/ireader/asm_annotation/AsmMethodReplace;'
       inject = true
       exclude = ['android.support', 'androidx']
   }
   ```

4. 在项目运行之后，会在项目的根目录生成 **replaceInsn.txt** 文件，用来记录被 hook 的类及相应的字节码



