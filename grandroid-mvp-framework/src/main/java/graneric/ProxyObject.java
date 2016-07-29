package graneric;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

import graneric.annotation.Anno;
import graneric.annotation.ProxyMethod;

/**
 * Created by Rovers on 2016/7/29.
 */
public class ProxyObject implements java.lang.reflect.InvocationHandler {
    protected Object entity;
    protected static HashMap<Class<? extends Annotation>, Method> annoMap;

    public ProxyObject() {
    }

    public ProxyObject(Object entity) {
        this.entity = entity;
    }

    public static void bindAnnotationHandler(Class c) {
        annoMap = Anno.scanMethod(c);
    }

    public static <T> T reflect(Class<T> c) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(
                c.getClassLoader(),
                new Class[]{c},
                new ProxyObject());
    }

    public static <T> T reflect(Class<T> c, Object entity) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(
                c.getClassLoader(),
                new Class[]{c},
                new ProxyObject(entity));
    }

    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Annotation[] anns = m.getAnnotations();
        Object[] v = args;
        if (m.isAnnotationPresent(ProxyMethod.class)) {
            if (entity == null) {
                throw new NullPointerException();
            }
            return m.invoke(entity, v);
        } else {
            Object result = null;
            if (annoMap == null) {
                bindAnnotationHandler(this.getClass());
            }
            for (int i = 0; i < anns.length; i++) {
                if (annoMap.containsKey(anns[i].annotationType())) {
                    try {

                        result = annoMap.get(anns[i].annotationType()).invoke(null, anns[i], m, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    result = executeAnnotationAction(anns[i], m, v);
                }
                v = new Object[]{result};
            }
            return result;
        }
        //result = m.invoke(obj, args);
    }

    protected Object executeAnnotationAction(Annotation ann, Method m, Object[] args) {
        return null;
    }
}
