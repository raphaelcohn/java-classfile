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

package com.stormmq.java.classfile.domain;

import com.stormmq.java.parsing.utilities.names.typeNames.TypeName;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.*;

import static com.stormmq.java.parsing.utilities.names.typeNames.VoidTypeName._void;

public final class InternalTypeName
{
	public static final int MaximumArrayDimensions = 255;
	@NotNull public static final InternalTypeName[] EmptyInternalTypeNames = new InternalTypeName[0];
	@NotNull public static final InternalTypeName VoidInternalTypeName = new InternalTypeName(_void, 0);

	@NotNull private final TypeName typeName;
	private final int arrayDimensions;

	public InternalTypeName(@NotNull final TypeName typeName, final int arrayDimensions)
	{
		if (arrayDimensions < 0)
		{
			throw new IllegalArgumentException(Formatting.format("arrayDimensions can not be negative, such as '%1$s'", arrayDimensions));
		}

		if (arrayDimensions > MaximumArrayDimensions)
		{
			throw new IllegalArgumentException(Formatting.format("arrayDimensions can not exceed '%1$s'; they can not be '%2$s'", MaximumArrayDimensions, arrayDimensions));
		}

		if (typeName == _void && arrayDimensions != 0)
		{
			throw new IllegalArgumentException(Formatting.format("void can not have arrayDimensions other than zero (and certainly not '%1$s')", arrayDimensions));
		}

		this.typeName = typeName;
		this.arrayDimensions = arrayDimensions;
	}

	public boolean isArray()
	{
		return arrayDimensions != 0;
	}

	public boolean isVoid()
	{
		return typeName.isVoid();
	}

	public boolean isPrimitive()
	{
		return typeName.isPrimitive();
	}

	@NonNls
	@NotNull
	@Override
	public String toString()
	{
		return Formatting.format("%1$s(%2$s, %3$s)", getClass().getSimpleName(), typeName, arrayDimensions);
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

		final InternalTypeName that = (InternalTypeName) o;

		if (arrayDimensions != that.arrayDimensions)
		{
			return false;
		}
		if (!typeName.equals(that.typeName))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = typeName.hashCode();
		result = 31 * result + arrayDimensions;
		return result;
	}

	@NotNull
	public KnownReferenceTypeName validateIsSuitableForType() throws InvalidInternalTypeNameException
	{
		if (isArray() || isVoid() || isPrimitive())
		{
			throw new InvalidInternalTypeNameException(Formatting.format("Type name '%1$s' is not suitable for a class, interface, enum or annotation", toString()));
		}
		return (KnownReferenceTypeName) typeName;
	}

	@NotNull
	public KnownReferenceTypeName toKnownReferenceTypeName() throws InvalidInternalTypeNameException
	{
		if (typeName instanceof KnownReferenceTypeName)
		{
			return (KnownReferenceTypeName) typeName;
		}
		throw new InvalidInternalTypeNameException("typeName is not a KnownReferenceTypeName");
	}

	@NotNull
	public TypeName typeName()
	{
		return typeName;
	}
}
