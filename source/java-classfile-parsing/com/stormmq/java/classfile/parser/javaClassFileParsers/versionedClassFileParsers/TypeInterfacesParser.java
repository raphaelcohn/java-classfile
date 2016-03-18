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

package com.stormmq.java.classfile.parser.javaClassFileParsers.versionedClassFileParsers;

import com.stormmq.java.classfile.domain.TypeKind;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolJavaClassFileReader;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.JavaClassFileContainsDataTooLongToReadException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.functions.InvalidExceptionBiIntConsumer;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.stormmq.java.classfile.domain.TypeKind.Annotation;
import static com.stormmq.java.classfile.domain.TypeKind.Class;
import static com.stormmq.java.classfile.domain.TypeKind.Interface;
import static com.stormmq.java.parsing.utilities.StringConstants.Should_be_impossible;
import static com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName.*;
import static com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName.JavaLangAnnotationAnnotation;
import static com.stormmq.string.StringUtilities.aOrAn;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class TypeInterfacesParser
{
	@NotNull private static final Map<KnownReferenceTypeName, TypeKind> MismatchedThisClassTypeNames = initialiseMismatchedThisClassTypeNames();

	@NotNull
	private static HashMap<KnownReferenceTypeName, TypeKind> initialiseMismatchedThisClassTypeNames()
	{
		final HashMap<KnownReferenceTypeName, TypeKind> mismatchedThisClassTypeNames = new HashMap<>(3);

		mismatchedThisClassTypeNames.put(JavaLangObject, Class);
		mismatchedThisClassTypeNames.put(JavaLangEnum, Class);
		mismatchedThisClassTypeNames.put(JavaLangAnnotationAnnotation, Interface);

		return mismatchedThisClassTypeNames;
	}

	@NotNull private static final KnownReferenceTypeName[] ProhibitedInterfaceTypeNames =
	{
		JavaLangObject,
		JavaLangEnum
	};

	@NotNull private final ConstantPoolJavaClassFileReader javaClassFileReader;
	@NotNull private final TypeKind typeKind;

	public TypeInterfacesParser(@NotNull final ConstantPoolJavaClassFileReader javaClassFileReader, @NotNull final TypeKind typeKind)
	{
		this.javaClassFileReader = javaClassFileReader;
		this.typeKind = typeKind;
	}

	@NotNull
	public KnownReferenceTypeName parseThisClass() throws InvalidJavaClassFileException
	{
		final KnownReferenceTypeName thisClassTypeName = javaClassFileReader.readKnownReferenceTypeName("this class reference");

		@Nullable final TypeKind typeKind = MismatchedThisClassTypeNames.get(thisClassTypeName);
		if (this.typeKind != typeKind)
		{
			throw new InvalidJavaClassFileException(aOrAn(this.typeKind.name()) + "'s this class can not be " + thisClassTypeName);
		}

		return thisClassTypeName;
	}

	@Nullable
	public KnownReferenceTypeName parseSuperClass(@NotNull @NonNls final KnownReferenceTypeName thisClassTypeName) throws InvalidJavaClassFileException
	{
		@Nullable final KnownReferenceTypeName superClassTypeName = javaClassFileReader.readNullableKnownReferenceTypeName("super class reference");

		if (superClassTypeName == null)
		{
			if (typeKind != Class)
			{
				throw new InvalidJavaClassFileException("super class reference index can only be null if this is a class");
			}

			if (thisClassTypeName.equals(JavaLangObject))
			{
				return null;
			}

			throw new InvalidJavaClassFileException("super class reference index can only be null if this class is " + JavaLangObject);
		}

		validateSuperClassIsNotThisClass(superClassTypeName, thisClassTypeName, typeKind);

		switch(typeKind)
		{
			case Class:
				break;

			case Interface:
			case Annotation:
				validateSuperClass(superClassTypeName, JavaLangObject, typeKind);
				break;

			case Enum:
				validateSuperClass(superClassTypeName, JavaLangEnum, typeKind);
				break;

			default:
				throw new IllegalStateException(Should_be_impossible);
		}

		return superClassTypeName;
	}

	@NotNull
	public Set<KnownReferenceTypeName> parseInterfaces(@NotNull final KnownReferenceTypeName thisClassTypeName, @Nullable final KnownReferenceTypeName superClassTypeName) throws InvalidJavaClassFileException
	{
		final Set<KnownReferenceTypeName> interfaces;
		try
		{
			interfaces = javaClassFileReader.parseTableAsSetWith16BitLength(new InvalidExceptionBiIntConsumer<Set<KnownReferenceTypeName>>()
			{
				@Override
				public void accept(@NotNull final Set<KnownReferenceTypeName> objects, final int index) throws InvalidJavaClassFileException, JavaClassFileContainsDataTooLongToReadException
				{
					final KnownReferenceTypeName interfaceTypeName = javaClassFileReader.readKnownReferenceTypeName("implemented interface");

					validateInterfaceIsNotAProhibitedTypeName(interfaceTypeName, ProhibitedInterfaceTypeNames);

					if (interfaceTypeName.equals(thisClassTypeName))
					{
						throw new InvalidJavaClassFileException(format(ENGLISH, "Interface name '%1$s' is thisClass", interfaceTypeName));
					}

					if (superClassTypeName == null)
					{
						throw new InvalidJavaClassFileException(format(ENGLISH, "'%1$s' can not implement interfaces", thisClassTypeName));
					}
					else
					{
						if (interfaceTypeName.equals(thisClassTypeName))
						{
							throw new InvalidJavaClassFileException(format(ENGLISH, "Interface name '%1$s' is superClass", interfaceTypeName));
						}
					}

					if (typeKind == Annotation)
					{
						if (index > 0)
						{
							throw new InvalidJavaClassFileException(format(ENGLISH, "Annotation '%1$s' should not implement more than one interface", thisClassTypeName));
						}
					}

					if (objects.add(interfaceTypeName))
					{
						return;
					}

					throw new InvalidJavaClassFileException(format(ENGLISH, "Duplicate interface name '%1$s'", interfaceTypeName));
				}
			});
		}
		catch (final JavaClassFileContainsDataTooLongToReadException e)
		{
			throw new IllegalStateException("Should not happen", e);
		}

		if (typeKind == Annotation)
		{
			if (interfaces.isEmpty())
			{
				throw new InvalidJavaClassFileException(format(ENGLISH, "Annotation '%1$s' should implement one interface (implements none)", thisClassTypeName));
			}

			if (!interfaces.contains(KnownReferenceTypeName.JavaLangAnnotationAnnotation))
			{
				throw new InvalidJavaClassFileException(format(ENGLISH, "Annotation '%1$s' should implement the interface '%2$s'", thisClassTypeName, KnownReferenceTypeName.JavaLangAnnotationAnnotation));
			}

			return AnnotationKnownReferenceTypeNamesSet;
		}

		return interfaces;
	}

	private static void validateInterfaceIsNotAProhibitedTypeName(@NotNull final KnownReferenceTypeName interfaceTypeName, @NotNull final KnownReferenceTypeName... prohibitedInterfaceTypeNames) throws InvalidJavaClassFileException
	{
		for (final KnownReferenceTypeName prohibitedInterfaceTypeName : prohibitedInterfaceTypeNames)
		{
			if (interfaceTypeName.equals(prohibitedInterfaceTypeName))
			{
				throw new InvalidJavaClassFileException(format(ENGLISH, "Interface name '%1$s' is '%2$s' which is not an interface", interfaceTypeName, prohibitedInterfaceTypeName));
			}
		}
	}

	private static void validateSuperClassIsNotThisClass(@NonNls @NotNull final KnownReferenceTypeName superClassTypeName, @NonNls @NotNull final KnownReferenceTypeName thisClassTypeName, @NotNull final TypeKind typeKind) throws InvalidJavaClassFileException
	{
		if (superClassTypeName.equals(thisClassTypeName))
		{
			throw new InvalidJavaClassFileException(aOrAn(typeKind.name()) + "'s super type must not be itself");
		}
	}

	private static void validateSuperClass(@NonNls @NotNull final KnownReferenceTypeName superClassTypeName, @NonNls @NotNull final KnownReferenceTypeName validSuperClassTypeName, @NotNull final TypeKind typeKind) throws InvalidJavaClassFileException
	{
		if (!superClassTypeName.equals(validSuperClassTypeName))
		{
			throw new InvalidJavaClassFileException(aOrAn(typeKind.name()) + "'s super type must be " + validSuperClassTypeName);
		}
	}

}
