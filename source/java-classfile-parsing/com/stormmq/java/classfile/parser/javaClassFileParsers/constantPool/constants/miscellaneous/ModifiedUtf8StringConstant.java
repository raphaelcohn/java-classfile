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

package com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.miscellaneous;

import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.AbstractSingleWidthConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.string.CodePointUser;
import com.stormmq.string.InvalidUtf16StringException;
import org.jetbrains.annotations.NotNull;

public final class ModifiedUtf8StringConstant extends AbstractSingleWidthConstant
{
	@NotNull private static final CodePointUser<RuntimeException> DoNothing = (index, codePoint) -> {};
	@NotNull private final String potentiallyInvalidValue;

	public ModifiedUtf8StringConstant(@NotNull final String potentiallyInvalidValue)
	{
		this.potentiallyInvalidValue = potentiallyInvalidValue;
	}

	@Override
	public void validateReferenceIndices()
	{
		// Note: We do not validate on construction or here because one user of Modified UTF-8 strings (for String constants in static field initializers and the operand stack) is permitted to have invalidly encoded Modified UTF-8 strings (strictly speaking, they are allowed to contain invalid surrogate pair sequences)
	}

	@Override
	public boolean isSuitableForAStaticFieldConstant()
	{
		return true;
	}

	@Override
	public boolean isValidBootstrapArgument()
	{
		return false;
	}

	@NotNull
	public String value() throws InvalidJavaClassFileException
	{
		return validateOnUse();
	}

	@NotNull
	public String potentiallyInvalidValue()
	{
		return potentiallyInvalidValue;
	}

	@NotNull
	private String validateOnUse() throws InvalidJavaClassFileException
	{
		try
		{
			DoNothing.iterateOverStringCodePoints(potentiallyInvalidValue);
		}
		catch (final InvalidUtf16StringException e)
		{
			throw new InvalidJavaClassFileException("Invalid Modified UTF-8 String because " + e.getMessage(), e);
		}
		return potentiallyInvalidValue;
	}
}
