package zyxhj.utils;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例构造器
 *
 */
public class Singleton {

	private static ConcurrentHashMap<Class<?>, Object> map = new ConcurrentHashMap<>();

	public static synchronized <T> T ins(Class<T> clazz) throws Exception {
		Object ret = map.get(clazz);
		if (ret == null) {
			ret = clazz.newInstance();
			map.put(clazz, ret);
		}
		// System.out.println("-------");
		return (T) ret;
	}

	public static synchronized <T> T ins(Class<T> clazz, Object... objects) throws Exception {
		Object ret = map.get(clazz);
		if (ret == null) {
			Class<?>[] types = new Class<?>[objects.length];
			for (int i = 0; i < objects.length; i++) {
				types[i] = objects[i].getClass();
			}
			Constructor<T> con = clazz.getConstructor(types);
			ret = con.newInstance(objects);
			map.put(clazz, ret);
		}
		// System.out.println("-------");
		return (T) ret;
	}

}
