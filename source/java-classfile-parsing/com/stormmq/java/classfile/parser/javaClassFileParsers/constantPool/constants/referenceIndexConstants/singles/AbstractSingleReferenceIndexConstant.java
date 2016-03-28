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

package com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.singles;

import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPool;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.AbstractSingleWidthConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSingleReferenceIndexConstant extends AbstractSingleWidthConstant
{
	@SuppressWarnings("FieldNotUsedInToString") @NotNull private final ConstantPool constantPool;
	@NotNull protected final ConstantPoolIndex modifiedUtf8StringIndex;

	protected AbstractSingleReferenceIndexConstant(@NotNull final ConstantPool constantPool, @NotNull final ConstantPoolIndex modifiedUtf8StringIndex)
	{
		this.constantPool = constantPool;
		this.modifiedUtf8StringIndex = modifiedUtf8StringIndex;
	}

	@Override
	public void validateReferenceIndices() throws InvalidJavaClassFileException
	{
		constantPool.validateReferenceIndexIsModifiedUtf8String(modifiedUtf8StringIndex);
	}

	@Override
	@NotNull
	public final String toString()
	{
		return Formatting.format("%1$s(%2$s)", getClass().getSimpleName(), modifiedUtf8StringIndex);
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public final boolean equals(@Nullable final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final AbstractSingleReferenceIndexConstant that = (AbstractSingleReferenceIndexConstant) o;

		if (!modifiedUtf8StringIndex.equals(that.modifiedUtf8StringIndex))
		{
			return false;
		}

		return true;
	}

	@Override
	public final int hashCode()
	{
		return modifiedUtf8StringIndex.hashCode();
	}

	@NotNull
	protected final String validValue()
	{
		try
		{
			return constantPool.retrieveModifiedUtf8String(modifiedUtf8StringIndex);
		}
		catch (final InvalidJavaClassFileException e)
		{
			throw new IllegalStateException("Make sure validateReferenceIndices was called first before validValue", e);
		}
	}

	@NotNull
	protected final String potentiallyInvalidValue()
	{
		try
		{
			return constantPool.retrievePotentiallyInvalidModifiedUtf8String(modifiedUtf8StringIndex);
		}
		catch (final InvalidJavaClassFileException e)
		{
			throw new IllegalStateException("Make sure validateReferenceIndices was called first before potentiallyInvalidValue", e);
		}
	}
}
