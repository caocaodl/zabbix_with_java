package com.isoft.imon.topo.platform.policy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AnalysableAnnotation {
	public abstract String label() default "";

	public abstract String enLabel() default "";

	public abstract String unit() default "";

	public abstract String component() default "";

	public abstract String alertSourceClazz() default "IMS";
}
