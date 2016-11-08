import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {

    public static void assign(Object to, Object from) {
        Map<String, Method> getters = new HashMap<>();
        Class classFrom = from.getClass();
        Class classTo = to.getClass();
        Method[] methodsFrom = classFrom.getMethods();
        Method[] methodsTo = classTo.getMethods();
        for (Method method : methodsFrom) {
            if (isGetter(method)) getters.put(method.getName().substring(3), method);
        }
        for (Method method : methodsTo) {
            String key = method.getName().substring(3);
            if (isSetter(method) && getters.containsKey(key) && isCompatible(method, getters.get(key))) {
                try {
                    method.invoke(to, getters.get(key).invoke(from));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isGetter(Method method) {
        if (!method.getName().startsWith("get")) return false;
        if (method.getParameterTypes().length != 0) return false;
        if (void.class.equals(method.getReturnType())) return false;
        return true;
    }

    public static boolean isSetter(Method method) {
        if (!method.getName().startsWith("set")) return false;
        if (method.getParameterTypes().length != 1) return false;
        return true;
    }

    public static boolean isCompatible(Method setter, Method getter) {
        Class clazz = getter.getReturnType();
        while (clazz != null) {
            if (clazz == setter.getParameterTypes()[0]) return true;
            clazz = clazz.getSuperclass();
        }
        return false;
    }
}
