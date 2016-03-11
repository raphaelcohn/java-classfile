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

package com.stormmq.java.parsing.utilities.string;

import com.stormmq.java.parsing.utilities.StringConstants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.*;

import static com.stormmq.java.parsing.utilities.StringConstants.Should_not_be_possible;
import static java.lang.Character.isHighSurrogate;
import static java.lang.Character.isLowSurrogate;
import static java.lang.Character.toCodePoint;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class StringUtilities
{
	private static final int MaximumSizeOfModifiedUtf8EncodingWithTwoBytePrecedingSize = 65535 + 2;
	private static final int HighSurrogateIncrement = 2;
	private static final int NonSurrogateIncrement = 1;

	public static int maximumUtf16ToUtf8EncodingSize(@NotNull final String fullyQualifiedTypeName)
	{
		return fullyQualifiedTypeName.length() * 3;
	}

	@NotNull
	public static String dataOutputFileModifiedUtf8EncodeSizeCheck(@NotNull final String fullyQualifiedNameUsingDotsAndDollarSigns, @NotNull final String simpleTypeName, @NotNull final String delimiter)
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

	@NonNls
	@NotNull
	public static String aOrAn(@NonNls @NotNull final String what)
	{
		switch (what.charAt(0))
		{
			case 'a':
			case 'A':
			case 'e':
			case 'E':
			case 'i':
			case 'I':
			case 'o':
			case 'O':
				return "An";
			default:
				return "A";
		}
	}

	// We do not use Java's built in methods in string because they incorrectly try to correct for wrong high surrogates, etc
	public static <X extends Exception> void iterateOverStringCodePoints(@NotNull final String value, @NotNull final CodePointUser<X> codePointUser) throws InvalidUtf16StringException, X
	{
		int index = 0;
		int indexIncrement;
		final int length = value.length();
		while (index < length)
		{
			final char firstCharacter = value.charAt(index);

			final int codePoint;
			if (isHighSurrogate(firstCharacter))
			{
				final char low;
				try
				{
					low = value.charAt(index + 1);
				}
				catch (final StringIndexOutOfBoundsException e)
				{
					throw new InvalidUtf16StringException(format(ENGLISH, "String value contains a missing final low surrogate after index '%1$s'", index), e);
				}
				codePoint = toCodePoint(firstCharacter, low);
				indexIncrement = HighSurrogateIncrement;
			}
			else if (isLowSurrogate(firstCharacter))
			{
				throw new InvalidUtf16StringException(format(ENGLISH, "String value contains a low surrogate without a preceding high surrogate at index '%1$s'", index));
			}
			else
			{
				codePoint = firstCharacter;
				indexIncrement = NonSurrogateIncrement;
			}

			codePointUser.useCodePoint(index, codePoint);

			index += indexIncrement;
		}
	}

	@NotNull
	private static IllegalStateException newShouldNotBePossible(@NotNull final IOException e)
	{
		return new IllegalStateException(Should_not_be_possible, e);
	}

	private StringUtilities()
	{
	}
}
