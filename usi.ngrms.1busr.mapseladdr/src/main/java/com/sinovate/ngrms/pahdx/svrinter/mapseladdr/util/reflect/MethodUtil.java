package com.sinovate.ngrms.pahdx.svrinter.mapseladdr.util.reflect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodUtil {
    private final static Logger logger = LoggerFactory.getLogger(MethodUtil.class);

    public static Method getSetter(Object object, String attrName, Class<?> paraType){
        String methodName = "set" + getMethodName(attrName);
        try {
            Method method = object.getClass().getMethod(methodName,paraType);
            return method;
        } catch (NoSuchMethodException e) {
            logger.error("获取setter方法失败:" + object.getClass() + ",方法为:" + methodName);
        }
        return null;
    }

    public static void setFieldValue(Object object, String attrName, Class<?> paraType, Object value) {
        Method method = getSetter(object,attrName,paraType);
        if(null == method)  return;
        method.setAccessible(true);

        try {
            method.invoke(object,value);
        } catch (IllegalAccessException e) {
            logger.error("访问setter方法非法:" + object.getClass() + ",方法为:" + method.getName());
        } catch (InvocationTargetException e) {
            logger.error("调用setter方法的目标类错误:" + object.getClass() + ",方法为:" + method.getName());
        } catch (IllegalArgumentException e) {
            logger.error("调用setter方法时，参数类型不一致：" + object.getClass() + "，方法为: " + method.getName() + ", 参数类型为：" + paraType.getName() + ", 参数值类型为：" + value.getClass());
        }
    }

    private static String getMethodName(String attrName) {
        if( null == attrName || attrName.isEmpty() )    return null;
        return attrName.substring(0,1).toUpperCase() + attrName.substring(1);
    }
}
