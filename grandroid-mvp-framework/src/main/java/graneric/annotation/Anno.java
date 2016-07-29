package graneric.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Rovers on 2016/7/29.
 */
public class Anno {
    public static HashMap<Class<? extends Annotation>, Method> scanMethod(Class c) {
        HashMap<Class<? extends Annotation>, Method> map = new HashMap<>();
        Method[] ms = c.getDeclaredMethods();
        for (Method m : ms) {
            Annotation[] anns = m.getAnnotations();
            for (Annotation ann : anns) {
                map.put(ann.annotationType(), m);
            }
        }
        return map;
    }

}
