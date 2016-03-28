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

package com.stormmq.java.parsing.utilities.literalParsers;

import com.stormmq.string.Formatting;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public enum IntegerLiteralParser
{
	Integer(32, 11, 10, 8, 2L, 1L, 4L, 7L, 4L, 8L, 3L, 6L, 4L, 7L),
	Long(64, 22, 20, 16, 9L, 2L, 2L, 3L, 3L, 7L, 2L, 0L, 3L, 6L, 8L, 5L, 4L, 7L, 7L, 5L, 8L, 0L, 7L),  // 9223372036854775807L
	;

	public static final long Mask = 0x00000000FFFFFFFFL;
	private final int binaryMaximumLength;
	private final int octalMaximumLength;
	private final int decimalMaximumLength;
	private final int hexadecimalMaximumLength;
	@NotNull private final long[] maximumDecimalValueDigits;

	IntegerLiteralParser(final int binaryMaximumLength, final int octalMaximumLength, final int decimalMaximumLength, final int hexadecimalMaximumLength, @NotNull final long... maximumDecimalValueDigits)
	{

		this.binaryMaximumLength = binaryMaximumLength;
		this.octalMaximumLength = octalMaximumLength;
		this.decimalMaximumLength = decimalMaximumLength;
		this.hexadecimalMaximumLength = hexadecimalMaximumLength;
		this.maximumDecimalValueDigits = maximumDecimalValueDigits;
	}

	@SuppressWarnings("NumericCastThatLosesPrecision")
	public static int parseJava7LiteralAsInteger(@NotNull final String valueIncludingRadixPrefixAndUnderscores)
	{
		final long converted = Integer.parseJava7Literal(valueIncludingRadixPrefixAndUnderscores);
		return (int) (converted & Mask);
	}

	public static long parseJava7LiteralAsLong(@NotNull final String valueIncludingRadixPrefixAndUnderscores)
	{
		return Long.parseJava7Literal(valueIncludingRadixPrefixAndUnderscores);
	}

	private long parseJava7Literal(@NotNull final String valueIncludingRadixPrefixAndUnderscores)
	{
		final int valueIncludingRadixPrefixAndUnderscoresLength = valueIncludingRadixPrefixAndUnderscores.length();

		if (valueIncludingRadixPrefixAndUnderscoresLength == 0)
		{
			throw new IllegalArgumentException(Formatting.format("Java %1$s literal may not be empty", name()));
		}

		final char firstCharacter = valueIncludingRadixPrefixAndUnderscores.charAt(0);

		if (valueIncludingRadixPrefixAndUnderscoresLength == 1)
		{
			return DigitToValue.Decimal.digitToValue(firstCharacter);
		}

		if (isUnderscore(valueIncludingRadixPrefixAndUnderscores.charAt(valueIncludingRadixPrefixAndUnderscoresLength - 1)))
		{
			throw new IllegalArgumentException(Formatting.format("Java %2$s literal '%1$s' may not end with an underscore", valueIncludingRadixPrefixAndUnderscores, name()));
		}

		// Signs are NOT supplied (it is considered an unary expression if it starts '-', eg '-67')
		switch (firstCharacter)
		{
			case '0':
				return parseBinaryOctalOrHexadecimalConstant(valueIncludingRadixPrefixAndUnderscores, valueIncludingRadixPrefixAndUnderscoresLength);
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				return parseDecimalLiteral(valueIncludingRadixPrefixAndUnderscores, valueIncludingRadixPrefixAndUnderscoresLength);
			case '_':
				throw new IllegalArgumentException(Formatting.format("Java %2$s literal '%1$s' may not start with an underscore", valueIncludingRadixPrefixAndUnderscores, name()));
			default:
				throw new IllegalArgumentException(Formatting.format("Java %2$s literal '%1$s' had an invalid radix", valueIncludingRadixPrefixAndUnderscores, name()));
		}
	}

	private long parseBinaryOctalOrHexadecimalConstant(@NonNls @NotNull final String valueIncludingRadixPrefixAndUnderscores, final int valueIncludingRadixPrefixAndUnderscoresLength)
	{
		switch (valueIncludingRadixPrefixAndUnderscores.charAt(1))
		{
			case '_':
				// Seems to be allowed in IntelliJ
				throw new IllegalArgumentException(Formatting.format("Java %2$s literal '%1$s' was supposedly 'octal' (or incomplete radix) with a leading underscore", valueIncludingRadixPrefixAndUnderscores, name()));

			case 'b':
			case 'B':
				return parseBinaryLiteral(valueIncludingRadixPrefixAndUnderscores, valueIncludingRadixPrefixAndUnderscoresLength);

			case 'x':
			case 'X':
				return parseHexadecimalLiteral(valueIncludingRadixPrefixAndUnderscores, valueIncludingRadixPrefixAndUnderscoresLength);

			default:
				return parseOctalLiteral(valueIncludingRadixPrefixAndUnderscores, valueIncludingRadixPrefixAndUnderscoresLength);
		}
	}

	private long parseBinaryLiteral(@NotNull final String valueIncludingRadixPrefixAndUnderscores, final int valueIncludingRadixPrefixAndUnderscoresLength)
	{
		final boolean[] atMaximumLength = new boolean[1];
		return parse(valueIncludingRadixPrefixAndUnderscores, valueIncludingRadixPrefixAndUnderscoresLength, DigitToValue.Binary, 2, binaryMaximumLength, atMaximumLength);
	}

	private long parseOctalLiteral(@NotNull final String valueIncludingRadixPrefixAndUnderscores, final int valueIncludingRadixPrefixAndUnderscoresLength)
	{
		final int initialIndex = 1;
		final boolean[] atMaximumLength = new boolean[1];
		final long converted = parse(valueIncludingRadixPrefixAndUnderscores, valueIncludingRadixPrefixAndUnderscoresLength, DigitToValue.Octal, initialIndex, octalMaximumLength, atMaximumLength);

		if (atMaximumLength[0])
		{
			switch (valueIncludingRadixPrefixAndUnderscores.charAt(initialIndex))
			{
				case '3':
				case '2':
				case '1':
				case '0':
					break;
				default:
					throw new IllegalArgumentException(Formatting.format("Java %2$s literal '%1$s' was %3$s but with a leading digit that is too big", valueIncludingRadixPrefixAndUnderscores, name(), DigitToValue.Octal.name()));
			}
		}

		return converted;
	}

	private long parseDecimalLiteral(@NotNull final String valueIncludingRadixPrefixAndUnderscores, final int valueIncludingRadixPrefixAndUnderscoresLength)
	{
		final int initialIndex = 0;
		final boolean[] atMaximumLength = new boolean[1];
		final long converted = parse(valueIncludingRadixPrefixAndUnderscores, valueIncludingRadixPrefixAndUnderscoresLength, DigitToValue.Decimal, initialIndex, decimalMaximumLength, atMaximumLength);

		if (atMaximumLength[0])
		{
			int index = initialIndex;
			final int digitIndex = 0;
			while (index < valueIncludingRadixPrefixAndUnderscoresLength)
			{
				final char digit = valueIncludingRadixPrefixAndUnderscores.charAt(index);
				if (isNotUnderscore(digit))
				{
					if (DigitToValue.Decimal.digitToValue(digit) > maximumDecimalValueDigits[digitIndex])
					{
						throw new IllegalArgumentException(Formatting.format("Java %2$s literal '%1$s' was %3$s but was too big", valueIncludingRadixPrefixAndUnderscores, name(), DigitToValue.Decimal.name()));
					}
				}
				index++;
			}
		}

		return converted;
	}

	private long parseHexadecimalLiteral(@NotNull final String valueIncludingRadixPrefixAndUnderscores, final int valueIncludingRadixPrefixAndUnderscoresLength)
	{
		final boolean[] atMaximumLength = new boolean[1];
		return parse(valueIncludingRadixPrefixAndUnderscores, valueIncludingRadixPrefixAndUnderscoresLength, DigitToValue.Hexadecimal, 2, hexadecimalMaximumLength, atMaximumLength);
	}

	private long parse(@NotNull final String valueIncludingRadixPrefixAndUnderscores, final int valueIncludingRadixPrefixAndUnderscoresLength, @NotNull final DigitToValue digitToValue, final int initialIndex, final int maximumLength, @NotNull final boolean[] atMaximumLength)
	{
		int index = initialIndex;
		int digitIndex = 0;
		final long[] digitValues = new long[maximumLength];
		boolean lastDigitWasUnderscore = false;
		while (index < valueIncludingRadixPrefixAndUnderscoresLength)
		{
			if (digitIndex == maximumLength)
			{
				throw new IllegalArgumentException(Formatting.format("Java %2$s literal '%1$s' was %3$s but was too long", valueIncludingRadixPrefixAndUnderscores, name(), digitToValue.name()));
			}

			final char digit = valueIncludingRadixPrefixAndUnderscores.charAt(index);
			if (isUnderscore(digit))
			{
				if (index == initialIndex)
				{
					throw new IllegalArgumentException(Formatting.format("Java %2$s literal '%1$s' was %3$s but with a leading underscore", valueIncludingRadixPrefixAndUnderscores, name(), digitToValue.name()));
				}
				lastDigitWasUnderscore = true;
			}
			else
			{
				try
				{
					digitValues[digitIndex] = digitToValue.digitToValue(digit);
				}
				catch (final IllegalArgumentException e)
				{
					throw new IllegalArgumentException(Formatting.format("Java %2$s literal '%1$s' was %3$s but with an invalid digit", valueIncludingRadixPrefixAndUnderscores, name(), digitToValue.name()), e);
				}
				lastDigitWasUnderscore = false;
				digitIndex++;
			}

			index++;
		}

		if (lastDigitWasUnderscore)
		{
			throw new IllegalArgumentException(Formatting.format("Java %2$s literal '%1$s' was binary but with a trailing underscore", valueIncludingRadixPrefixAndUnderscores, name()));
		}

		if (digitIndex == 0)
		{
			throw new IllegalArgumentException(Formatting.format("Java %2$s literal '%1$s' was binary but with no value", valueIncludingRadixPrefixAndUnderscores, name()));
		}

		final int length = digitIndex;
		atMaximumLength[0] = length == maximumLength;

		long converted = 0;
		for(digitIndex = 0; digitIndex < length; digitIndex++)
		{
			converted = digitToValue.shift(length, converted, digitValues[digitIndex], digitIndex);
		}

		return converted;
	}

	public static boolean isUnderscore(final char character)
	{
		return character == '_';
	}

	public static boolean isNotUnderscore(final char character)
	{
		return character != '_';
	}
}
