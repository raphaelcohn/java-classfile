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

import com.stormmq.java.classfile.domain.JavaClassFileVersion;
import com.stormmq.java.classfile.domain.TypeKind;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.parsing.utilities.Completeness;
import com.stormmq.java.parsing.utilities.Visibility;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.AccessFlags.hasFlagSet;
import static com.stormmq.java.parsing.utilities.Completeness.Abstract;
import static com.stormmq.java.parsing.utilities.Completeness.Final;
import static com.stormmq.java.parsing.utilities.Completeness.Normal;
import static com.stormmq.java.parsing.utilities.Visibility.PackageLocal;
import static com.stormmq.java.parsing.utilities.Visibility.Public;

public final class TypeAccessFlags
{
	private static final char ACC_PUBLIC = 0x0001; // Declared public; may be accessed from outside its package.
	private static final char ACC_FINAL = 0x0010; // Declared final; no subclasses allowed.
	private static final char ACC_SUPER = 0x0020; // Treat superclass methods specially when invoked by the invokespecial instruction.
	private static final char ACC_INTERFACE = 0x0200; // Is an interface, not a class.
	private static final char ACC_ABSTRACT = 0x0400; // Declared abstract; must not be instantiated.
	private static final char ACC_SYNTHETIC = 0x1000; // Declared synthetic; not present in the source code.
	private static final char ACC_ANNOTATION = 0x2000; // Declared as an annotation type.
	private static final char ACC_ENUM = 0x4000; // Declared as an enum type.

	public static final int TypeAccessFlagsValidityMask = ~(ACC_PUBLIC | ACC_FINAL | ACC_SUPER | ACC_INTERFACE | ACC_ABSTRACT | ACC_SYNTHETIC | ACC_ANNOTATION | ACC_ENUM);

	public static boolean isTypeSynthetic(final char accessFlags)
	{
		return hasFlagSet(accessFlags, ACC_SYNTHETIC);
	}

	@NotNull
	public static TypeKind typeKind(final char accessFlags, @NotNull final JavaClassFileVersion javaClassFileVersion) throws InvalidJavaClassFileException
	{
		final boolean isInterface = hasFlagSet(accessFlags, ACC_INTERFACE);
		final boolean isAnnotation = hasFlagSet(accessFlags, ACC_ANNOTATION);
		final boolean isAbstract = hasFlagSet(accessFlags, ACC_ABSTRACT);
		final boolean isFinal = hasFlagSet(accessFlags, ACC_FINAL);
		final boolean isEnum = hasFlagSet(accessFlags, ACC_ENUM);

		if (javaClassFileVersion.isLessThanJava5())
		{
			if (isEnum)
			{
				throw new InvalidJavaClassFileException("Type flags can not be enum for Java before version 5");
			}

			if (isAnnotation)
			{
				throw new InvalidJavaClassFileException("Type flags can not be annotation for Java before version 5");
			}
		}

		if (isInterface)
		{
			if (isEnum)
			{
				throw new InvalidJavaClassFileException("Type flags can not be both for an enum and an interface");
			}
			if (isAnnotation)
			{
				return TypeKind.Annotation;
			}
			return TypeKind.Interface;
		}
		else
		{
			if (isAnnotation)
			{
				throw new InvalidJavaClassFileException("Type access flags can not be an annotation if they distinguish a class or enum");
			}
			if (isAbstract && isFinal)
			{
				throw new InvalidJavaClassFileException("Type access flags can not be both abstract and final if they distinguish a class or enum");
			}
			return isEnum ? TypeKind.Enum : TypeKind.Class;
		}
	}

	public static boolean hasLegacySuperFlagSetting(final char accessFlags, @NotNull final JavaClassFileVersion javaClassFileVersion) throws InvalidJavaClassFileException
	{
		final boolean isSuper = hasFlagSet(accessFlags, ACC_SUPER);

		// isSuper should be true for all class files after and including Java 1.1 (the oldest version we will probably support) if created using javac
		// Java 8 (and almost certainly later) ignore the flag
		// https://stackoverflow.com/questions/8949933/what-is-the-purpose-of-the-acc-super-access-flag-on-java-class-files

		if (javaClassFileVersion.isJava8OrLater())
		{
			// The flag is effectively no longer defined from Java 8 (it is ignored, although it appears that is set by the javac compiler)
			return false;
		}

		if (javaClassFileVersion.isJava1_1OrLater())
		{
			if (!isSuper)
			{
				throw new InvalidJavaClassFileException("In Java class files from Java 1.1 onwards, ACC_SUPER should be set");
			}
			return false;
		}

		return true;
	}

	@NotNull
	public static Visibility typeVisibility(final char accessFlags) throws InvalidJavaClassFileException
	{
		final boolean isPublic = hasFlagSet(accessFlags, ACC_PUBLIC);
		return isPublic ? Public : PackageLocal;
	}

	@NotNull
	public static Completeness typeCompleteness(final char accessFlags) throws InvalidJavaClassFileException
	{
		final boolean isAbstract = hasFlagSet(accessFlags, ACC_ABSTRACT);
		final boolean isFinal = hasFlagSet(accessFlags, ACC_FINAL);
		if (isAbstract && isFinal)
		{
			throw new InvalidJavaClassFileException("Type access flags can not be both abstract and final");
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

	private TypeAccessFlags()
	{
	}
}
