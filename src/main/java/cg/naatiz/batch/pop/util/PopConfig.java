package cg.naatiz.batch.pop.util;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;

@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Qualifier
@Retention(RUNTIME)
public @interface PopConfig {

	/**
	 * configuration filenames
	 * 
	 * @return array of relative filenames
	 */
	@Nonbinding
	String[] value() default { "/pop.cfg" };
}
