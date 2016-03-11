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

package com.stormmq.java.parsing.utilities;

import org.jetbrains.annotations.*;

import java.util.function.IntFunction;

import static java.lang.System.arraycopy;

public final class ArrayMerge
{
	@NotNull
	public static <T> T[] merge(@NotNull final T[] ours, @NotNull final T[] theirs, @NotNull final IntFunction<T[]> arrayCreator)
	{
		final int ourLength = ours.length;
		final int theirLength = theirs.length;
		final T[] copy = arrayCreator.apply(ourLength + theirLength);
		arraycopy(ours, 0, copy, 0, ourLength);
		arraycopy(theirs, 0, copy, ourLength, theirLength);
		return copy;
	}

	private ArrayMerge()
	{
	}
}
