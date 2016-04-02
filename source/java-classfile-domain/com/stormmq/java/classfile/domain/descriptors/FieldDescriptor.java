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
import com.stormmq.java.classfile.domain.InvalidInternalTypeNameException;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.*;

import static com.stormmq.string.Formatting.format;

public final class FieldDescriptor implements Comparable<FieldDescriptor>
{
	@NotNull public final InternalTypeName internalTypeName;

	public FieldDescriptor(@NotNull final InternalTypeName internalTypeName)
	{
		if (internalTypeName.isVoid())
		{
			throw new IllegalArgumentException("A fieldDescriptor can not be void");
		}
		this.internalTypeName = internalTypeName;
	}

	@Override
	public int compareTo(@NotNull final FieldDescriptor o)
	{
		return internalTypeName.compareTo(o.internalTypeName);
	}

	@Override
	@NotNull
	public String toString()
	{
		return format("%1$s(%2$s)", getClass().getSimpleName(), internalTypeName);
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

		final FieldDescriptor that = (FieldDescriptor) o;

		if (!internalTypeName.equals(that.internalTypeName))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return internalTypeName.hashCode();
	}

	public boolean is(@NotNull final InternalTypeName internalTypeName)
	{
		return this.internalTypeName.equals(internalTypeName);
	}

	@NotNull
	public KnownReferenceTypeName knownReferenceTypeName() throws InvalidInternalTypeNameException
	{
		return internalTypeName.toKnownReferenceTypeName();
	}

	@NotNull
	@NonNls
	public String canonicalName()
	{
		return internalTypeName.canonicalName();
	}
}
