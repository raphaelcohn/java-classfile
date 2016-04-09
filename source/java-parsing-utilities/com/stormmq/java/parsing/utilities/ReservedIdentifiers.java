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

import com.stormmq.string.Formatting;
import com.stormmq.string.InvalidUtf16StringException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static com.stormmq.functions.CollectionHelper.addOnce;
import static com.stormmq.java.parsing.utilities.StringConstants.*;
import static com.stormmq.string.StringUtilities.iterateOverStringCodePoints;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;

public final class ReservedIdentifiers
{
	@NotNull @NonNls public static final String StaticInitializerMethodName = "<clinit>";
	@NotNull @NonNls public static final String InstanceInitializerMethodName = "<init>";
	@NotNull @NonNls public static final String DefaultAnnotationMemberName = value;
	@NotNull @NonNls public static final String _void = "void";
	@NonNls @NotNull private static final String unqualifiedNameDescription = "unqualifiedName";
	@NonNls @NotNull private static final String javaIdentifierDescription = "javaIdentifier";
	@NotNull @NonNls private static final Set<String> ReservedIdentifiers = reservedIdentifiers();
	@NotNull @NonNls public static final String _boolean = "boolean";
	@NotNull @NonNls public static final String _byte = "byte";
	@NotNull @NonNls public static final String _char = "char";
	@NotNull @NonNls public static final String _short = "short";
	@NotNull @NonNls public static final String _float = "float";
	@NotNull @NonNls public static final String _double = "double";
	@NotNull @NonNls public static final String _int = "int";
	@NotNull @NonNls public static final String _long = "long";
	@NotNull @NonNls public static final String _true = "true";
	@NotNull @NonNls public static final String _false = "false";
	@NotNull @NonNls public static final String _class = StringConstants._class;
	@NotNull @NonNls public static final String _null = "null";

	@NotNull
	private static Set<String> reservedIdentifiers()
	{
		@NonNls final Set<String> reservedIdentifiers = new HashSet<>(50);

		addOnce(reservedIdentifiers, "while");
		addOnce(reservedIdentifiers, _super);
		addOnce(reservedIdentifiers, "native");
		addOnce(reservedIdentifiers, _float);
		addOnce(reservedIdentifiers, "const");
		addOnce(reservedIdentifiers, "volatile");
		addOnce(reservedIdentifiers, "strictfp");
		addOnce(reservedIdentifiers, _long);
		addOnce(reservedIdentifiers, "finally");
		addOnce(reservedIdentifiers, _class);
		addOnce(reservedIdentifiers, _void);
		addOnce(reservedIdentifiers, "static");
		addOnce(reservedIdentifiers, _interface);
		addOnce(reservedIdentifiers, "final");
		addOnce(reservedIdentifiers, _char);
		addOnce(reservedIdentifiers, "try");
		addOnce(reservedIdentifiers, _short);
		addOnce(reservedIdentifiers, _int);
		addOnce(reservedIdentifiers, "extends");
		addOnce(reservedIdentifiers, "catch");
		addOnce(reservedIdentifiers, "transient");
		addOnce(reservedIdentifiers, "return");
		addOnce(reservedIdentifiers, "instanceof");
		addOnce(reservedIdentifiers, _enum);
		addOnce(reservedIdentifiers, "case");
		addOnce(reservedIdentifiers, "throws");
		addOnce(reservedIdentifiers, "public");
		addOnce(reservedIdentifiers, "import");
		addOnce(reservedIdentifiers, "else");
		addOnce(reservedIdentifiers, _byte);
		addOnce(reservedIdentifiers, "throw");
		addOnce(reservedIdentifiers, _protected);
		addOnce(reservedIdentifiers, "implements");
		addOnce(reservedIdentifiers, _double);
		addOnce(reservedIdentifiers, "break");
		addOnce(reservedIdentifiers, "this");
		addOnce(reservedIdentifiers, _private);
		addOnce(reservedIdentifiers, "if");
		addOnce(reservedIdentifiers, "do");
		addOnce(reservedIdentifiers, _boolean);
		addOnce(reservedIdentifiers, "synchronized");
		addOnce(reservedIdentifiers, "package");
		addOnce(reservedIdentifiers, "goto");
		addOnce(reservedIdentifiers, _default);
		addOnce(reservedIdentifiers, "assert");
		addOnce(reservedIdentifiers, "switch");
		addOnce(reservedIdentifiers, "new");
		addOnce(reservedIdentifiers, "for");
		addOnce(reservedIdentifiers, "continue");
		addOnce(reservedIdentifiers, "abstract");

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
						throw new InvalidUtf16StringException(Formatting.format("%1$s '%2$s' contains the code point '%3$s' which is invalid", unqualifiedNameDescription, unqualifiedName, codePoint));

					default:
						break;
				}
			});
		}
		catch (final InvalidUtf16StringException e)
		{
			throw newInvalidUtf16String(e, unqualifiedNameDescription);
		}

		return unqualifiedName;
	}

	public static void validateIsJavaIdentifier(@NotNull @NonNls final String javaIdentifier, final boolean canBeInitializerMethodName, final boolean isClassLikeIdentifier) throws InvalidJavaIdentifierException
	{
		if (canBeInitializerMethodName && isClassLikeIdentifier)
		{
			throw new IllegalArgumentException("canBeInitializerMethodName and isClassLikeIdentifier can not both be true");
		}

		validateIsNotEmpty(javaIdentifier, javaIdentifierDescription);

		if (isValidInitializerMethodName(javaIdentifier, canBeInitializerMethodName, javaIdentifierDescription))
		{
			return;
		}

		if (isClassLikeIdentifier && javaIdentifier.equals("package-info"))
		{
			return;
		}

		if (isClassLikeIdentifier)
		{
			validateIsNotAReservedIdentifier(javaIdentifier, javaIdentifierDescription);
		}

		try
		{
			iterateOverStringCodePoints(javaIdentifier, (index, codePoint) ->
			{
				if (index == 0)
				{
					if (!isJavaIdentifierStart(codePoint))
					{
						throw new InvalidJavaIdentifierException(Formatting.format("%1$s '%2$s' starts with an illegal code point '0x%3$s' at index '%4$s'", javaIdentifierDescription, javaIdentifier, Integer.toHexString(codePoint), 0));
					}
				}
				else
				{
					if (!isJavaIdentifierPart(codePoint))
					{
						throw new InvalidJavaIdentifierException(Formatting.format("%1$s '%2$s' contains an illegal code point '0x%3$s' at index '%4$s'", javaIdentifierDescription, javaIdentifier, Integer.toHexString(codePoint), index));
					}
				}
			});
		}
		catch (final InvalidUtf16StringException e)
		{
			throw newInvalidUtf16String(e, javaIdentifierDescription);
		}
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
			throw new IllegalArgumentException(Formatting.format("%1$s is not allowed to be '%2$s'", whatIsIt, javaIdentifier));
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
