package com.isoft.framework.core.concurrent;

import java.util.concurrent.Callable;

/**
 * This interface is used to denote a WaterfallCallable that terminates the chain of 
 * execution by not returning a subsequent Callable&lt;Callable&lt;?&gt;&gt; value.
 */
public interface EndOfTheWaterfall extends Callable<Callable<Void>> {}