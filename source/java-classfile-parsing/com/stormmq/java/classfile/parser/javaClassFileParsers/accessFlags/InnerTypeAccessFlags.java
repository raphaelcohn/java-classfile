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

package com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags;

import com.stormmq.java.classfile.domain.TypeKind;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.parsing.utilities.Completeness;
import com.stormmq.java.parsing.utilities.Visibility;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.AccessFlags.computeFlagsInverseMask;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.AccessFlags.hasFlagSet;
import static com.stormmq.java.parsing.utilities.Completeness.*;
import static com.stormmq.java.parsing.utilities.Visibility.*;

public final class InnerTypeAccessFlags
{
	private static final char ACC_PUBLIC = 0x0001; // Marked or implicitly public in source.
	private static final char ACC_PRIVATE = 0x0002; // Marked private in source.
	private static final char ACC_PROTECTED = 0x0004; // Marked protected in source.
	private static final char ACC_STATIC = 0x0008; // Marked or implicitly static in source.
	private static final char ACC_FINAL = 0x0010;
	private static final char ACC_INTERFACE = 0x0200;
	private static final char ACC_ABSTRACT = 0x0400;
	private static final char ACC_SYNTHETIC = 0x1000;
	private static final char ACC_ANNOTATION = 0x2000;
	private static final char ACC_ENUM = 0x4000;

	public static final int InnerTypeAccessFlagsValidityMask = computeFlagsInverseMask(ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_INTERFACE, ACC_ABSTRACT, ACC_SYNTHETIC, ACC_ANNOTATION, ACC_ENUM);

	public static boolean isInnerTypeSynthetic(final char accessFlags)
	{
		return hasFlagSet(accessFlags, ACC_SYNTHETIC);
	}

	@NotNull
	public static TypeKind innerTypeKind(final char accessFlags) throws InvalidJavaClassFileException
	{
		final boolean isInterface = hasFlagSet(accessFlags, ACC_INTERFACE);
		final boolean isAnnotation = hasFlagSet(accessFlags, ACC_ANNOTATION);
		final boolean isAbstract = hasFlagSet(accessFlags, ACC_ABSTRACT);
		final boolean isFinal = hasFlagSet(accessFlags, ACC_FINAL);
		final boolean isEnum = hasFlagSet(accessFlags, ACC_ENUM);
		if (isInterface)
		{
			if (isEnum)
			{
				throw new InvalidJavaClassFileException("Inner type flags can not be both for an enum and an interface");
			}
			if (isAnnotation)
			{
				return TypeKind.Annotation;
			}
			return TypeKind.Interface;
		}

		if (isAnnotation)
		{
			throw new InvalidJavaClassFileException("Inner type access flags can not be an annotation if they distinguish a class or enum");
		}

		if (isAbstract && isFinal)
		{
			throw new InvalidJavaClassFileException("Inner type access flags can not be both abstract and final if they distinguish a class or enum");
		}

		return isEnum ? TypeKind.Enum : TypeKind.Class;
	}

	@NotNull
	public static Visibility innerTypeVisibility(final char accessFlags) throws InvalidJavaClassFileException
	{
		final boolean isPublic = hasFlagSet(accessFlags, ACC_PUBLIC);
		final boolean isProtected = hasFlagSet(accessFlags, ACC_PROTECTED);
		final boolean isPrivate = hasFlagSet(accessFlags, ACC_PRIVATE);

		if (isPublic)
		{
			if (isProtected)
			{
				throw new InvalidJavaClassFileException("An inner type can not be public and protected");
			}

			if (isPrivate)
			{
				throw new InvalidJavaClassFileException("An inner type can not be public and private");
			}

			return Public;
		}

		if (isProtected)
		{
			if (isPrivate)
			{
				throw new InvalidJavaClassFileException("An inner type can not be protected and private");
			}

			return Protected;
		}

		if (isPrivate)
		{
			return Private;
		}

		return PackageLocal;
	}

	@NotNull
	public static Completeness innerTypeCompleteness(final char accessFlags) throws InvalidJavaClassFileException
	{
		final boolean isAbstract = hasFlagSet(accessFlags, ACC_ABSTRACT);
		final boolean isFinal = hasFlagSet(accessFlags, ACC_FINAL);
		if (isAbstract && isFinal)
		{
			throw new InvalidJavaClassFileException("Inner type access flags can not be both abstract and final");
		}
		if (isAbstract)
		{
			return Abstract;
		}
		if (isFinal)
		{
			return Final;
		}
		return Normal;
	}

	private InnerTypeAccessFlags()
	{
	}
}
