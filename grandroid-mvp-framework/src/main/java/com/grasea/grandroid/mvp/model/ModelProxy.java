package com.grasea.grandroid.mvp.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.grasea.grandroid.database.FaceData;
import com.grasea.grandroid.database.GenericHelper;
import com.grasea.grandroid.database.Identifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import graneric.ObjectTypeHandler;
import graneric.ProxyObject;

/**
 * Created by Rovers on 2016/5/7.
 */
public class ModelProxy extends ProxyObject<ModelProxy> {
    private static Context context;
    private static final ConcurrentHashMap<String, Object> objectMap = new ConcurrentHashMap<>();

    static {
        bindAnnotationHandler(ModelProxy.class);
    }

    public static void init(Context context) {
        ModelProxy.context = context;
    }

    public static <T> T reflect(Class<T> c) {
        return reflect(c, new ModelProxy(c));
    }

    protected ModelProxy(Class subjectInterface) {
        super(subjectInterface);
    }

    @Put()
    public static boolean put(ModelProxy instance, final Annotation ann, Method m, Object[] args) {
        if (args.length > 0) {
            switch (((Put) ann).storage()) {
                case Memory:
                    return new ObjectTypeHandler<Boolean>(args[0]) {
                        @Override
                        protected Boolean onBoolean(Boolean value) {
                            objectMap.put(((Put) ann).value(), value);
                            return true;
                        }

                        @Override
                        protected Boolean onString(String value) {
                            objectMap.put(((Put) ann).value(), value);
                            return true;
                        }

                        @Override
                        protected Boolean onInt(Integer value) {
                            objectMap.put(((Put) ann).value(), value);
                            return true;
                        }

                        @Override
                        protected Boolean onDouble(Double value) {
                            objectMap.put(((Put) ann).value(), value);
                            return true;
                        }

                        @Override
                        protected Boolean onFloat(Float value) {
                            objectMap.put(((Put) ann).value(), value);
                            return true;
                        }

                        @Override
                        protected Boolean onLong(Long value) {
                            objectMap.put(((Put) ann).value(), value);
                            return true;
                        }

                        @Override
                        protected Boolean onList(ArrayList list) {
                            objectMap.put(((Put) ann).value(), value);
                            return true;
                        }

                        @Override
                        protected Boolean onObject(Object value) {
                            objectMap.put(((Put) ann).value(), value);
                            return true;
                        }
                    }.process();
                case Preferences:
                    final SharedPreferences sp = context.getSharedPreferences("default", Context.MODE_PRIVATE);
                    return new ObjectTypeHandler<Boolean>(args[0]) {
                        @Override
                        protected Boolean onBoolean(Boolean value) {
                            return sp.edit().putBoolean(((Put) ann).value(), value).commit();
                        }

                        @Override
                        protected Boolean onString(String value) {
                            return sp.edit().putString(((Put) ann).value(), value).commit();
                        }

                        @Override
                        protected Boolean onInt(Integer value) {
                            return sp.edit().putInt(((Put) ann).value(), value).commit();
                        }

                        @Override
                        protected Boolean onDouble(Double value) {
                            return sp.edit().putFloat(((Put) ann).value(), value.floatValue()).commit();
                        }

                        @Override
                        protected Boolean onFloat(Float value) {
                            return sp.edit().putFloat(((Put) ann).value(), value).commit();
                        }

                        @Override
                        protected Boolean onLong(Long value) {
                            return sp.edit().putLong(((Put) ann).value(), value).commit();
                        }

                        @Override
                        protected Boolean onList(ArrayList list) {
                            return false;
                        }

                        @Override
                        protected Boolean onObject(Object value) {
                            return false;
                        }
                    }.process();
            }
        }
        return false;
    }

    @Get()
    public static Object get(ModelProxy instance, final Annotation ann, Method m, Object[] args) {
        final String key = ((Get) ann).value();
        switch (((Get) ann).storage()) {
            case Memory:
                if (objectMap.containsKey(key)) {
                    return objectMap.get(key);
                } else {
                    return null;
                }
            case Preferences:
                final SharedPreferences sp = context.getSharedPreferences("default", Context.MODE_PRIVATE);
                return new ObjectTypeHandler(m.getReturnType()) {
                    @Override
                    protected Object onBoolean(Boolean value) {
                        return sp.getBoolean(key, ((Get) ann).defaultValue().getBooleanValue());
                    }

                    @Override
                    protected Object onString(String value) {
                        return sp.getString(key, ((Get) ann).defaultValue().getStringValue());
                    }

                    @Override
                    protected Object onInt(Integer value) {
                        return sp.getInt(key, ((Get) ann).defaultValue().getIntValue());
                    }

                    @Override
                    protected Object onDouble(Double value) {
                        return (double) sp.getFloat(key, ((Get) ann).defaultValue().getFloatValue());
                    }

                    @Override
                    protected Object onFloat(Float value) {
                        return sp.getFloat(key, ((Get) ann).defaultValue().getFloatValue());
                    }

                    @Override
                    protected Object onLong(Long value) {
                        return sp.getLong(key, ((Get) ann).defaultValue().getLongValue());
                    }
                }.process();
            default:
                return null;
        }
    }

    @Save()
    public static boolean save(ModelProxy instance, final Annotation ann, Method m, Object[] args) {
        final FaceData fd = new FaceData(context, "default");
        if (args.length > 0) {
            return new ObjectTypeHandler<Boolean>(args[0]) {
                @Override
                protected Boolean onObject(Object obj) {
                    if (Identifiable.class.isInstance(obj)) {
                        Identifiable id = (Identifiable) obj;
                        if (id.get_id() == null || id.get_id() == 0) {
                            return new GenericHelper(fd, obj.getClass()).insert(id);
                        } else {
                            return new GenericHelper(fd, obj.getClass()).update(id);
                        }
                    }
                    return false;
                }

                @Override
                protected Boolean onList(ArrayList list) {
                    if (!list.isEmpty()) {
                        if (Identifiable.class.isInstance(list.get(0))) {
                            boolean result = true;
                            GenericHelper helper = new GenericHelper(fd, list.get(0).getClass());
                            for (Object obj : list) {
                                Identifiable id = (Identifiable) obj;
                                if (id.get_id() == 0) {
                                    result = result && helper.insert(id);
                                } else {
                                    result = result && helper.update(id);
                                }
                            }
                            return result;
                        }
                    } else {
                        //if list size is 0, then return true
                        return true;
                    }
                    return false;
                }
            }.process();
        } else {
            return false;
        }
    }

    @Query()
    public static Object query(ModelProxy instance, final Annotation ann, Method m, Object[] args) {
        final FaceData fd = new FaceData(context, "default");
        final Class objClass = ((Query) ann).value();
        String where = ((Query) ann).where();
        if (args != null && args.length > 0) {
            if (where.contains("_PARAM_")) {
                where = where.replaceAll("_PARAM_", args[0].toString());
            } else {
                where += args[0].toString();
            }
        }
        final String whereStr = where;
        return new ObjectTypeHandler(m.getReturnType()) {
            @Override
            protected Identifiable onObject(Object obj) {
                return new GenericHelper(fd, objClass).selectSingle(whereStr);
            }

            @Override
            protected ArrayList<Identifiable> onList(ArrayList list) {
                return new GenericHelper(fd, objClass).select(whereStr);
            }
        }.process();
    }
}
