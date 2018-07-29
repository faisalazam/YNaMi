package pk.lucidxpo.ynami.testutils;

import org.slf4j.Logger;
import org.springframework.aop.framework.Advised;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.apache.commons.lang3.ArrayUtils.removeElement;
import static org.mockito.Mockito.mock;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Reflection based helper providing accessors for private fields
 */
public class ReflectionHelper {
    private static final Logger logger = getLogger(ReflectionHelper.class);
    private static final String MODIFIERS = "modifiers";

    /**
     * @param clazz     Class of target object
     * @param object    target object
     * @param fieldName name of field on target object
     * @return value of field
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    public static Object getField(final Class clazz, final Object object, final String fieldName) throws IllegalArgumentException,
            IllegalAccessException, SecurityException, NoSuchFieldException {
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * @param object    target object
     * @param fieldName name of field on target object
     * @return value of field
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    public static Object getField(final Object object, final String fieldName) throws IllegalArgumentException,
            IllegalAccessException, SecurityException, NoSuchFieldException {
        final Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * @param clazz      Class of target object
     * @param object     target object
     * @param fieldName  name of field on target object
     * @param fieldValue value to set on field
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    public static void setField(final Class clazz, Object object, final String fieldName, final Object fieldValue)
            throws Exception, SecurityException, NoSuchFieldException {

        object = unwrap(object);
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        final Field modifiersField = Field.class.getDeclaredField(MODIFIERS);
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(object, fieldValue);
    }

    public static void setFieldIfPresent(final Object object, final String fieldName, final Object fieldValue) {
        try {
            setField(object, fieldName, fieldValue);
        } catch (Exception e) {
            logger.info("can't set field " + fieldName + " on object " + object.getClass() + ". Exception:" + e.getMessage());
        }
    }

    public static void setField(Object object, final String fieldName, final Object fieldValue) throws Exception {
        object = unwrap(object);
        final Class clazz = object.getClass();
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        final Field modifiersField = Field.class.getDeclaredField(MODIFIERS);
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(object, fieldValue);
    }

    public static Field[] getFields(final Class clazz, final Class requiredClass) throws Exception {
        Field[] allFields = clazz.getDeclaredFields();
        for (final Field field : allFields) {
            if (!field.getType().equals(requiredClass)) {
                allFields = removeElement(allFields, field);
            }
        }
        return allFields;
    }

    /**
     * Modifies constant's value (generally it is "public static final .." )
     *
     * @param clazz
     * @param fieldName
     * @param fieldValue
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setStaticFinalField(final Class clazz, final String fieldName, final Object fieldValue) throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField(MODIFIERS);
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, fieldValue);
    }

    /**
     * Invokes a private method on object
     *
     * @param object
     * @param methodName
     * @param argClasses
     * @param argument
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invokePrivateMethod(final Object object, final String methodName, final Class<?>[] argClasses, final Object... argument)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Method tagMethod = makePrivateMethodAccessible(object, methodName, argClasses);
        return tagMethod.invoke(object, argument);
    }

    /**
     * Sets a private method to accessible
     *
     * @param object
     * @param methodName
     * @param argClasses
     * @return
     * @throws NoSuchMethodException
     */
    public static Method makePrivateMethodAccessible(final Object object, final String methodName, final Class... argClasses) throws NoSuchMethodException {

        Method tagMethod;
        try {
            tagMethod = object.getClass().getDeclaredMethod(methodName, argClasses);
        } catch (NoSuchMethodException e) {

            if (object.getClass().getSuperclass() != null) {
                tagMethod = object.getClass().getSuperclass().getDeclaredMethod(methodName, argClasses);
            } else {
                throw e;
            }
        }

        tagMethod.setAccessible(true);
        return tagMethod;
    }

    /**
     * Unwrap concrete class from proxies
     *
     * @param proxiedInstance
     * @return
     * @throws Exception
     */
    public static Object unwrap(final Object proxiedInstance) throws Exception {
        if (proxiedInstance instanceof Advised) {
            return unwrap(((Advised) proxiedInstance).getTargetSource().getTarget());
        }
        return proxiedInstance;
    }


    public static Logger createMockLoggerInClass(Class clazz) {
        Logger mockLogger = mock(Logger.class);
        try {
            setStaticFinalField(clazz, "LOGGER", mockLogger);
            return mockLogger;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void restoreLoggerInClass(Class clazz) throws Exception {
        setStaticFinalField(clazz, "LOGGER", getLogger(clazz));
    }
}
