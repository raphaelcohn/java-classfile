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

package com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool;

import com.stormmq.java.classfile.domain.InternalTypeName;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.domain.descriptors.MethodDescriptor;
import com.stormmq.java.parsing.utilities.InvalidJavaIdentifierException;
import com.stormmq.java.parsing.utilities.names.typeNames.TypeName;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.stormmq.functions.ListHelper.listToArray;
import static com.stormmq.java.classfile.domain.InternalTypeName.EmptyInternalTypeNames;
import static com.stormmq.java.classfile.domain.InternalTypeName.MaximumArrayDimensions;
import static com.stormmq.java.classfile.domain.InternalTypeName.VoidInternalTypeName;
import static com.stormmq.java.parsing.utilities.ReservedIdentifiers.validateIsJavaIdentifier;
import static com.stormmq.java.parsing.utilities.StringConstants.*;
import static com.stormmq.java.parsing.utilities.names.typeNames.PrimitiveTypeName.*;
import static com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName.knownReferenceTypeName;

public final class MethodDescriptorParser
{
	private static final int MaximumLengthOfMethodParametersIncludingReturnType = 255;

	@NotNull
	public static InternalTypeName processSimpleTypeDescriptor(final int length, @NonNls @NotNull final String rawTypeDescriptor) throws InvalidJavaClassFileException
	{
		return processTypeDescriptor(0, length, rawTypeDescriptor, false).internalTypeName;
	}

	@NotNull
	public static MethodDescriptor parseMethodDescriptor(@NonNls @NotNull final String rawMethodDescriptor, final boolean returnTypeMustBeVoid) throws InvalidJavaClassFileException
	{
		final int length = rawMethodDescriptor.length();
		if (length == 0)
		{
			throw new InvalidJavaClassFileException("A method methodDescriptor can not be empty");
		}

		final char firstCharacter = rawMethodDescriptor.charAt(0);
		if (firstCharacter != StartOfMethodDescriptorParameters)
		{
			throw new InvalidJavaClassFileException("A method methodDescriptor must start with '" + StartOfMethodDescriptorParameters + '\'');
		}

		final ParameterDescriptorsResult result = processParameterDescriptors(rawMethodDescriptor, length);
		final InternalTypeName[] parameterDescriptors = result.parameterDescriptors;
		final int methodParametersLengthExcludingThisButIncludingReturnType = result.methodParametersLengthExcludingThisButIncludingReturnType;
		final int initialIndex = result.initialIndex;

		final InternalTypeName returnDescriptor = processReturnDescriptor(rawMethodDescriptor, length, methodParametersLengthExcludingThisButIncludingReturnType, initialIndex);
		if (returnTypeMustBeVoid && !returnDescriptor.isVoid())
		{
			throw new InvalidJavaClassFileException("Return type of method is required to be void");
		}
		return new MethodDescriptor(returnDescriptor, parameterDescriptors);
	}

	@NotNull
	private static ParameterDescriptorsResult processParameterDescriptors(@NonNls @NotNull final String rawMethodDescriptor, final int length) throws InvalidJavaClassFileException
	{
		final List<InternalTypeName> parameterDescriptors = new ArrayList<>(255);

		int methodParametersLengthExcludingThisButIncludingReturnType = 0;
		int index = 1;
		while (index < length)
		{
			if (methodParametersLengthExcludingThisButIncludingReturnType == MaximumLengthOfMethodParametersIncludingReturnType)
			{
				throw new InvalidJavaClassFileException("There is no space left for a return type methodDescriptor");
			}

			if (rawMethodDescriptor.charAt(index) == EndOfMethodDescriptorParameters)
			{
				final int initialIndex = index + 1;
				final int size = parameterDescriptors.size();
				if (size == 0)
				{
					return new ParameterDescriptorsResult(EmptyInternalTypeNames, 0, initialIndex);
				}

				final InternalTypeName[] internalTypeNames = listToArray(parameterDescriptors, InternalTypeName[]::new, EmptyInternalTypeNames);
				return new ParameterDescriptorsResult(internalTypeNames, methodParametersLengthExcludingThisButIncludingReturnType, initialIndex);
			}

			final ParsedTypeDescriptorResult parsedTypeDescriptorResult = processTypeDescriptor(index, length, rawMethodDescriptor, false);
			parameterDescriptors.add(parsedTypeDescriptorResult.internalTypeName);
			index = parsedTypeDescriptorResult.lastIndex;
			methodParametersLengthExcludingThisButIncludingReturnType += parsedTypeDescriptorResult.parameterLength;

			index++;
		}
		throw new InvalidJavaClassFileException("A method methodDescriptor's ParameterDescriptors must finish with '" + EndOfMethodDescriptorParameters + '\'');
	}

