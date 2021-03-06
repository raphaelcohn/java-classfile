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

package com.stormmq.java.classfile.processing.processLogs;

import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.JavaClassFileContainsDataTooLongToReadException;
import com.stormmq.java.classfile.processing.typeInformationUsers.TypeInformationTriplet;
import com.stormmq.logs.Log;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.*;

import static com.stormmq.logs.LogLevel.Error;
import static com.stormmq.logs.LogLevel.*;
import static com.stormmq.string.Formatting.format;

public final class StandardProcessLog implements ProcessLog
{
	@NotNull private final Log log;
	@NotNull private final AtomicInteger failureCount;
	@NotNull private final AtomicInteger successCount;

	public StandardProcessLog(@NotNull final Log log)
	{
		this.log = log;
		failureCount = new AtomicInteger(0);
		successCount = new AtomicInteger(0);
	}

	@Override
	public int failureCount()
	{
		return failureCount.get();
	}

	@Override
	public int successCount()
	{
		return successCount.get();
	}

	@Override
	public void success(@NotNull final String filePath)
	{
		successCount.getAndIncrement();
		log.log(Info, format("Successfully parsed '%1$s'", filePath));
	}

	@Override
	public void genericSuccess(@NonNls @NotNull final String messageTemplate, @NotNull final Object... arguments)
	{
		log.log(Info, format(messageTemplate, arguments));
	}

	@Override
	public void failureZip(@NotNull final Path zipFilePath, @NotNull final IOException e)
	{
		failure("JAR or ZIP archive '%1$s' on disk could not be read because of an input error '%2$s'", zipFilePath, e.getMessage());
	}

	@Override
	public void failureZip(@NotNull final Path zipFilePath, @NotNull final ZipException e)
	{
		failure("JAR or ZIP archive '%1$s' on disk could not be read because of a ZIP input error '%2$s'", zipFilePath, e.getMessage());
	}

	@Override
	public void failure(@NotNull final Path filePath, @NotNull final IOException e)
	{
		failure("File '%1$s' on disk could not be read because of an input error '%2$s'", filePath, e.getMessage());
	}

	@Override
	public void failure(@NotNull final String filePath, @NotNull final InvalidJavaClassFileException e)
	{
		failure("File '%1$s' on disk could not be read because it is an invalid Java class file '%2$s'", filePath, e.getMessage());
	}

	@Override
	public void failure(@NotNull final String filePath, @NotNull final JavaClassFileContainsDataTooLongToReadException e)
	{
		failure("File '%1$s' on disk could not be parsed because it is an invalid Java class file ('%2$s')", filePath, e.getMessage());
	}

	@Override
	public void failure(@NotNull final ZipFile zipFile, @NotNull final ZipEntry zipEntry, @NotNull final IOException e)
	{
		failure("File '%1$s' in zip archive could not be read because of an input error '%2$s'", ProcessLog.zipPathDetails(zipFile, zipEntry), e.getMessage());
	}

	@Override
	public void failureJavaClassFileIsTooLarge(@NotNull final String filePath)
	{
		failure("File '%1$s' is larger than 2Gb and so is too big to parse. This should be exceedingly rare.");
	}

	@Override
	public void duplicateTypeInformationWarning(@NotNull final TypeInformationTriplet extant, @NotNull final TypeInformationTriplet replacement)
	{
		log.log(Notice, format("Duplicate type information for '%1$s' found at '%2$s' in '%3$s' overrides '%4$s' in '%5$s", replacement.thisClassTypeName(), replacement.relativeFilePath, replacement.relativeRootFolderPath, extant.relativeFilePath, extant.relativeRootFolderPath));
	}

	@SuppressWarnings("OverloadedVarargsMethod")
	private void failure(@NotNull @NonNls final String template, @NotNull final Object... arguments)
	{
		failureCount.getAndIncrement();

		log.log(Error, format(template, arguments));
	}
}
