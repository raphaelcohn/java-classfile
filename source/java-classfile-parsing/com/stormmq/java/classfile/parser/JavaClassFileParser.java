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

package com.stormmq.java.classfile.parser;

import com.stormmq.java.classfile.domain.JavaClassFileVersion;
import com.stormmq.java.classfile.domain.information.ConcreteTypeInformation;
import com.stormmq.byteReaders.ByteReader;
import com.stormmq.java.classfile.parser.javaClassFileParsers.VersionedClassFileParserChooser;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.versionedClassFileParsers.VersionedClassFileParser;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static com.stormmq.java.classfile.domain.JavaClassFileVersion.values;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.VersionedClassFileParserChooser.Lax;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.VersionedClassFileParserChooser.Strict;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.NotAJavaClassFileException.JavaClassFileMagicNumber;

public final class JavaClassFileParser
{
	@NotNull
	public static ConcreteTypeInformation parseJavaClassFile(@NotNull final ByteReader byteReader, final boolean permitConstantsInInstanceFields) throws InvalidJavaClassFileException, NotAJavaClassFileException
	{
		return parseJavaClassFile(new SimpleJavaClassFileReader(byteReader), permitConstantsInInstanceFields ? Lax : Strict);
	}

	@NotNull
	private static ConcreteTypeInformation parseJavaClassFile(@NotNull final JavaClassFileReader javaClassFileReader, @NotNull final VersionedClassFileParserChooser versionedClassFileParserChooser) throws InvalidJavaClassFileException, NotAJavaClassFileException
	{
		final int magicNumber;
		try
		{
			magicNumber = javaClassFileReader.readBigEndianSigned32BitInteger("class file magic number");
		}
		catch (final InvalidJavaClassFileException e)
		{
			throw new NotAJavaClassFileException(e);
		}
		if (magicNumber != JavaClassFileMagicNumber)
		{
			throw new NotAJavaClassFileException(magicNumber);
		}

		final char minorVersionNumber = javaClassFileReader.readBigEndianUnsigned16BitInteger("minor version number");
		final char majorVersionNumber = javaClassFileReader.readBigEndianUnsigned16BitInteger("major version number");

		final JavaClassFileVersion javaClassFileVersion = parseJavaClassFileVersion(majorVersionNumber, minorVersionNumber);

		final Function<JavaClassFileReader, VersionedClassFileParser> constructor = versionedClassFileParserChooser.choose(javaClassFileVersion);
		return constructor.apply(javaClassFileReader).parse();
	}

	@NotNull
	private static JavaClassFileVersion parseJavaClassFileVersion(final char majorVersionNumber, final char minorVersionNumber) throws InvalidJavaClassFileException
	{
		boolean rejectedForPreviousComparisonBecauseOfMinorVersionNumber = false;
		@NonNls final String minorVersionUnsupportedMessage = "Major version number '%1$s' is a java version which is supported but the minor version number '%2$s' is not)";
		for (final JavaClassFileVersion javaClassFileVersion : values())
		{
			if (javaClassFileVersion.majorVersionNumber < majorVersionNumber)
			{
				rejectedForPreviousComparisonBecauseOfMinorVersionNumber = false;
				continue;
			}
			if (javaClassFileVersion.majorVersionNumber > majorVersionNumber)
			{
				if (rejectedForPreviousComparisonBecauseOfMinorVersionNumber)
				{
					throw new InvalidJavaClassFileException(Formatting.format(minorVersionUnsupportedMessage, majorVersionNumber, minorVersionNumber));
				}
				throw new InvalidJavaClassFileException(Formatting.format("Major version number '%1$s' is a legacy java version which is unsupported (minor version number is '%2$s')", majorVersionNumber, minorVersionNumber));
			}
			if (javaClassFileVersion.minimumInclusiveMinorVersionNumber >= minorVersionNumber || minorVersionNumber <= javaClassFileVersion.maximumInclusiveMinorVersionNumber)
			{
				return javaClassFileVersion;
			}
			rejectedForPreviousComparisonBecauseOfMinorVersionNumber = true;
		}
		if (rejectedForPreviousComparisonBecauseOfMinorVersionNumber)
		{
			throw new InvalidJavaClassFileException(Formatting.format(minorVersionUnsupportedMessage, majorVersionNumber, minorVersionNumber));
		}
		throw new InvalidJavaClassFileException(Formatting.format("Major version number '%1$s' is a newer java version which is unsupported (minor version number is '%2$s')", majorVersionNumber, minorVersionNumber));
	}

	private JavaClassFileParser()
	{
	}
}
