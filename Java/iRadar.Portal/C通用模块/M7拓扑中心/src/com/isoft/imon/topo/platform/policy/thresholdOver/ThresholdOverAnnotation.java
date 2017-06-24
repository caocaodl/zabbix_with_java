package com.isoft.imon.topo.platform.policy.thresholdOver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { java.lang.annotation.ElementType.FIELD })
public @interface ThresholdOverAnnotation {
	public abstract int severity() default (int) 1;

	public abstract boolean enabled() default true;

	public abstract int upgradeUpper() default (int) 2;

	public abstract int violateUpper() default (int) 2;

	public abstract double threshold() default 90.0;

	public abstract java.lang.String format() default "#";

}