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

package com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants;

import com.stormmq.java.classfile.domain.descriptors.FieldDescriptor;
import com.stormmq.java.classfile.domain.descriptors.MethodDescriptor;
import com.stormmq.java.classfile.domain.names.FieldName;
import com.stormmq.java.classfile.domain.names.MethodName;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPool;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.AbstractSingleWidthConstant;
import com.stormmq.java.parsing.utilities.InvalidJavaIdentifierException;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.*;

import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.FieldDescriptorParser.parseFieldDescriptor;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.MethodDescriptorParser.parseMethodDescriptor;
import static com.stormmq.java.parsing.utilities.ReservedIdentifiers.*;

public final class NameAndTypeReferenceIndexConstant extends AbstractSingleWidthConstant
{
	@NonNls
	@NotNull
	public static FieldName parseFieldName(@NotNull @NonNls final String rawFieldName) throws InvalidJavaClassFileException
	{
		final String fieldName;
		try
		{
			fieldName = validateIsUnqualifiedName(rawFieldName, false);
		}
		catch (final InvalidJavaIdentifierException e)
		{
			throw new InvalidJavaClassFileException("field name is not a valid unqualified name", e);
		}
		return new FieldName(fieldName);
	}

	@NotNull
	public static MethodName parseMethodName(@NotNull @NonNls final String rawMethodName) throws InvalidJavaClassFileException
	{
		final String methodName;
		try
		{
			methodName = validateIsUnqualifiedName(rawMethodName, true);
		}
		catch (final InvalidJavaIdentifierException e)
		{
			throw new InvalidJavaClassFileException("method name is not a valid unqualified name", e);
		}
		return new MethodName(methodName);
	}

	@SuppressWarnings("FieldNotUsedInToString") @NotNull private final ConstantPool constantPool;
	@NotNull private final ConstantPoolIndex modifiedUtf8StringReferenceIndexForName;
	@NotNull private final ConstantPoolIndex modifiedUtf8StringReferenceIndexForDescriptor;

	public NameAndTypeReferenceIndexConstant(@NotNull final ConstantPool constantPool, @NotNull final ConstantPoolIndex modifiedUtf8StringReferenceIndexForName, @NotNull final ConstantPoolIndex modifiedUtf8StringReferenceIndexForDescriptor)
	{
		this.constantPool = constantPool;
		this.modifiedUtf8StringReferenceIndexForName = modifiedUtf8StringReferenceIndexForName;
		this.modifiedUtf8StringReferenceIndexForDescriptor = modifiedUtf8StringReferenceIndexForDescriptor;
	}

	@Override
	@NotNull
	public String toString()
	{
		return Formatting.format("%1$s(%2$s, %3$s)", getClass().getSimpleName(), modifiedUtf8StringReferenceIndexForName, modifiedUtf8StringReferenceIndexForDescriptor);
	}

	@SuppressWarnings("RedundantIfStatement")
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

		final NameAndTypeReferenceIndexConstant that = (NameAndTypeReferenceIndexConstant) o;

		if (!modifiedUtf8StringReferenceIndexForName.equals(that.modifiedUtf8StringReferenceIndexForName))
		{
			return false;
		}
		if (!modifiedUtf8StringReferenceIndexForDescriptor.equals(that.modifiedUtf8StringReferenceIndexForDescriptor))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = modifiedUtf8StringReferenceIndexForName.hashCode();
		result = 31 * result + modifiedUtf8StringReferenceIndexForDescriptor.hashCode();
		return result;
	}

	@SuppressWarnings("HardcodedFileSeparator")
	@Override
	public void validateReferenceIndices() throws InvalidJavaClassFileException
	{
		constantPool.validateReferenceIndexIsModifiedUtf8String(modifiedUtf8StringReferenceIndexForName);
		constantPool.validateReferenceIndexIsModifiedUtf8String(modifiedUtf8StringReferenceIndexForDescriptor);
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

	@NotNull
	public FieldName fieldName() throws InvalidJavaClassFileException
	{
		return parseFieldName(rawName());
	}

	@NotNull
	public MethodName methodName() throws InvalidJavaClassFileException
	{
		final String rawMethodName = rawName();

		if (rawMethodName.equals(StaticInitializerMethodName))
		{
			throw new InvalidJavaClassFileException("methodName can not be <clinit>");
		}

		return parseMethodName(rawMethodName);
	}

	@NotNull
	public FieldDescriptor fieldDescriptor() throws InvalidJavaClassFileException
	{
		final String rawFieldDescriptor = constantPool.retrieveModifiedUtf8String(modifiedUtf8StringReferenceIndexForDescriptor);
		return parseFieldDescriptor(rawFieldDescriptor);
	}

	@NotNull
	public MethodDescriptor methodDescriptor() throws InvalidJavaClassFileException
	{
		final boolean returnTypeMustBeVoid = rawName().equals(InstanceInitializerMethodName);
		final String rawMethodDescriptor = constantPool.retrieveModifiedUtf8String(modifiedUtf8StringReferenceIndexForDescriptor);
		return parseMethodDescriptor(rawMethodDescriptor, returnTypeMustBeVoid);
	}

	@NotNull
	private String rawName() throws InvalidJavaClassFileException
	{
		return constantPool.retrieveModifiedUtf8String(modifiedUtf8StringReferenceIndexForName);
	}
}
