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

package com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.numbers;

import com.stormmq.java.classfile.domain.attributes.code.constants.SingleWidthConstantForLoad;
import com.stormmq.java.classfile.domain.attributes.code.constants.SingleWidthConstantForLoadUser;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.AbstractSingleWidthConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.FieldConstant;
import org.jetbrains.annotations.NotNull;

public final class IntegerConstant extends AbstractSingleWidthConstant implements FieldConstant, SingleWidthConstantForLoad
{
	private final int signed32BitIntegerValue;

	public IntegerConstant(final int signed32BitIntegerValue)
	{
		this.signed32BitIntegerValue = signed32BitIntegerValue;
	}

	@Override
	public void validateReferenceIndices()
	{
	}

	@Override
	public boolean isSuitableForAStaticFieldConstant()
	{
		return true;
	}

	@Override
	public boolean isValidBootstrapArgument()
	{
		return true;
	}

	@NotNull
	@Override
	public Integer value()
	{
		return signed32BitIntegerValue;
	}

	@NotNull
	@Override
	public <T> T visit(@NotNull final SingleWidthConstantForLoadUser<T> singleWidthConstantForLoadUser)
	{
		return singleWidthConstantForLoadUser.useInteger(signed32BitIntegerValue);
	}
}
