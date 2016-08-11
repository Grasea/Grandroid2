package graneric;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import graneric.annotation.Anno;

/**
 * Created by Rovers on 2016/7/29.
 */
public class ProxyObject<S extends ProxyObject> implements java.lang.reflect.InvocationHandler {
    protected Class subjectInterface;
    protected static ConcurrentHashMap<Class<? extends Annotation>, Method> annoMap;

    protected ProxyObject(Class subjectInterface) {
        this.subjectInterface = subjectInterface;
    }

    public static void bindAnnotationHandler(Class c) {
        if (annoMap == null) {
            annoMap = new ConcurrentHashMap<>();
        }
        annoMap.putAll(Anno.scanMethod(c));
    }

    protected static <T> T reflect(Class<T> c, ProxyObject proxyObject) {
        T instance = (T) java.lang.reflect.Proxy.newProxyInstance(
                c.getClassLoader(),
                new Class[]{c},
                proxyObject);
        Annotation[] anns = c.getAnnotations();
        if (anns != null) {
            for (Annotation ann : anns) {
                proxyObject.onAnnotationDetected(ann);
            }
        }
        return instance;
    }

    protected void onAnnotationDetected(Annotation annotation) {
    }

    public Class getSubjectInterface() {
        return subjectInterface;
    }

    public boolean isAnnotationPresent(Class annotationType) {
        return subjectInterface.isAnnotationPresent(annotationType);
    }

    public <T> Annotation getAnnotation(Class<T> annotationType) {
        return subjectInterface.getAnnotation(annotationType);
    }

    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        //Log.d("grandroid", "got proxy method invoked");
        Object[] v = args;
        Annotation[] anns = m.getAnnotations();
        Object result = null;
        if (annoMap == null) {
            bindAnnotationHandler(this.getClass());
        }
        for (int i = 0; i < anns.length; i++) {
            if (annoMap.containsKey(anns[i].annotationType())) {
                //Log.d("grandroid", "try to invoke method " + annoMap.get(anns[i].annotationType()) + ", instance=" + this + ", ann=" + anns[i] + ", m=" + m);
                try {
                    result = annoMap.get(anns[i].annotationType()).invoke(null, (S) this, anns[i], m, v);

                } catch (Exception e) {
                    Log.e("grandroid", null, e);
                }
            } else {
                result = executeAnnotationAction((S) this, anns[i], m, v);
            }
            v = new Object[]{result};
        }
        return result;
        //result = m.invoke(obj, args);
    }

    protected Object executeAnnotationAction(S instance, Annotation ann, Method m, Object[] args) {
        return null;
    }
}
