package com.isoft.framework.core.concurrent;

import java.util.concurrent.Callable;

public interface WaterfallCallable extends Callable<Callable<?>> {}