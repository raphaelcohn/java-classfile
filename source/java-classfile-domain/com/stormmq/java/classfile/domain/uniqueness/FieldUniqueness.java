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

import com.stormmq.java.classfile.domain.descriptors.FieldDescriptor;
import com.stormmq.java.classfile.domain.names.FieldName;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.*;

public final class FieldUniqueness
{
	@NotNull public final FieldName fieldName;
	@NotNull public final FieldDescriptor fieldDescriptor;

	public FieldUniqueness(@NotNull final FieldName fieldName, @NotNull @NonNls final FieldDescriptor fieldDescriptor)
	{
		this.fieldName = fieldName;
		this.fieldDescriptor = fieldDescriptor;
	}

	@NonNls
	@NotNull
	@Override
	public String toString()
	{
		return Formatting.format("%1$s(%2$s, %3$s)", getClass().getSimpleName(), fieldName, fieldDescriptor);
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

		final FieldUniqueness that = (FieldUniqueness) o;

		if (!fieldName.equals(that.fieldName))
		{
			return false;
		}
		if (!fieldDescriptor.equals(that.fieldDescriptor))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = fieldName.hashCode();
		result = 31 * result + fieldDescriptor.hashCode();
		return result;
	}
}
