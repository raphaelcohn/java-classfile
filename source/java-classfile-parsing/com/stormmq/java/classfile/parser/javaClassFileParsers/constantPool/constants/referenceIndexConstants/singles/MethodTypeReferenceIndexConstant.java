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

import com.stormmq.java.classfile.domain.attributes.code.constants.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPool;
import com.stormmq.java.classfile.domain.descriptors.MethodDescriptor;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.MethodDescriptorParser.parseMethodDescriptor;

public final class MethodTypeReferenceIndexConstant extends AbstractSingleReferenceIndexConstant implements BootstrapMethodArgument, SingleWidthConstantForLoad
{
	public MethodTypeReferenceIndexConstant(@NotNull final ConstantPool constantPool, @NotNull final ConstantPoolIndex modifiedUtf8StringIndex)
	{
		super(constantPool, modifiedUtf8StringIndex);
	}

	@Override
	public void validateReferenceIndices() throws InvalidJavaClassFileException
	{
		super.validateReferenceIndices();
	}

	@Override
	public boolean isSuitableForAStaticFieldConstant()
	{
		return false;
	}

	@NotNull
	public MethodDescriptor methodDescriptor() throws InvalidJavaClassFileException
	{
		return parseMethodDescriptor(validValue(), false);
	}

	@Override
	public boolean isValidBootstrapArgument()
	{
		return true;
	}

	@Override
	public <T> T visit(@NotNull final SingleWidthConstantForLoadUser<T> singleWidthConstantForLoadUser) throws InvalidConstantException
	{
		final MethodDescriptor methodDescriptor;
		try
		{
			methodDescriptor = methodDescriptor();
		}
		catch (final InvalidJavaClassFileException e)
		{
			throw new InvalidConstantException("Could not parse method descriptor", e);
		}
		return singleWidthConstantForLoadUser.useMethodType(methodDescriptor);
	}
}
