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

import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPool;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.Constant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.doubles.ClassMethodReferenceIndexConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.doubles.InterfaceMethodReferenceIndexConstant;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractVersionedInvokeMethodHandleConstant extends AbstractInvokeMethodHandleConstant
{
	private final boolean isLessThanJava8;

	protected AbstractVersionedInvokeMethodHandleConstant(@NotNull final ConstantPool constantPool, @NotNull final ConstantPoolIndex referenceIndex, final boolean isLessThanJava8)
	{
		super(constantPool, referenceIndex);
		this.isLessThanJava8 = isLessThanJava8;
	}

	@Override
	protected final void validate(@NotNull final Constant constant) throws InvalidJavaClassFileException
	{
		if (isLessThanJava8)
		{
			validateMethodReference(constant);
		}
		else
		{
			if (!(constant instanceof ClassMethodReferenceIndexConstant) && !(constant instanceof InterfaceMethodReferenceIndexConstant))
			{
				//noinspection SpellCheckingInspection
				throw new InvalidJavaClassFileException(Formatting.format("The reference at constant pool index '%1$s' is not a CONSTANT_Methodref_info or CONSTANT_Interfaceref_info but is instead a '%2$s'", referenceIndex, constant.getClass().getSimpleName()));
			}
		}

		if (constant instanceof ClassMethodReferenceIndexConstant)
		{
			validateMethodReferenceIsNotAnInstanceOrStaticInitializer(constant);
		}
		else
		{
			validateInterfaceReferenceIsNotAnInstanceOrStaticInitializer(constant);
		}
	}
}
