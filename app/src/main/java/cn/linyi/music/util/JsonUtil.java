package cn.linyi.music.util;

import android.util.Log;

import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.PropertyDescriptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by linyi on 2016/4/19.
 */
public class JSONUtil  extends JSONObject{
    private static final String TYPE_STRING = "class java.lang.String";
    private  Class cla;
    //只支持对象的属性为int 或者String的类
    public JSONUtil(String s) throws JSONException {
        super(s);
    }
    public static  <E> E toObject(JSONObject j,E e) {
        Class  cla = e.getClass();
        Field[] fileds = e.getClass().getDeclaredFields();
        for (Field f : fileds) {
            PropertyDescriptor pd = null;//获取对象的
            try {
                pd = new PropertyDescriptor(f.getName(), cla);
                Method setMethod = pd.getWriteMethod();
                setMethod.invoke(e,j.get(f.getName()));//通过类反射将json转换为对象
            } catch (IntrospectionException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
        return e;
    }

}
