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

import com.stormmq.string.StringConstants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.parsing.utilities.names.typeNames.TypeNameCategory.FloatingPoint;
import static com.stormmq.java.parsing.utilities.names.typeNames.TypeNameCategory.SignedInteger;
import static com.stormmq.java.parsing.utilities.names.typeNames.TypeNameCategory.UnsignedInteger;

public final class PrimitiveTypeName implements TypeName
{
	@NotNull public static final PrimitiveTypeName _boolean = new PrimitiveTypeName(StringConstants._boolean, 1, SignedInteger);
	@NotNull public static final PrimitiveTypeName _byte = new PrimitiveTypeName(StringConstants._byte, 8, SignedInteger);
	@NotNull public static final PrimitiveTypeName _short = new PrimitiveTypeName(StringConstants._short, 16, SignedInteger);
	@NotNull public static final PrimitiveTypeName _char = new PrimitiveTypeName(StringConstants._char, 16, UnsignedInteger);
	@NotNull public static final PrimitiveTypeName _int = new PrimitiveTypeName(StringConstants._int, 32, SignedInteger);
	@NotNull public static final PrimitiveTypeName _long = new PrimitiveTypeName(StringConstants._long, 64, SignedInteger);
	@NotNull public static final PrimitiveTypeName _float = new PrimitiveTypeName(StringConstants._float, 32, FloatingPoint);
	@NotNull public static final PrimitiveTypeName _double = new PrimitiveTypeName(StringConstants._double, 64, FloatingPoint);

	@NotNull private final String name;
	@SuppressWarnings("FieldNotUsedInToString") private final int sizeInBitsOnASixtyFourBitCpu;
	@SuppressWarnings("FieldNotUsedInToString") @NotNull private final TypeNameCategory typeNameCategory;

	private PrimitiveTypeName(@NotNull @NonNls final String name, final int sizeInBitsOnASixtyFourBitCpu, @NotNull final TypeNameCategory typeNameCategory)
	{
		this.name = name;
		this.sizeInBitsOnASixtyFourBitCpu = sizeInBitsOnASixtyFourBitCpu;
		this.typeNameCategory = typeNameCategory;
	}

	@Override
	public int sizeInBitsOnASixtyFourBitCpu()
	{
		return sizeInBitsOnASixtyFourBitCpu;
	}

	@NotNull
	@Override
	public String name()
	{
		return name;
	}

	@NotNull
	@Override
	public TypeNameCategory category()
	{
		return typeNameCategory;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final PrimitiveTypeName that = (PrimitiveTypeName) o;

		return sizeInBitsOnASixtyFourBitCpu == that.sizeInBitsOnASixtyFourBitCpu && name.equals(that.name);
	}

	@Override
	public int hashCode()
	{
		int result = name.hashCode();
		result = 31 * result + sizeInBitsOnASixtyFourBitCpu;
		return result;
	}

	@Override
	public boolean isVoid()
	{
		return false;
	}

	@Override
	public boolean isPrimitive()
	{
		return true;
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public int compareTo(@NotNull final TypeName o)
	{
		return TypeName.compareTo(sizeInBitsOnASixtyFourBitCpu, name, o);
	}
}