	@NotNull
	private static InternalTypeName processReturnDescriptor(@NotNull @NonNls final String rawMethodDescriptor, final int length, final int methodParametersLengthExcludingThisButIncludingReturnType, final int index) throws InvalidJavaClassFileException
	{
		if (index == length)
		{
			throw new InvalidJavaClassFileException("A method methodDescriptor must terminate its parameter descriptors with '" + EndOfMethodDescriptorParameters + "' followed by a type code");
		}

		final ParsedTypeDescriptorResult returnDescriptorParsedTypeDescriptorResult = processTypeDescriptor(index, length, rawMethodDescriptor, true);
		if (methodParametersLengthExcludingThisButIncludingReturnType + returnDescriptorParsedTypeDescriptorResult.parameterLength > MaximumLengthOfMethodParametersIncludingReturnType)
		{
			throw new InvalidJavaClassFileException("A method type methodDescriptor may not have parameters (including return type" + EndOfMethodDescriptorParameters + " with a total length greater than 255");
		}
		return returnDescriptorParsedTypeDescriptorResult.internalTypeName;
	}

	@NotNull
	private static ParsedTypeDescriptorResult processTypeDescriptor(final int initialIndex, final int length, @NotNull @NonNls final String rawTypeDescriptor, final boolean voidIsAllowed) throws InvalidJavaClassFileException
	{
		int arrayDimensions = 0;
		int index = initialIndex;

		while (index < length)
		{
			final char typeCode = rawTypeDescriptor.charAt(index);

			switch (typeCode)
			{
				case 'V':
					if (voidIsAllowed)
					{
						if (arrayDimensions == 0)
						{
							return new ParsedTypeDescriptorResult(VoidInternalTypeName, index);
						}
						throw new InvalidJavaClassFileException(Formatting.format("A void can not be an array of dimension count '%1$s'", arrayDimensions));
					}
					throw new InvalidJavaClassFileException("A void is not allowed in a method parameter or field methodDescriptor");

				case 'Z':
					return new ParsedTypeDescriptorResult(_boolean, index, arrayDimensions);

				case 'C':
					return new ParsedTypeDescriptorResult(_char, index, arrayDimensions);

				case 'B':
					return new ParsedTypeDescriptorResult(_byte, index, arrayDimensions);

				case 'S':
					return new ParsedTypeDescriptorResult(_short, index, arrayDimensions);

				case 'I':
					return new ParsedTypeDescriptorResult(_int, index, arrayDimensions);

				case 'F':
					return new ParsedTypeDescriptorResult(_float, index, arrayDimensions);

				case 'J':
					return new ParsedTypeDescriptorResult(_long, index, arrayDimensions, 2);

				case 'D':
					return new ParsedTypeDescriptorResult(_double, index, arrayDimensions, 2);

				case 'L':
					return processClassLikeTypeDescriptor(index, length, rawTypeDescriptor, arrayDimensions);

				case ArrayTypeCodeCharacter:
					arrayDimensions++;
					if (arrayDimensions > MaximumArrayDimensions)
					{
						throw new InvalidJavaClassFileException("An array type methodDescriptor can have a maximum of only " + MaximumArrayDimensions + " dimensions");
					}
					index++;
					break;

				default:
					throw new InvalidJavaClassFileException(Formatting.format("Unknown type code '%1$s'", typeCode));
			}
		}
		throw new InvalidJavaClassFileException("Type descriptor consists solely of array dimensions or is empty");
	}

