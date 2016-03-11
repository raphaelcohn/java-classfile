package com.stormmq.java.parsing.fileParsers.caches;

import org.jetbrains.annotations.NotNull;

public interface CacheMaker
{
	@NotNull
	<V> Cache<V> makeCache();
}
