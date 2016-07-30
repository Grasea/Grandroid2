package com.grasea.grandroid.mvp.model;

import android.content.Context;
import android.content.SharedPreferences;


import com.grasea.grandroid.sample.database.FaceData;
import com.grasea.grandroid.sample.database.GenericHelper;
import com.grasea.grandroid.sample.database.Identifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import graneric.ObjectTypeHandler;
import graneric.ProxyObject;

/**
 * Created by Rovers on 2016/5/7.
 */
public class Model extends ProxyObject {
    private static Context context;

    static {
        bindAnnotationHandler(Model.class);
    }

    public static void init(Context context) {
        Model.context = context;
    }

    public Model() {
    }

    public Model(Object entity) {
        super(entity);
    }

    @Put()
    protected static boolean put(final Annotation ann, Method m, Object[] args) {
        final SharedPreferences sp = context.getSharedPreferences("default", Context.MODE_PRIVATE);
        if (args.length > 0) {
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
                protected Boolean onObject(Object value) {
                    return false;
                }
            }.process();
        }
        return false;
    }

    @Get()
    protected static Object get(final Annotation ann, Method m, Object[] args) {
        final SharedPreferences sp = context.getSharedPreferences("default", Context.MODE_PRIVATE);
        return new ObjectTypeHandler(m.getReturnType()) {
            @Override
            protected Object onBoolean(Boolean value) {
                return sp.getBoolean(((Get) ann).value(), ((Get) ann).defaultValue().getBooleanValue());
            }

            @Override
            protected Object onString(String value) {
                return sp.getString(((Get) ann).value(), ((Get) ann).defaultValue().getStringValue());
            }

            @Override
            protected Object onInt(Integer value) {
                return sp.getInt(((Get) ann).value(), ((Get) ann).defaultValue().getIntValue());
            }

            @Override
            protected Object onDouble(Double value) {
                return (double) sp.getFloat(((Get) ann).value(), ((Get) ann).defaultValue().getFloatValue());
            }

            @Override
            protected Object onFloat(Float value) {
                return sp.getFloat(((Get) ann).value(), ((Get) ann).defaultValue().getFloatValue());
            }

            @Override
            protected Object onLong(Long value) {
                return sp.getLong(((Get) ann).value(), ((Get) ann).defaultValue().getLongValue());
            }
        }.process();
    }

    @Save()
    protected static boolean save(final Annotation ann, Method m, Object[] args) {
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
    protected static Object query(final Annotation ann, Method m, Object[] args) {
        final FaceData fd = new FaceData(context, "default");
        final Class objClass = ((Query) ann).value();
        String where = ((Query) ann).where();
        if (args!=null && args.length > 0) {
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
