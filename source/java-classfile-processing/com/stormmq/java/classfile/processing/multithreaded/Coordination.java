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

package com.stormmq.java.classfile.processing.multithreaded;

import com.stormmq.java.classfile.processing.processLogs.ProcessLog;
import com.stormmq.java.classfile.processing.fileParsers.FileParser;
import com.stormmq.java.classfile.processing.files.ParsableFile;
import org.jetbrains.annotations.NotNull;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.currentThread;

public final class Coordination
{
	@NotNull private final Thread[] queueProcessors;
	@NotNull private final AtomicBoolean finish;
	@NotNull private final CountDownLatch countDownLatch;
	@NotNull private final UncaughtExceptionHandler uncaughtExceptionHandler;

	public Coordination(final int count, @NotNull final ConcurrentLinkedQueue<ParsableFile> parsableFileQueue, @NotNull final FileParser fileParser, @NotNull final ProcessLog processLog, @NotNull final UncaughtExceptionHandler uncaughtExceptionHandler)
	{
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
		queueProcessors = new Thread[count];
		for(int index = 0; index < count; index++)
		{
			final QueueProcessor queueProcessor = new QueueProcessor(parsableFileQueue, fileParser, processLog, this);
			queueProcessors[index] = new Thread(queueProcessor, "QueueProcessor" + index);
		}

		finish = new AtomicBoolean(false);
		countDownLatch = new CountDownLatch(count);
	}

	public void start()
	{
		for (final Thread queueProcessor : queueProcessors)
		{
			queueProcessor.setUncaughtExceptionHandler((t, e) ->
			{
				countDownLatch.countDown();
				finish.set(true);
				uncaughtExceptionHandler.uncaughtException(t, e);
			});
			queueProcessor.start();
		}
	}

	public void finish()
	{
		finish.set(true);

		try
		{
			countDownLatch.await();
		}
		catch (final InterruptedException ignored)
		{
			currentThread().interrupt();
		}
	}

	public boolean shouldContinue()
	{
		return !isFinished();
	}

	private boolean isFinished()
	{
		return finish.get();
	}

	public void countDown()
	{
		countDownLatch.countDown();
	}
}
