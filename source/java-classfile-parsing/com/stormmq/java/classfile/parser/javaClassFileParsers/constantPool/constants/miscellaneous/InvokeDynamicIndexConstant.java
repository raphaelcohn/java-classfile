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

import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPool;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.AbstractSingleWidthConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import org.jetbrains.annotations.NotNull;

public final class InvokeDynamicIndexConstant extends AbstractSingleWidthConstant
{
	@NotNull private final ConstantPool constantPool;
	private final char bootstrapMethodAttributeIndex;
	@NotNull private final ConstantPoolIndex nameAndTypeDescriptorReferenceIndex;

	public InvokeDynamicIndexConstant(@NotNull final ConstantPool constantPool, final char bootstrapMethodAttributeIndex, @NotNull final ConstantPoolIndex nameAndTypeDescriptorReferenceIndex)
	{
		this.constantPool = constantPool;
		this.bootstrapMethodAttributeIndex = bootstrapMethodAttributeIndex;
		this.nameAndTypeDescriptorReferenceIndex = nameAndTypeDescriptorReferenceIndex;
	}

	@Override
	public boolean isSuitableForAStaticFieldConstant()
	{
		return false;
	}

	@Override
	public boolean isValidBootstrapArgument()
	{
		return false;
	}

	@Override
	public void validateReferenceIndices() throws InvalidJavaClassFileException
	{
		constantPool.validateReferenceIndexIsNameAndType(nameAndTypeDescriptorReferenceIndex);
	}
}
