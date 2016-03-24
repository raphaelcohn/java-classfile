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

import com.stormmq.java.parsing.utilities.StringConstants;
import com.stormmq.java.parsing.utilities.names.parentNames.AbstractParentName;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.stormmq.java.parsing.utilities.StringConstants.ExternalTypeNameSeparator;
import static com.stormmq.java.parsing.utilities.StringConstants.ExternalTypeNameSeparatorString;
import static com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName.knownReferenceTypeName;

public final class PackageName extends AbstractParentName
{
	@NotNull private static final ConcurrentMap<String, PackageName> cache = new ConcurrentHashMap<>(4096);
	@NotNull public static final PackageName NoPackageName = cache("");
	@NotNull public static final PackageName JavaLangPackageName = cache("java.lang");

	@NotNull
	private static PackageName cache(@NonNls @NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns)
	{
		final PackageName potentialDuplicate = new PackageName(fullyQualifiedNameUsingDotsAndDollarSigns);
		@Nullable final PackageName previous = cache.putIfAbsent(fullyQualifiedNameUsingDotsAndDollarSigns, potentialDuplicate);
		if (previous == null)
		{
			return potentialDuplicate;
		}
		return previous;
	}

	@NotNull
	public static PackageName packageName(@NonNls @NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns)
	{
		@Nullable final PackageName packageName = cache.get(fullyQualifiedNameUsingDotsAndDollarSigns);
		if (packageName != null)
		{
			return packageName;
		}

		return cache(fullyQualifiedNameUsingDotsAndDollarSigns);
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
