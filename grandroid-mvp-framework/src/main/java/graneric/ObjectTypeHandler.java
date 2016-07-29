package graneric;

import com.grasea.grandroid.mvp.model.Put;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rovers on 2016/7/29.
 */
public abstract class ObjectTypeHandler<T> {
    protected Object value;
    protected Class type;

    public ObjectTypeHandler(Class type) {
        this.type = type;
    }

    public ObjectTypeHandler(Object value) {
        this.value = value;
        type = value.getClass();
    }

    public T process() {
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return onBoolean((Boolean) value);
        } else if (type.equals(String.class)) {
            return onString((String) value);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return onInt((Integer) value);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return onDouble((Double) value);
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            return onFloat((Float) value);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return onLong((Long) value);
        } else if (type.equals(ArrayList.class)) {
            return onList((ArrayList) value);
        } else {
            return onObject(value);
        }
    }

    protected T onBoolean(Boolean value) {
        return null;
    }

    protected T onString(String value) {
        return null;
    }

    protected T onInt(Integer value) {
        return null;
    }

    protected T onDouble(Double value) {
        return null;
    }

    protected T onFloat(Float value) {
        return null;
    }

    protected T onLong(Long value) {
        return null;
    }

    protected T onList(ArrayList list) {
        return null;
    }

    protected T onObject(Object value) {
        return null;
    }
}
