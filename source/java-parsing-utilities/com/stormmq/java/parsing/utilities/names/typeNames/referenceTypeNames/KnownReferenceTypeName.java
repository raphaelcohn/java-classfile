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

package com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames;

import com.stormmq.java.parsing.utilities.names.parentNames.AbstractParentName;
import com.stormmq.java.parsing.utilities.names.typeNames.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.stormmq.string.StringConstants.*;
import static com.stormmq.java.parsing.utilities.names.typeNames.TypeNameCategory.Reference;
import static com.stormmq.string.Utf8ByteUser.maximumUtf16ToUtf8EncodingSize;
import static java.util.Collections.singleton;

public final class KnownReferenceTypeName extends AbstractParentName implements ReferenceTypeName
{
	@NotNull private static final ConcurrentMap<String, KnownReferenceTypeName> cache = new ConcurrentHashMap<>(8192);

	@SuppressWarnings("HardcodedFileSeparator") @NotNull private static final String JavaLangObjectInternalName = "java/lang/Object";
	@SuppressWarnings("HardcodedFileSeparator") @NotNull private static final String JavaLangAnnotationAnnotationInternalName = "java/lang/annotation/Annotation";
	@SuppressWarnings("HardcodedFileSeparator") @NotNull private static final String JavaLangEnumInternalName = "java/lang/Enum";
	@SuppressWarnings("HardcodedFileSeparator") @NotNull private static final String JavaLangStringInternalName = "java/lang/String";

	@NotNull public static final KnownReferenceTypeName JavaLangObject = knownReferenceTypeNameFromInternalName(JavaLangObjectInternalName);
	@NotNull public static final KnownReferenceTypeName JavaLangAnnotationAnnotation = knownReferenceTypeNameFromInternalName(JavaLangAnnotationAnnotationInternalName);
	@NotNull public static final KnownReferenceTypeName JavaLangEnum = knownReferenceTypeNameFromInternalName(JavaLangEnumInternalName);
	@NotNull public static final KnownReferenceTypeName JavaLangString = knownReferenceTypeNameFromInternalName(JavaLangStringInternalName);

	@NotNull public static final KnownReferenceTypeName[] EmptyKnownReferenceTypeNames = {};
	@NotNull public static final KnownReferenceTypeName[] AnnotationKnownReferenceTypeNames = {JavaLangAnnotationAnnotation};
	@NotNull public static final Set<KnownReferenceTypeName> AnnotationKnownReferenceTypeNamesSet = singleton(JavaLangAnnotationAnnotation);
	private static final int MaximumSizeOfModifiedUtf8EncodingWithTwoBytePrecedingSize = 65535 + 2;
	private static final int SizeInBitsOnASixtyFourBitCpu = 64;

	@NotNull
	public static KnownReferenceTypeName knownReferenceTypeName(@NonNls @NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns)
	{
		return cache.computeIfAbsent(fullyQualifiedNameUsingDotsAndDollarSigns, KnownReferenceTypeName::new);
	}

	@NotNull
	private static KnownReferenceTypeName knownReferenceTypeNameFromInternalName(@NotNull final String internalName)
	{
		return knownReferenceTypeName(convertInternalNameToRegularName(internalName));
	}

	@NotNull
	private static String convertInternalNameToRegularName(@NotNull final String internalName)
	{
		return internalName.replace(InternalTypeNameSeparator, ExternalTypeNameSeparator);
	}

	@NotNull
	public static KnownReferenceTypeName child(@NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns, @NotNull final String simpleTypeName, @NotNull final String delimiter)
	{
		if (simpleTypeName.isEmpty())
		{
			throw new IllegalArgumentException("simpleTypeName must not be empty");
		}

		if (simpleTypeName.contains(ExternalTypeNameSeparatorString))
		{
			throw new IllegalArgumentException("simpleTypeName must not contain periods");
		}

		final String fullyQualifiedTypeName = dataOutputFileModifiedUtf8EncodeSizeCheck(fullyQualifiedNameUsingDotsAndDollarSigns, simpleTypeName, delimiter);

		return knownReferenceTypeName(fullyQualifiedTypeName);
	}