	@NotNull
	private static ParsedTypeDescriptorResult processClassLikeTypeDescriptor(final int initialIndex, final int length, @NotNull @NonNls final String rawTypeDescriptor, final int arrayDimensions) throws InvalidJavaClassFileException
	{
		int index = initialIndex + 1;
		int startIndex = index;
		final StringBuilder internalTypeName = new StringBuilder(length);
		while(index < length)
		{
			final char character = rawTypeDescriptor.charAt(index);
			switch (character)
			{
				case EndOfTypeDescriptorCharacter:
					extractElement(rawTypeDescriptor, index, startIndex, internalTypeName, true);
					return new ParsedTypeDescriptorResult(knownReferenceTypeName(internalTypeName.toString()), index, arrayDimensions, 1);

				case InternalTypeNameSeparator:
					extractElement(rawTypeDescriptor, index, startIndex, internalTypeName, false);
					internalTypeName.append(ExternalTypeNameSeparator);
					startIndex = index + 1;
					break;

				default:
					break;
			}

			index++;
		}
		throw new InvalidJavaClassFileException("Class-like type methodDescriptor does not end with ';'");
	}

	private static void extractElement(@NonNls @NotNull final String rawTypeDescriptor, final int index, final int previousIndex, @NotNull final StringBuilder internalTypeName, final boolean isFinalElement) throws InvalidJavaClassFileException
	{
		final String javaIdentifier = rawTypeDescriptor.substring(previousIndex, index);
		try
		{
			validateIsJavaIdentifier(javaIdentifier, false, isFinalElement);
		}
		catch (final InvalidJavaIdentifierException e)
		{
			throw new InvalidJavaClassFileException("Type methodDescriptor element is invalid", e);
		}
		internalTypeName.append(javaIdentifier);
	}

	private MethodDescriptorParser()
	{
	}

	private static final class ParameterDescriptorsResult
	{
		@NotNull private final InternalTypeName[] parameterDescriptors;
		private final int methodParametersLengthExcludingThisButIncludingReturnType;
		private final int initialIndex;

		private ParameterDescriptorsResult(@NotNull final InternalTypeName[] parameterDescriptors, final int methodParametersLengthExcludingThisButIncludingReturnType, final int initialIndex)
		{
			this.parameterDescriptors = parameterDescriptors;
			this.methodParametersLengthExcludingThisButIncludingReturnType = methodParametersLengthExcludingThisButIncludingReturnType;
			this.initialIndex = initialIndex;
		}
	}

	private static final class ParsedTypeDescriptorResult
	{
		@NotNull private final InternalTypeName internalTypeName;
		private final int lastIndex;
		private final int parameterLength;

		private ParsedTypeDescriptorResult(@NotNull final TypeName typeName, final int lastIndex, final int arrayDimensions)
		{
			this(new InternalTypeName(typeName, arrayDimensions), lastIndex, 1);
		}

		private ParsedTypeDescriptorResult(@NotNull final TypeName typeName, final int lastIndex, final int arrayDimensions, final int parameterLength)
		{
			this(new InternalTypeName(typeName, arrayDimensions), lastIndex, parameterLength);
		}

		private ParsedTypeDescriptorResult(@NotNull final InternalTypeName internalTypeName, final int lastIndex)
		{
			this(internalTypeName, lastIndex, 1);
		}

		private ParsedTypeDescriptorResult(@NotNull final InternalTypeName internalTypeName, final int lastIndex, final int parameterLength)
		{
			this.internalTypeName = internalTypeName;
			this.lastIndex = lastIndex;
			this.parameterLength = parameterLength;
		}
	}
}
