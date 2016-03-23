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

package com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool;

import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.Constant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import org.jetbrains.annotations.*;

import static com.stormmq.java.classfile.parser.JavaClassFileReader.unsigned16BitIntegerToString;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class ConstantPoolIndex
{
	private static final char Maximum = 65535;
	@NotNull private static final String InvalidMaximum = format(ENGLISH, "constantPoolIndexUnsigned16BitValue can not be the maximum %1$s", unsigned16BitIntegerToString(Maximum));
	@NotNull private static final ConstantPoolIndex[] ConstantPoolIndices = constantPoolIndices();

	@NotNull
	private static ConstantPoolIndex[] constantPoolIndices()
	{
		final int length = 65535 - 1;
		final ConstantPoolIndex[] constantPoolIndices = new ConstantPoolIndex[length];
		for(int index = 1; index < length; index++)
		{
			constantPoolIndices[index] = new ConstantPoolIndex((char) index);
		}

		return constantPoolIndices;
	}

	@NotNull
	public static ConstantPoolIndex referenceIndexToConstantPoolIndex(final char referenceIndex, @NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		if (referenceIndex == 0)
		{
			throw new InvalidJavaClassFileException(format(ENGLISH, "A %1$s can not point to zero", what));
		}
		if (referenceIndex == Maximum)
		{
			throw new InvalidJavaClassFileException(InvalidMaximum);
		}
		return ConstantPoolIndices[referenceIndex];
	}

	private final char constantPoolIndexUnsigned16BitValue;

	public ConstantPoolIndex(final char constantPoolIndexUnsigned16BitValue)
	{
		if (constantPoolIndexUnsigned16BitValue == 0)
		{
			throw new IllegalArgumentException("constantPoolIndexUnsigned16BitValue can not be zero");
		}
		if (constantPoolIndexUnsigned16BitValue == Maximum)
		{
			throw new IllegalArgumentException(InvalidMaximum);
		}
		this.constantPoolIndexUnsigned16BitValue = constantPoolIndexUnsigned16BitValue;
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Nullable
	public Constant retrieve(@NotNull final Constant[] constants)
	{
		return constants[constantPoolIndexUnsigned16BitValue];
	}

	@NotNull
	@Override
	public String toString()
	{
		return unsigned16BitIntegerToString(constantPoolIndexUnsigned16BitValue);
	}

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

		final ConstantPoolIndex that = (ConstantPoolIndex) o;

		return constantPoolIndexUnsigned16BitValue == that.constantPoolIndexUnsigned16BitValue;
	}

	@Override
	public int hashCode()
	{
		return constantPoolIndexUnsigned16BitValue;
	}

	@NotNull
	public ConstantPoolIndex incrementForDoubleWidthConstantPoolItem(final int constantPoolCount) throws InvalidJavaClassFileException
	{
		final int incremented = constantPoolIndexUnsigned16BitValue + 1;
		if (incremented == Maximum)
		{
			throw new InvalidJavaClassFileException("Invalid double-width constant pool item");
		}
		if (incremented == constantPoolCount)
		{
			throw new InvalidJavaClassFileException(format(ENGLISH, "Constant pool index is the final one but the constant is double width (ie constant pool index is '%1$s')", this));
		}
		return new ConstantPoolIndex((char) incremented);
	}

	public void set(@NotNull final Constant[] constants, @NotNull final Constant constant)
	{
		if (constants[constantPoolIndexUnsigned16BitValue] != null)
		{
			throw new IllegalStateException(format(ENGLISH, "Constant index '%1$s' is already in use", this));
		}
		constants[constantPoolIndexUnsigned16BitValue] = constant;
	}
}