	@NotNull
	private static String dataOutputFileModifiedUtf8EncodeSizeCheck(@NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns, @NotNull final String simpleTypeName, @NotNull final String delimiter)
	{
		final String fullyQualifiedTypeName = fullyQualifiedNameUsingDotsAndDollarSigns.isEmpty() ? simpleTypeName : fullyQualifiedNameUsingDotsAndDollarSigns + delimiter + simpleTypeName;
		final int size;
		try (DataOutputStream dataOutputStream = new DataOutputStream(new ByteArrayOutputStream(2 + maximumUtf16ToUtf8EncodingSize(fullyQualifiedTypeName))))
		{
			try
			{
				dataOutputStream.writeUTF(fullyQualifiedTypeName);
				dataOutputStream.flush();
			}
			catch (final IOException e)
			{
				throw newShouldNotBePossible(e);
			}
			size = dataOutputStream.size();
		}
		catch (final IOException e)
		{
			throw newShouldNotBePossible(e);
		}
		if (size > MaximumSizeOfModifiedUtf8EncodingWithTwoBytePrecedingSize)
		{
			throw new IllegalArgumentException("simpleTypeName creates a name which when encoded in Modified UTF-8 is too big for DataOutput (eg a Java class file)");
		}
		return fullyQualifiedTypeName;
	}

	@NotNull
	private static IllegalStateException newShouldNotBePossible(@NotNull final IOException e)
	{
		return new IllegalStateException(Should_not_be_possible, e);
	}

	private KnownReferenceTypeName(@NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns)
	{
		super(fullyQualifiedNameUsingDotsAndDollarSigns, true);
	}

	@Override
	public int compareTo(@NotNull final TypeName o)
	{
		return TypeName.compareTo(SizeInBitsOnASixtyFourBitCpu, fullyQualifiedNameUsingDotsAndDollarSigns, o);
	}

	@NotNull
	@Override
	public String name()
	{
		return fullyQualifiedNameUsingDotsAndDollarSigns;
	}

	@NotNull
	@Override
	public TypeNameCategory category()
	{
		return Reference;
	}

	@NotNull
	@Override
	public <T> T visit(@NotNull final TypeNameVisitor<T> typeNameVisitor)
	{
		return typeNameVisitor.useReference(this);
	}

	@Override
	public boolean isVoid()
	{
		return false;
	}

	@Override
	public boolean isJavaLangObject()
	{
		return Objects.equals(this, JavaLangObject);
	}

	@NotNull
	@Override
	public KnownReferenceTypeName resolve()
	{
		return this;
	}

	@Override
	@NotNull
	public KnownReferenceTypeName childOrSame(@NotNull final String anyPartiallyQualifiedTypeName)
	{
		if (anyPartiallyQualifiedTypeName.isEmpty())
		{
			return this;
		}
		if (fullyQualifiedNameUsingDotsAndDollarSigns.isEmpty())
		{
			return knownReferenceTypeName(anyPartiallyQualifiedTypeName);
		}
		return knownReferenceTypeName(fullyQualifiedNameUsingDotsAndDollarSigns + ExternalTypeNameSeparator + anyPartiallyQualifiedTypeName);
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
	}

	@Override
	public int sizeInBitsOnASixtyFourBitCpu()
	{
		return SizeInBitsOnASixtyFourBitCpu;
	}

	@NotNull
	public KnownReferenceTypeName anonymous(final int index)
	{
		return knownReferenceTypeName(fullyQualifiedNameUsingDotsAndDollarSigns + '$' + Integer.toString(index));
	}

	@NotNull
	@Override
	public KnownReferenceTypeName child(@NotNull final String simpleTypeName)
	{
		return child(fullyQualifiedNameUsingDotsAndDollarSigns, simpleTypeName, "");
	}

	@NotNull
	public KnownReferenceTypeName packageClass()
	{
		final int lastIndexOf = fullyQualifiedNameUsingDotsAndDollarSigns.lastIndexOf('.');
		if (lastIndexOf == -1)
		{
			return knownReferenceTypeName("package-info");
		}
		return knownReferenceTypeName(fullyQualifiedNameUsingDotsAndDollarSigns.substring(0, lastIndexOf) + '.' + "package-info");
	}
}
