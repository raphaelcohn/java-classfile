// The MIT License (MIT)
//
// Copyright © 2016, Raphael Cohn <raphael.cohn@stormmq.com>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.stormmq.java.parsing.utilities.names;

import com.stormmq.java.parsing.utilities.names.parentNames.AbstractParentName;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.*;

import static com.stormmq.functions.MapHelper.useMapValue;
import static com.stormmq.functions.MapHelper.useMapValueOrGetDefault;
import static com.stormmq.java.parsing.utilities.StringConstants.ExternalTypeNameSeparator;
import static com.stormmq.java.parsing.utilities.StringConstants.ExternalTypeNameSeparatorString;
import static com.stormmq.java.parsing.utilities.names.parentNames.AbstractParentName.namespaceSplitter;
import static com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName.knownReferenceTypeName;

public final class PackageName extends AbstractParentName
{
	@NotNull public static final BiConsumer<PackageName, Consumer<String>> NamespaceSplitter = AbstractParentName.namespaceSplitter();
	@NotNull public static final BiFunction<PackageName, String, String> NamespaceWithSimpleTypeNameJoiner = (packageName, simpleTypeName) -> packageName.fullyQualifiedNameUsingDotsAndDollarSigns + '.' + simpleTypeName;

	@NotNull private static final ConcurrentMap<String, PackageName> cache = new ConcurrentHashMap<>(4096);
	@NotNull public static final PackageName NoPackageName = cache("");
	@NotNull public static final PackageName JavaLangPackageName = cache("java.lang");

	@NotNull
	private static PackageName cache(@NonNls @NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns)
	{
		return cache.computeIfAbsent(fullyQualifiedNameUsingDotsAndDollarSigns, PackageName::new);
	}

	@NotNull
	public static PackageName packageName(@NonNls @NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns)
	{
		final Supplier<PackageName> ifAbsent = () -> cache(fullyQualifiedNameUsingDotsAndDollarSigns);
		return useMapValueOrGetDefault(cache, fullyQualifiedNameUsingDotsAndDollarSigns, ifAbsent);
	}

	private PackageName(@NonNls @NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns)
	{
		super(fullyQualifiedNameUsingDotsAndDollarSigns, false);
	}

	@NotNull
	@Override
	public KnownReferenceTypeName child(@NonNls @NotNull final String simpleTypeName)
	{
		return KnownReferenceTypeName.child(fullyQualifiedNameUsingDotsAndDollarSigns, simpleTypeName, ExternalTypeNameSeparatorString);
	}

	@Override
	@NotNull
	public KnownReferenceTypeName childOrSame(@NonNls @NotNull final String anyPartiallyQualifiedTypeName)
	{
		if (anyPartiallyQualifiedTypeName.isEmpty())
		{
			throw new IllegalArgumentException("anyPartiallyQualifiedTypeName should not be empty");
		}
		if (fullyQualifiedNameUsingDotsAndDollarSigns.isEmpty())
		{
			return knownReferenceTypeName(anyPartiallyQualifiedTypeName);
		}
		return knownReferenceTypeName(fullyQualifiedNameUsingDotsAndDollarSigns + ExternalTypeNameSeparator + anyPartiallyQualifiedTypeName);
	}
}
