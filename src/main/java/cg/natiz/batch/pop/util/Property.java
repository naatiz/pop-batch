package cg.natiz.batch.pop.util;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE, METHOD, FIELD, PARAMETER})
@Qualifier
@Retention(RUNTIME)
public @interface Property {
	/**
	 * Bundle key
	 * 
	 * @return a valid bundle key or ""
	 */
	@Nonbinding
	String key() default "";

	/**
	 * Is it a mandatory property
	 * 
	 * @return true if mandatory
	 */
	@Nonbinding
	boolean mandatory() default false;

	/**
	 * Default value if not provided
	 * 
	 * @return default value or ""
	 */
	@Nonbinding
	String defaultValue() default "";
}
