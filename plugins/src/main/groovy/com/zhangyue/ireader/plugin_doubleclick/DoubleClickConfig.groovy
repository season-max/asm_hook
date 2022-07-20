package com.zhangyue.ireader.plugin_doubleclick

import com.zhangyue.ireader.util.CommonUtil

/**
 * 配置类
 */
class DoubleClickConfig {

    public static String ViewDescriptor = "Landroid/view/View;"

    /**
     * 处理快速点击的工具类
     */
    public static String doubleClickCheckClass = CommonUtil.fileSeparatorName("com.zhangyue.ireader.toolslibrary.doubleclick.DoubleClickConfig")
    public static String doubleClickCheckMethod = "inDoubleClick"
    public static String doubleClickCheckMethodDesc = "(Landroid/view/View;J)Z"


    String checkAnnotation

    String annotationName

}
