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

package com.stormmq.java.parsing.utilities.names.typeNames;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface TypeName extends Comparable<TypeName>
{
	static int compareTo(final int leftSizeInBitsOnASixtyFourBitCpu, @NotNull @NonNls final String leftName, @NotNull final TypeName right)
	{
		final int rightSizeInBitsOnASixtyFourBitCpu = right.sizeInBitsOnASixtyFourBitCpu();
		if (leftSizeInBitsOnASixtyFourBitCpu == rightSizeInBitsOnASixtyFourBitCpu)
		{
			return leftName.compareTo(right.name());
		}
		return leftSizeInBitsOnASixtyFourBitCpu < rightSizeInBitsOnASixtyFourBitCpu ? -1 : 1;
	}

	@NotNull
	<T> T visit(@NotNull final TypeNameVisitor<T> typeNameVisitor);

	boolean isVoid();

	boolean isPrimitive();

	int sizeInBitsOnASixtyFourBitCpu();

	@NotNull
	String name();

	@NotNull
	TypeNameCategory category();
}
