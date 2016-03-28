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

import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.parsing.utilities.*;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.AccessFlags.computeFlagsInverseMask;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.AccessFlags.hasFlagSet;
import static com.stormmq.java.parsing.utilities.Visibility.*;

public final class FieldAccessFlags
{
	private static final char ACC_PUBLIC = 0x0001; // Declared public; may be accessed from outside its package.
	private static final char ACC_PRIVATE = 0x0002; // Declared private; usable only within the defining class.
	private static final char ACC_PROTECTED = 0x0004; // Declared protected; may be accessed within subclasses.
	private static final char ACC_STATIC = 0x0008; // Declared static.
	private static final char ACC_FINAL = 0x0010; // Declared final; must not be overridden (§5.4.5).
	private static final char ACC_VOLATILE = 0x0040; // Declared volatile; cannot be cached.
	private static final char ACC_TRANSIENT = 0x0080; // Declared transient; not written or read by a persistent object manager.
	private static final char ACC_SYNTHETIC = 0x1000; // Declared synthetic; not present in the source code.
	private static final char ACC_ENUM = 0x4000; // Declared as an element of an enum.

	public static final int FieldAccessFlagsValidityMask = computeFlagsInverseMask(ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_VOLATILE, ACC_TRANSIENT, ACC_SYNTHETIC, ACC_ENUM);

	@NotNull
	public static FieldFinality fieldFinality(final char accessFlags) throws InvalidJavaClassFileException
	{
		final boolean isVolatile = hasFlagSet(accessFlags, ACC_VOLATILE);
		final boolean isFinal = hasFlagSet(accessFlags, ACC_FINAL);
		if (isVolatile)
		{
			if (isFinal)
			{
				throw new InvalidJavaClassFileException("Field access flags can not be both volatile and final");
			}
			return FieldFinality.Volatile;
		}
		if (isFinal)
		{
			return FieldFinality.Final;
		}
		return FieldFinality.Normal;
	}

	public static boolean isFieldSynthetic(final char accessFlags)
	{
		return hasFlagSet(accessFlags, ACC_SYNTHETIC);
	}

	public static boolean isFieldTransient(final char accessFlags, final boolean isInterfaceOrAnnotation) throws InvalidJavaClassFileException
	{
		final boolean isTransient = hasFlagSet(accessFlags, ACC_TRANSIENT);

		if (isTransient)
		{
			if (isInterfaceOrAnnotation)
			{
				throw new InvalidJavaClassFileException("An interface or annotation field can not be transient");
			}

			return true;
		}

		return false;
	}

	@NotNull
	public static Visibility fieldVisibility(final char accessFlags, final boolean isInterfaceOrAnnotation) throws InvalidJavaClassFileException
	{
		final boolean isPublic = hasFlagSet(accessFlags, ACC_PUBLIC);
		final boolean isProtected = hasFlagSet(accessFlags, ACC_PROTECTED);
		final boolean isPrivate = hasFlagSet(accessFlags, ACC_PRIVATE);

		if (isPublic)
		{
			if (isProtected)
			{
				throw new InvalidJavaClassFileException("A field can not be public and protected");
			}

			if (isPrivate)
			{
				throw new InvalidJavaClassFileException("A field can not be public and private");
			}

			return Public;
		}

		if (isProtected)
		{
			if (isPrivate)
			{
				throw new InvalidJavaClassFileException("A field can not be protected and private");
			}

			if (isInterfaceOrAnnotation)
			{
				throw new InvalidJavaClassFileException("An interface or annotation field must be public, not protected");
			}

			return Protected;
		}

		if (isPrivate)
		{
			if (isInterfaceOrAnnotation)
			{
				throw new InvalidJavaClassFileException("An interface or annotation field must be public, not private");
			}

			return Private;
		}

		return PackageLocal;
	}

	public static boolean isFieldFinal(final char accessFlags, final boolean isInterfaceOrAnnotation) throws InvalidJavaClassFileException
	{
		final boolean isFinal = hasFlagSet(accessFlags, ACC_FINAL);
		if (isFinal)
		{
			return true;
		}
		if (isInterfaceOrAnnotation)
		{
			throw new InvalidJavaClassFileException("An interface or annotation field must be final");
		}
		return false;
	}

	public static boolean isFieldStatic(final char accessFlags, final boolean isInterfaceOrAnnotation) throws InvalidJavaClassFileException
	{
		final boolean isStatic = hasFlagSet(accessFlags, ACC_STATIC);
		if (isStatic)
		{
			return true;
		}
		if (isInterfaceOrAnnotation)
		{
			throw new InvalidJavaClassFileException("An interface or annotation field must be static");
		}
		return false;
	}

	private FieldAccessFlags()
	{
	}
}
