package com.oe.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
/**
 * class工具类
 * @author liaocheng
 *
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class ReflectionUtils {
	/**
	 * 设置对象中的属性，任何修饰符都可以设置，(无法设置final修饰的基本类型和字符串常量)
	 * @param clz 对象所属的类
	 * @param obj 对象,如果为静态属性，为可以为null
	 * @param name 变量名
	 * @param value 变量值
	 */
	public static void setFieldValue(Class clz,Object obj,String name,Object value){
		try {
			Field f = clz.getDeclaredField(name);
			//修改final修饰符
			Field modifierField = Field.class.getDeclaredField("modifiers");
			modifierField.setAccessible(true);
			modifierField.set(f, f.getModifiers() & ~Modifier.FINAL);
			//修改private修饰符
			f.setAccessible(true);
			f.set(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取对象中属性的值
	 * @param clz class
	 * @param obj 对象
	 * @param name 属性名
	 * @return
	 */
	public static <T> T getFieldValue(Class clz,Object obj,String name){
		try {
			Field f = clz.getDeclaredField(name);
			f.setAccessible(true);
			return (T)f.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
