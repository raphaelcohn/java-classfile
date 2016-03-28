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

package com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.methodHandleConstants;

import com.stormmq.java.classfile.domain.names.MethodName;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPool;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.Constant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.doubles.ClassMethodReferenceIndexConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.classfile.domain.names.MethodName.InstanceInitializer;

public final class NewInvokeSpecialMethodHandleConstant extends AbstractInvokeMethodHandleConstant
{
	public NewInvokeSpecialMethodHandleConstant(@NotNull final ConstantPool constantPool, @NotNull final ConstantPoolIndex referenceIndex)
	{
		super(constantPool, referenceIndex);
	}

	@Override
	protected void validate(@NotNull final Constant constant) throws InvalidJavaClassFileException
	{
		validateMethodReference(constant);
		validateMethodReferenceIsInstanceInitializer(constant);
	}

	private void validateMethodReferenceIsInstanceInitializer(@NotNull final Constant constant) throws InvalidJavaClassFileException
	{
		final ClassMethodReferenceIndexConstant classMethodReferenceIndexConstant = (ClassMethodReferenceIndexConstant) constant;
		classMethodReferenceIndexConstant.validateReferenceIndices();

		@NonNls final MethodName methodName = classMethodReferenceIndexConstant.methodName();

		if (methodName.equals(InstanceInitializer))
		{
			return;
		}

		throw new InvalidJavaClassFileException(Formatting.format("A method handle that is of kind InvokeInterface must reference '%1$s' an instance initializer of '<init>', not '%2$s'", referenceIndex, methodName));
	}
}
