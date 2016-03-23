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

import com.stormmq.java.classfile.domain.InternalTypeName;
import com.stormmq.java.classfile.domain.attributes.code.constants.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPool;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.parsing.utilities.InvalidJavaIdentifierException;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.MethodDescriptorParser.processSimpleTypeDescriptor;
import static com.stormmq.java.parsing.utilities.ReservedIdentifiers.validateIsJavaIdentifier;
import static com.stormmq.java.parsing.utilities.StringConstants.*;
import static com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName.knownReferenceTypeName;

public final class TypeReferenceIndexConstant extends AbstractSingleReferenceIndexConstant implements BootstrapMethodArgument, SingleWidthConstantForLoad
{
	public TypeReferenceIndexConstant(@NotNull final ConstantPool constantPool, @NotNull final ConstantPoolIndex modifiedUtf8StringIndex)
	{
		super(constantPool, modifiedUtf8StringIndex);
	}

	@Override
	public boolean isSuitableForAStaticFieldConstant()
	{
		return false;
	}

	@Override
	public boolean isValidBootstrapArgument()
	{
		return true;
	}

	@NotNull
	public InternalTypeName internalTypeName() throws InvalidJavaClassFileException
	{
		return createInternalTypeNameFromTypeDescriptor(referencedValue());
	}

	@NotNull
	private static InternalTypeName createInternalTypeNameFromTypeDescriptor(@NotNull final String rawInternalTypeName) throws InvalidJavaClassFileException
	{
		final int length = rawInternalTypeName.length();

		if (length == 0)
		{
			throw new InvalidJavaClassFileException("Internal type name has an empty type methodDescriptor");
		}

		// eg [Ljava/lang/String;
		if (rawInternalTypeName.charAt(0) == ArrayTypeCodeCharacter)
		{
			return processSimpleTypeDescriptor(length, rawInternalTypeName);
		}

		// eg java/lang/String
		return new InternalTypeName(toKnownReferenceTypeName(rawInternalTypeName), 0);
	}

	@NotNull
	private static KnownReferenceTypeName toKnownReferenceTypeName(@NotNull final String internalTypeName) throws InvalidJavaClassFileException
	{
		final String[] identifiers = internalTypeName.split(InternalTypeNameSeparatorString);
		if (identifiers.length == 0)
		{
			throw new InvalidJavaClassFileException("No values");
		}

		@NotNull final StringBuilder stringBuilder = new StringBuilder(internalTypeName.length());
		boolean afterFirst = false;
		for (final String identifier : identifiers)
		{
			try
			{
				validateIsJavaIdentifier(identifier, false, true);
			}
			catch (final InvalidJavaIdentifierException e)
			{
				throw new InvalidJavaClassFileException("Class has a simple type name or package name element which isn't a valid Java Identifier", e);
			}
			if (afterFirst)
			{
				stringBuilder.append(ExternalTypeNameSeparator);
			}
			else
			{
				afterFirst = true;
			}
			stringBuilder.append(identifier);
		}

		return knownReferenceTypeName(stringBuilder.toString());
	}

	@NotNull
	@Override
	public <T> T visit(@NotNull final SingleWidthConstantForLoadUser<T> singleWidthConstantForLoadUser) throws InvalidConstantException
	{
		final InternalTypeName internalTypeName;
		try
		{
			internalTypeName = internalTypeName();
		}
		catch (final InvalidJavaClassFileException e)
		{
			throw new InvalidConstantException("Type constant is invalid", e);
		}
		return singleWidthConstantForLoadUser.useType(internalTypeName);
	}
}
