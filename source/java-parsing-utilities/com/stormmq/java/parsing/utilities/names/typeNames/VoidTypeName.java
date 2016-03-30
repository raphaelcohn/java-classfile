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

import com.stormmq.java.parsing.utilities.ReservedIdentifiers;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.parsing.utilities.names.typeNames.TypeNameCategory.Void;

public final class VoidTypeName implements TypeName
{
	@NotNull public static final VoidTypeName _void = new VoidTypeName(ReservedIdentifiers._void, 0);

	@NotNull private final String name;
	@SuppressWarnings("FieldNotUsedInToString") private final int sizeInBitsOnASixtyFourBitCpu;

	private VoidTypeName(@NotNull @NonNls final String name, final int sizeInBitsOnASixtyFourBitCpu)
	{
		this.name = name;
		this.sizeInBitsOnASixtyFourBitCpu = sizeInBitsOnASixtyFourBitCpu;
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
		return Void;
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

		final VoidTypeName that = (VoidTypeName) o;

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
		return true;
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
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
