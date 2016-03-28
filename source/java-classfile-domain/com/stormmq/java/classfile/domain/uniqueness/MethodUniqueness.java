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

package com.stormmq.java.classfile.domain.uniqueness;

import com.stormmq.java.classfile.domain.descriptors.MethodDescriptor;
import com.stormmq.java.classfile.domain.names.MethodName;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.*;

public final class MethodUniqueness
{
	@NotNull private final MethodName methodName;
	@NotNull private final MethodDescriptor methodDescriptor;

	public MethodUniqueness(@NotNull final MethodName methodName, @NotNull @NonNls final MethodDescriptor methodDescriptor)
	{
		this.methodName = methodName;
		this.methodDescriptor = methodDescriptor;
	}

	@NonNls
	@NotNull
	@Override
	public String toString()
	{
		return Formatting.format("%1$s(%2$s, %3$s)", getClass().getSimpleName(), methodName, methodDescriptor);
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(@Nullable final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final MethodUniqueness that = (MethodUniqueness) o;

		if (!methodName.equals(that.methodName))
		{
			return false;
		}
		if (!methodDescriptor.equals(that.methodDescriptor))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = methodName.hashCode();
		result = 31 * result + methodDescriptor.hashCode();
		return result;
	}
}
