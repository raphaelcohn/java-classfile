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

package com.stormmq.java.classfile;

import com.stormmq.java.classfile.domain.information.TypeInformation;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.JavaClassFileContainsDataTooLongToReadException;
import com.stormmq.java.classfile.parser.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

import static com.stormmq.java.classfile.parser.JavaClassFileParser.parseJavaClassFile;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.VersionedClassFileParserChooser.Modern;
import static com.stormmq.java.classfile.parser.SimpleJavaClassFileReader.classFileReaderForFile;
import static java.lang.System.err;
import static java.lang.System.exit;
import static java.nio.file.Paths.get;

public final class ConsoleEntryPoint
{
	private ConsoleEntryPoint()
	{
	}

	public static void main(@NotNull final String... commandLineArguments)
	{
		@SuppressWarnings({"HardcodedFileSeparator", "HardCodedStringLiteral"}) final Path path = get("./out/production/java-classfile/com/stormmq/java/classfile/ConsoleEntryPoint.class");

		final JavaClassFileReader javaClassFileReader;
		try
		{
			javaClassFileReader = classFileReaderForFile(path);
		}
		catch (final IOException e)
		{
			abort(e);
			throw new IllegalStateException("Impossible", e);
		}

		final TypeInformation typeInformation;
		try
		{
			typeInformation = parseJavaClassFile(javaClassFileReader, Modern);
		}
		catch (final InvalidJavaClassFileException | JavaClassFileContainsDataTooLongToReadException e)
		{
			abort(e);
		}
		
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void abort(@NotNull final Exception e)
	{
		e.printStackTrace(err);
		exit(1);
	}
}
