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

package com.stormmq.java.parsing.utilities;

import com.stormmq.java.parsing.utilities.string.InvalidUtf16StringException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.stormmq.java.parsing.utilities.StringConstants.*;
import static com.stormmq.java.parsing.utilities.string.StringUtilities.iterateOverStringCodePoints;
import static java.lang.Character.*;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.requireNonNull;

public final class ReservedIdentifiers
{
	@NotNull @NonNls public static final String StaticInitializerMethodName = "<clinit>";
	@NotNull @NonNls public static final String InstanceInitializerMethodName = "<init>";
	@NotNull @NonNls public static final String DefaultAnnotationMemberName = value;
	@NonNls @NotNull private static final String unqualifiedNameDescription = "unqualifiedName";
	@NonNls @NotNull private static final String javaIdentifierDescription = "javaIdentifier";
	@NotNull @NonNls private static final Set<String> ReservedIdentifiers = reservedIdentifiers();

	@NotNull
	private static Set<String> reservedIdentifiers()
	{
		@NonNls final Set<String> reservedIdentifiers = new HashSet<>(50);

		reservedIdentifiers.add("while");
		reservedIdentifiers.add(_super);
		reservedIdentifiers.add("native");
		reservedIdentifiers.add("float");
		reservedIdentifiers.add("const");
		reservedIdentifiers.add("volatile");
		reservedIdentifiers.add("strictfp");
		reservedIdentifiers.add("long");
		reservedIdentifiers.add("finally");
		reservedIdentifiers.add(_class);
		reservedIdentifiers.add("void");
		reservedIdentifiers.add("static");
		reservedIdentifiers.add(_interface);
		reservedIdentifiers.add("final");
		reservedIdentifiers.add("char");
		reservedIdentifiers.add("try");
		reservedIdentifiers.add("short");
		reservedIdentifiers.add("int");
		reservedIdentifiers.add("extends");
		reservedIdentifiers.add("catch");
		reservedIdentifiers.add("transient");
		reservedIdentifiers.add("return");
		reservedIdentifiers.add("instanceof");
		reservedIdentifiers.add(_enum);
		reservedIdentifiers.add("case");
		reservedIdentifiers.add("throws");
		reservedIdentifiers.add("public");
		reservedIdentifiers.add("import");
		reservedIdentifiers.add("else");
		reservedIdentifiers.add("byte");
		reservedIdentifiers.add("throw");
		reservedIdentifiers.add(_protected);
		reservedIdentifiers.add("implements");
		reservedIdentifiers.add("double");
		reservedIdentifiers.add("break");
		reservedIdentifiers.add("this");
		reservedIdentifiers.add(_private);
		reservedIdentifiers.add("if");
		reservedIdentifiers.add("do");
		reservedIdentifiers.add("boolean");
		reservedIdentifiers.add("synchronized");
		reservedIdentifiers.add("package");
		reservedIdentifiers.add("goto");
		reservedIdentifiers.add(_default);
		reservedIdentifiers.add("assert");
		reservedIdentifiers.add("switch");
		reservedIdentifiers.add("new");
		reservedIdentifiers.add("for");
		reservedIdentifiers.add("continue");
		reservedIdentifiers.add("abstract");

		return reservedIdentifiers;
	}

	@NotNull
	@NonNls
	public static String validateIsUnqualifiedName(@NotNull @NonNls final String unqualifiedName, final boolean canBeInitializerMethodName) throws InvalidJavaIdentifierException
	{
		validateIsNotEmpty(unqualifiedName, unqualifiedNameDescription);

		if (isValidInitializerMethodName(unqualifiedName, canBeInitializerMethodName, unqualifiedNameDescription))
		{
			return unqualifiedName;
		}

		try
		{
			iterateOverStringCodePoints(unqualifiedName, (index, codePoint) ->
			{
				switch (codePoint)
				{
					case ExternalTypeNameSeparator:
					case EndOfTypeDescriptorCharacter:
					case ArrayTypeCodeCharacter:
					case InternalTypeNameSeparator:
						throw new InvalidUtf16StringException(format(ENGLISH, "%1$s '%2$s' contains the code point '%3$s' which is invalid", unqualifiedNameDescription, unqualifiedName, codePoint));
				}
			});
		}
		catch (final InvalidUtf16StringException e)
		{
			throw newInvalidUtf16String(e, unqualifiedNameDescription);
		}

		return unqualifiedName;
	}

	@NotNull @NonNls
	public static String validateIsJavaIdentifier(@NotNull @NonNls final String javaIdentifier, final boolean canBeInitializerMethodName) throws InvalidJavaIdentifierException
	{
		validateIsNotEmpty(javaIdentifier, javaIdentifierDescription);

		if (isValidInitializerMethodName(javaIdentifier, canBeInitializerMethodName, javaIdentifierDescription))
		{
			return javaIdentifier;
		}

		validateIsNotAReservedIdentifier(javaIdentifier, javaIdentifierDescription);

		try
		{
			iterateOverStringCodePoints(javaIdentifier, (index, codePoint) ->
			{
				if (index == 0)
				{
					if (!isJavaIdentifierStart(codePoint))
					{
						throw new InvalidJavaIdentifierException(format(ENGLISH, "%1$s '%2$s' starts with an illegal code point '0x%3$s' at index '%4$s'", javaIdentifierDescription, javaIdentifier, Integer.toHexString(codePoint), index));
					}
				}
				else
				{
					if (!isJavaIdentifierPart(codePoint))
					{
						throw new InvalidJavaIdentifierException(format(ENGLISH, "%1$s '%2$s' contains an illegal code point '0x%3$s' at index '%4$s'", javaIdentifierDescription, javaIdentifier, Integer.toHexString(codePoint), index));
					}
				}
			});
		}
		catch (final InvalidUtf16StringException e)
		{
			throw newInvalidUtf16String(e, javaIdentifierDescription);
		}
		return javaIdentifier;
	}

	private static void validateIsNotEmpty(@NotNull @NonNls final String javaIdentifier, @NonNls @NotNull final String whatIsIt)
	{
		if (javaIdentifier.isEmpty())
		{
			throw new IllegalArgumentException(whatIsIt + " can not be empty");
		}
	}

	private static boolean isValidInitializerMethodName(@NonNls @NotNull final String identifier, final boolean canBeInitializerMethodName, @NonNls @NotNull final String whatIsIt)
	{
		if (identifier.charAt(0) == '<')
		{
			if (canBeInitializerMethodName)
			{
				if (identifier.equals(StaticInitializerMethodName) || identifier.equals(InstanceInitializerMethodName))
				{
					return true;
				}
				throw new IllegalArgumentException(whatIsIt + " starts with '<' and is not <clinit> or <init>");
			}
			throw new IllegalArgumentException(whatIsIt + " starts with '<' and can not be an initializer method name");
		}
		return false;
	}

	private static void validateIsNotAReservedIdentifier(@NotNull final String javaIdentifier, @NonNls @NotNull final String whatIsIt)
	{
		if (ReservedIdentifiers.contains(javaIdentifier))
		{
			throw new IllegalArgumentException(format(ENGLISH, "%1$s is not allowed to be '%2$s'", whatIsIt, javaIdentifier));
		}
	}

	@NotNull
	private static InvalidJavaIdentifierException newInvalidUtf16String(@NotNull final InvalidUtf16StringException cause, @NonNls @NotNull final String whatIsIt)
	{
		return new InvalidJavaIdentifierException(whatIsIt + " is not a valid UTF-16 string", cause);
	}

	private ReservedIdentifiers()
	{
	}
}
