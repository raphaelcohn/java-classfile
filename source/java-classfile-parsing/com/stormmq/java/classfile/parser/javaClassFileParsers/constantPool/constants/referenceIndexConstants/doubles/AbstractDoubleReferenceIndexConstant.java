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

package com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.doubles;

import com.stormmq.java.classfile.domain.InternalTypeName;
import com.stormmq.java.classfile.domain.names.FieldName;
import com.stormmq.java.classfile.domain.names.MethodName;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPool;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.AbstractSingleWidthConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.NameAndTypeReferenceIndexConstant;
import com.stormmq.java.classfile.domain.descriptors.FieldDescriptor;
import com.stormmq.java.classfile.domain.descriptors.MethodDescriptor;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public abstract class AbstractDoubleReferenceIndexConstant extends AbstractSingleWidthConstant
{
	@SuppressWarnings("FieldNotUsedInToString") @NotNull private final ConstantPool constantPool;
	@NotNull private final ConstantPoolIndex classReferenceIndex;
	@NotNull private final ConstantPoolIndex nameAndTypeDescriptorReferenceIndex;

	protected AbstractDoubleReferenceIndexConstant(@NotNull final ConstantPool constantPool, @NotNull final ConstantPoolIndex classReferenceIndex, @NotNull final ConstantPoolIndex nameAndTypeDescriptorReferenceIndex)
	{
		this.constantPool = constantPool;
		this.classReferenceIndex = classReferenceIndex;
		this.nameAndTypeDescriptorReferenceIndex = nameAndTypeDescriptorReferenceIndex;
	}

	@Override
	public final void validateReferenceIndices() throws InvalidJavaClassFileException
	{
		constantPool.validateReferenceIndexIsClass(classReferenceIndex);
		constantPool.validateReferenceIndexIsNameAndType(nameAndTypeDescriptorReferenceIndex);
	}

	@NotNull
	public final InternalTypeName internalTypeName() throws InvalidJavaClassFileException
	{
		return constantPool.retrieveInternalTypeName(classReferenceIndex);
	}

	@NotNull
	protected final NameAndTypeReferenceIndexConstant nameAndTypeReferenceIndexConstant() throws InvalidJavaClassFileException
	{
		return constantPool.retrieveNameAndTypeReference(nameAndTypeDescriptorReferenceIndex);
	}

	@Override
	public final boolean isSuitableForAStaticFieldConstant()
	{
		return false;
	}

	@Override
	public final boolean isValidBootstrapArgument()
	{
		return false;
	}

	@Override
	@NotNull
	public final String toString()
	{
		return format(ENGLISH, "%1$s(%2$s, %3$s)", getClass().getSimpleName(), classReferenceIndex, nameAndTypeDescriptorReferenceIndex);
	}

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

		final AbstractDoubleReferenceIndexConstant that = (AbstractDoubleReferenceIndexConstant) o;

		if (!classReferenceIndex.equals(that.classReferenceIndex))
		{
			return false;
		}
		if (!nameAndTypeDescriptorReferenceIndex.equals(that.nameAndTypeDescriptorReferenceIndex))
		{
			return false;
		}

		return true;
	}

	@Override
	public final int hashCode()
	{
		int result = classReferenceIndex.hashCode();
		result = 31 * result + nameAndTypeDescriptorReferenceIndex.hashCode();
		return result;
	}

	@NotNull
	protected final FieldName fieldNameX() throws InvalidJavaClassFileException
	{
		return nameAndTypeReferenceIndexConstant().fieldName();
	}

	@NotNull
	protected final FieldDescriptor fieldDescriptorX() throws InvalidJavaClassFileException
	{
		return nameAndTypeReferenceIndexConstant().fieldDescriptor();
	}

	@NotNull
	protected final MethodName methodNameX() throws InvalidJavaClassFileException
	{
		return nameAndTypeReferenceIndexConstant().methodName();
	}

	@NotNull
	protected final MethodDescriptor methodDescriptorX() throws InvalidJavaClassFileException
	{
		return nameAndTypeReferenceIndexConstant().methodDescriptor();
	}
}
