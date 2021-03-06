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

package com.stormmq.java.parsing.utilities.names.parentNames;

import com.stormmq.java.parsing.utilities.InvalidJavaIdentifierException;
import com.stormmq.java.parsing.utilities.names.PackageName;
import com.stormmq.string.AbstractToString;
import com.stormmq.string.StringUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.stormmq.java.parsing.utilities.ReservedIdentifiers.validateIsJavaIdentifier;
import static com.stormmq.string.StringConstants.ExternalTypeNameSeparator;
import static com.stormmq.string.StringConstants.InternalTypeNameSeparatorString;
import static com.stormmq.java.parsing.utilities.names.PackageName.packageName;

public abstract class AbstractParentName extends AbstractToString implements ParentName
{
	@NotNull
	protected static <P extends AbstractParentName> BiConsumer<P, Consumer<String>> namespaceSplitter()
	{
		return (namespace, stringConsumer) -> StringUtilities.split(namespace.fullyQualifiedNameUsingDotsAndDollarSigns, '.', stringConsumer);
	}

	public static void split(@NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns, @NotNull final Consumer<String> user)
	{
		StringUtilities.split(fullyQualifiedNameUsingDotsAndDollarSigns, '.', user);
	}

	@NotNull protected final String fullyQualifiedNameUsingDotsAndDollarSigns;

	protected AbstractParentName(@NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns, final boolean isClassLikeName)
	{
		if (!fullyQualifiedNameUsingDotsAndDollarSigns.isEmpty())
		{
			final String[] pieces = fullyQualifiedNameUsingDotsAndDollarSigns.split("\\.");
			final int length = pieces.length;
			final int lastIndex = length - 1;
			for (int index = 0; index < length; index++)
			{
				final String piece = pieces[index];

				final boolean isClassLikeIdentifier = index == lastIndex && isClassLikeName;
				try
				{
					validateIsJavaIdentifier(piece, false, isClassLikeIdentifier);
				}
				catch (final InvalidJavaIdentifierException e)
				{
					throw new IllegalArgumentException("fullyQualifiedNameUsingDotsAndDollarSigns not consist of valid java identifiers", e);
				}
			}

			if ((fullyQualifiedNameUsingDotsAndDollarSigns.charAt(0) == ExternalTypeNameSeparator) || (fullyQualifiedNameUsingDotsAndDollarSigns.charAt(fullyQualifiedNameUsingDotsAndDollarSigns.length() - 1) == ExternalTypeNameSeparator))
			{
				throw new IllegalArgumentException("fullyQualifiedNameUsingDotsAndDollarSigns must not start or end with a '.'");
			}
			if (fullyQualifiedNameUsingDotsAndDollarSigns.contains(InternalTypeNameSeparatorString))
			{
				//noinspection HardcodedFileSeparator
				throw new IllegalArgumentException("fullyQualifiedNameUsingDotsAndDollarSigns must not contain a '/'");
			}
			if (fullyQualifiedNameUsingDotsAndDollarSigns.contains(".."))
			{
				throw new IllegalArgumentException("fullyQualifiedNameUsingDotsAndDollarSigns must not contain ..");
			}
		}
		this.fullyQualifiedNameUsingDotsAndDollarSigns = fullyQualifiedNameUsingDotsAndDollarSigns;
	}

	@NotNull
	@Override
	protected Object[] fields()
	{
		return fields(fullyQualifiedNameUsingDotsAndDollarSigns);
	}

	public final void split(@NotNull final Consumer<String> user)
	{
		split(fullyQualifiedNameUsingDotsAndDollarSigns, user);
	}

	@NotNull
	@Override
	public final String fullyQualifiedNameUsingDotsAndDollarSigns()
	{
		return fullyQualifiedNameUsingDotsAndDollarSigns;
	}

	@NotNull
	public final String allParents()
	{
		final int lastIndex = fullyQualifiedNameUsingDotsAndDollarSigns.lastIndexOf(ExternalTypeNameSeparator);
		if (lastIndex == -1)
		{
			return "";
		}

		return fullyQualifiedNameUsingDotsAndDollarSigns.substring(0, lastIndex);
	}

	@NotNull
	public final String simpleTypeName()
	{
		final int lastIndex = fullyQualifiedNameUsingDotsAndDollarSigns.lastIndexOf(ExternalTypeNameSeparator);

		final String simpleName = lastIndex == -1 ? fullyQualifiedNameUsingDotsAndDollarSigns : fullyQualifiedNameUsingDotsAndDollarSigns.substring(lastIndex + 1);

		if (simpleName.isEmpty())
		{
			throw new IllegalStateException("Empty simpleName");
		}

		return simpleName;
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(@Nullable final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if ((o == null) || (getClass() != o.getClass()))
		{
			return false;
		}

		final AbstractParentName that = (AbstractParentName) o;

		if (!fullyQualifiedNameUsingDotsAndDollarSigns.equals(that.fullyQualifiedNameUsingDotsAndDollarSigns))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return fullyQualifiedNameUsingDotsAndDollarSigns.hashCode();
	}

	@SuppressWarnings("ClassReferencesSubclass")
	@NotNull
	public final PackageName parent()
	{
		return packageName(allParents());
	}
}
