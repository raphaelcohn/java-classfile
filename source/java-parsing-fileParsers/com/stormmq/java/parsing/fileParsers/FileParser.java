package com.stormmq.java.parsing.fileParsers;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public interface FileParser
{
	@NotNull FileParser DoNothingFileParser = new FileParser()
	{
		@Override
		public void parseFile(@NotNull final Path filePath, @NotNull final Path sourceRootPath)
		{
		}

		@Override
		public void parseFile(@NotNull final ZipFile zipFile, @NotNull final ZipEntry zipEntry)
		{
		}
	};

	void parseFile(@NotNull final Path filePath, @NotNull final Path sourceRootPath);

	void parseFile(@NotNull final ZipFile zipFile, @NotNull final ZipEntry zipEntry);
}
