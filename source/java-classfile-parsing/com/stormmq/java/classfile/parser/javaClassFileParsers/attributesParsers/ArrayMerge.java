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

package com.stormmq.java.classfile.parser.javaClassFileParsers.attributesParsers;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.IntFunction;

import static java.lang.System.arraycopy;

public final class ArrayMerge
{
	@NotNull
	public static <T> T[] arrayMerge(@NotNull final IntFunction<T[]> arrayCreator, @NotNull final List<T[]> arrays)
	{
		int totalLength = 0;
		for (final T[] array : arrays)
		{
			final int length = array.length;
			final int previousTotalLength = totalLength;
			totalLength += length;
			if (totalLength < previousTotalLength)
			{
				throw new IllegalArgumentException("totalLength is too great (overflows)");
			}
		}

		final T[] copy = arrayCreator.apply(totalLength);

		int copyIntoAtIndex = 0;
		for (final T[] array : arrays)
		{
			final int arrayLength = array.length;
			arraycopy(array, 0, copy, copyIntoAtIndex, arrayLength);
			copyIntoAtIndex += arrayLength;
		}

		return copy;
	}

	private ArrayMerge()
	{
	}
}
