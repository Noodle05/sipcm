/**
 * 
 */
package com.sipcm.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Jack
 * 
 */
public abstract class AnnotationUtils {
	/**
	 * Get annotated fields from specific class. The field should not static
	 * 
	 * @param clazz
	 *            - The class that need to check
	 * @param annotationClass
	 *            - expecting annotation class that present on the fields
	 * @param fieldType
	 *            - expecting field type
	 * @return Array of fields. Return empty array if now field been find.
	 */
	public static Field[] getAnnotatedFields(Class<?> clazz,
			Class<? extends Annotation> annotationClass, Class<?> fieldType) {
		Class<?> cl = clazz;
		// Get all fields of class. We use getDeclaredFields to get
		// private/default/protected/public fields of class
		// As well as fields from super class.
		Collection<Field> fields = new ArrayList<Field>();
		while (cl != null) {
			for (Field f : cl.getDeclaredFields()) {
				fields.add(f);
			}
			cl = cl.getSuperclass();
		}
		Collection<Field> fs = new ArrayList<Field>(1);
		for (Field f : fields) {
			// If this field is not static
			// Has annotation present
			// And type is expecting fieldType
			if (f.isAnnotationPresent(annotationClass)
					&& !Modifier.isStatic(f.getModifiers())
					&& fieldType.isAssignableFrom(f.getType())) {
				fs.add(f);
			}
		}
		return fs.toArray(new Field[fs.size()]);
	}
}
