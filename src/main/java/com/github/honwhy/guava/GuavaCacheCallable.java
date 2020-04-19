package com.github.honwhy.guava;

import java.util.concurrent.Callable;

public interface GuavaCacheCallable extends Callable {

    String getKey();
}
