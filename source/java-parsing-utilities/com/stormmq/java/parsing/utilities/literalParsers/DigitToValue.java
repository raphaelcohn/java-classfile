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

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public enum DigitToValue
{
	Binary(1, 0L)
	{
		@Override
		public long digitToValue(final char digit)
		{
			switch (digit)
			{
				case '0':
					return 0;

				case '1':
					return 1;

				default:
					throw new IllegalArgumentException(format(ENGLISH, "Invalid binary digit '%1$s'", digit));
			}
		}
	},
	Octal(3, 0L)
	{
		@Override
		public long digitToValue(final char digit)
		{
			switch (digit)
			{
				case '0':
					return 0;

				case '1':
					return 1;

				case '2':
					return 2;

				case '3':
					return 3;

				case '4':
					return 4;

				case '5':
					return 5;

				case '6':
					return 6;

				case '7':
					return 7;

				default:
					throw new IllegalArgumentException(format(ENGLISH, "Invalid octal digit '%1$s'", digit));
			}
		}
	},
	Decimal(0, 10L)
	{
		@Override
		public long digitToValue(final char digit)
		{

			switch (digit)
			{
				case '0':
					return 0;

				case '1':
					return 1;

				case '2':
					return 2;

				case '3':
					return 3;

				case '4':
					return 4;

				case '5':
					return 5;

				case '6':
					return 6;

				case '7':
					return 7;

				case '8':
					return 8;

				case '9':
					return 9;

				default:
					throw new IllegalArgumentException(format(ENGLISH, "Invalid decimal digit '%1$s'", digit));
			}
		}

		@Override
		public long shift(final int length, final long input, final long decimalValue, final int digitIndex)
		{
			return shiftWithValue(length, input, decimalValue, digitIndex);
		}
	},
	Hexadecimal(4, 0L)
	{
		@Override
		public long digitToValue(final char digit)
		{
			switch (digit)
			{
				case '0':
					return 0;

				case '1':
					return 1;

				case '2':
					return 2;

				case '3':
					return 3;

				case '4':
					return 4;

				case '5':
					return 5;

				case '6':
					return 6;

				case '7':
					return 7;

				case '8':
					return 8;

				case '9':
					return 9;

				case 'A':
				case 'a':
					return 10;

				case 'B':
				case 'b':
					return 11;

				case 'C':
				case 'c':
					return 12;

				case 'D':
				case 'd':
					return 13;

				case 'E':
				case 'e':
					return 14;

				case 'F':
				case 'f':
					return 15;

				default:
					throw new IllegalArgumentException(format(ENGLISH, "Invalid hexadecimal digit '%1$s'", digit));
			}
		}
	},
	;
	private final int bitsPerDigit;
	private final long scalar;

	private DigitToValue(final int bitsPerDigit, final long scalar)
	{
		this.bitsPerDigit = bitsPerDigit;
		this.scalar = scalar;
	}

	public abstract long digitToValue(final char digit);

	public long shift(final int length, final long input, final long decimalValue, final int digitIndex)
	{
		return shiftWithOr(length, input, decimalValue, digitIndex);
	}

	protected long shiftWithOr(final int length, final long input, final long decimalValue, final int digitIndex)
	{
		final int shift = (length - 1 - digitIndex) * bitsPerDigit;
		return (decimalValue << shift) | input;
	}

	protected long shiftWithValue(final int length, final long input, final long decimalValue, final int digitIndex)
	{
		final long shift = ((long) length - 1 - digitIndex) * scalar;
		return (decimalValue * shift) | input;
	}
}
