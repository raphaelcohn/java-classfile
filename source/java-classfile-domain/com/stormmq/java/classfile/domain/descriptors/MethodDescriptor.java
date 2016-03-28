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

package com.stormmq.java.classfile.domain.descriptors;

import com.stormmq.java.classfile.domain.InternalTypeName;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.*;

import java.util.*;

public final class MethodDescriptor
{
	@NotNull private final InternalTypeName returnDescriptor;
	@NotNull private final InternalTypeName[] parameterDescriptors;
	@SuppressWarnings("FieldNotUsedInToString") private final int parameterCount;

	public MethodDescriptor(@NotNull final InternalTypeName returnDescriptor, @NotNull final InternalTypeName... parameterDescriptors)
	{
		this.returnDescriptor = returnDescriptor;
		this.parameterDescriptors = parameterDescriptors;
		for (final InternalTypeName parameterDescriptor : parameterDescriptors)
		{
			if (parameterDescriptor.isVoid())
			{
				throw new IllegalArgumentException("parameterDescriptors must not contain void");
			}
		}
		parameterCount = parameterDescriptors.length;
	}

	@NotNull
	@Override
	public String toString()
	{
		return Formatting.format("%1$s(%2$s, %3$s)", getClass().getSimpleName(), returnDescriptor, Arrays.toString(parameterDescriptors));
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

		final MethodDescriptor that = (MethodDescriptor) o;

		if (!returnDescriptor.equals(that.returnDescriptor))
		{
			return false;
		}
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if (!Arrays.equals(parameterDescriptors, that.parameterDescriptors))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = returnDescriptor.hashCode();
		result = 31 * result + Arrays.hashCode(parameterDescriptors);
		return result;
	}

	public boolean hasParameters()
	{
		return parameterCount != 0;
	}

	public boolean hasReturnTypeOtherThanVoid()
	{
		return !returnDescriptor.isVoid();
	}

	public boolean hasVoidReturnType()
	{
		return returnDescriptor.isVoid();
	}

	public int parameterCount()
	{
		return parameterCount;
	}
}
