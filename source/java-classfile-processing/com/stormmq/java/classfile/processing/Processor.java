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

package com.stormmq.java.classfile.processing;

import com.stormmq.java.classfile.processing.fileParsers.FileParser;
import com.stormmq.java.classfile.processing.fileParsers.JavaClassFileParser;
import com.stormmq.java.classfile.processing.files.ParsableFile;
import com.stormmq.java.classfile.processing.multithreaded.*;
import com.stormmq.java.classfile.processing.processLogs.ProcessLog;
import com.stormmq.java.classfile.processing.typeInformationUsers.*;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.NotNull;

import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Path;
import java.util.concurrent.*;

public final class Processor
{
	private static final int OptimumThreads = 16; // consider linking to CPU count and number of entries in sourcePaths
	private static final int OptimumHashMapSizeWhenRecording = 75_000;

	private final boolean permitConstantsInInstanceFields;
	@NotNull private final ProcessLog processLog;
	@NotNull private final UncaughtExceptionHandler uncaughtExceptionHandler;

	public Processor(final boolean permitConstantsInInstanceFields, @NotNull final ProcessLog processLog, @NotNull final UncaughtExceptionHandler uncaughtExceptionHandler)
	{
		this.permitConstantsInInstanceFields = permitConstantsInInstanceFields;
		this.processLog = processLog;
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
	}

	@NotNull
	public Records process(@NotNull final Iterable<Path> paths)
	{
		final ConcurrentMap<KnownReferenceTypeName, TypeInformationTriplet> records = new ConcurrentHashMap<>(OptimumHashMapSizeWhenRecording);
		final TypeInformationUser typeInformationUser = new RecordingTypeInformationUser(records, processLog);
		final FileParser javaClassFileParser = new JavaClassFileParser(processLog, permitConstantsInInstanceFields, typeInformationUser);
		final ConcurrentLinkedQueue<ParsableFile> parsableFileQueue = new ConcurrentLinkedQueue<>();
		final Coordination coordination = new Coordination(OptimumThreads, parsableFileQueue, javaClassFileParser, processLog, uncaughtExceptionHandler);
		final EnqueuePathsWalker enqueuePathsWalker = new EnqueuePathsWalker(coordination, new PathProcessor(parsableFileQueue));

		try
		{
			enqueuePathsWalker.parse(paths);
		}
		finally
		{
			final int successCount = processLog.successCount();
			final int failureCount = processLog.failureCount();
			final int total = successCount + failureCount;

			processLog.genericSuccess("Success: %1$s.  Failure: %2$s.  Total: %3$s.", successCount, failureCount, total);
		}

		return new ConcreteRecords(records);
	}

}
