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

import com.stormmq.java.classfile.domain.MethodHandle;
import com.stormmq.java.classfile.domain.attributes.code.constants.SingleWidthConstantForLoadUser;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPool;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.AbstractSingleWidthConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.Constant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMethodHandleConstant extends AbstractSingleWidthConstant implements MethodHandle
{
	@SuppressWarnings("FieldNotUsedInToString") @NotNull private final ConstantPool constantPool;
	@NotNull protected final ConstantPoolIndex referenceIndex;

	protected AbstractMethodHandleConstant(@NotNull final ConstantPool constantPool, @NotNull final ConstantPoolIndex referenceIndex)
	{
		this.constantPool = constantPool;
		this.referenceIndex = referenceIndex;
	}

	@Override
	public String toString()
	{
		return "MethodHandle(" + referenceIndex + ')';
	}

	@Override
	public final boolean isSuitableForAStaticFieldConstant()
	{
		return false;
	}

	@Override
	public final boolean isValidBootstrapArgument()
	{
		return true;
	}

	@Override
	public final void validateReferenceIndices() throws InvalidJavaClassFileException
	{
		final Constant constant = retrieveConstant();
		validate(constant, referenceIndex);
	}

	protected abstract void validate(@NotNull final Constant constant, @NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException;

	@NotNull
	protected final Constant retrieveConstant()
	{
		return constantPool.retrieve(referenceIndex);
	}

	@NotNull
	@Override
	public final <T> T visit(@NotNull final SingleWidthConstantForLoadUser<T> singleWidthConstantForLoadUser)
	{
		return singleWidthConstantForLoadUser.useMethodHandle(this);
	}
}
