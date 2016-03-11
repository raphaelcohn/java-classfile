package com.stormmq.java.parsing.fileParsers;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.zip.*;

import static com.stormmq.path.FileAndFolderHelper.*;
import static com.stormmq.path.IsFileTypeFilter.*;
import static com.stormmq.path.IsSubFolderFilter.IsSubFolder;
import static java.lang.String.format;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.util.Locale.ENGLISH;

public final class MultiplePathsParser
{
	@NotNull private final FileParser javaSourceFileParser;
	@NotNull private final FileParser javaClassFileParser;

	public MultiplePathsParser(@NotNull final FileParser javaSourceFileParser, @NotNull final FileParser javaClassFileParser)
	{
		this.javaSourceFileParser = javaSourceFileParser;
		this.javaClassFileParser = javaClassFileParser;
	}

	public void parse(@NotNull final Iterable<Path> paths)
	{
		for (final Path path : paths)
		{
			parse(path);
		}
	}

	public void parse(@NotNull final Path path)
	{
		if (IsJarOrZipFile.accept(path))
		{
			processJarOrZipFile(path);
		}
		else if(IsSubFolder.accept(path))
		{
			processFolder(path);
		}
		else
		{
			throw new IllegalStateException(format(ENGLISH, "Do not know what to do with imports path '%1$s'", path));
		}
	}

	private void processFolder(@NotNull final Path dependencyPath)
	{
		walkTreeFollowingSymlinks(dependencyPath, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(@NotNull final Path file, @NotNull final BasicFileAttributes attrs) throws IOException
			{
				super.visitFile(file, attrs);

				if (IsJarOrZipFile.accept(file))
				{
					processJarOrZipFile(file);
				}
				else if (IsJavaOrClassFile.accept(file))
				{
					processRegularFile(file, dependencyPath);
				}
				return CONTINUE;
			}
		});
	}

	private void processJarOrZipFile(@NotNull final Path zipFilePath)
	{
		try(final ZipFile zipFile = new ZipFile(zipFilePath.toFile()))
		{
			final Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while(entries.hasMoreElements())
			{
				final ZipEntry zipEntry = entries.nextElement();

				processZipEntry(zipFile, zipEntry);
			}
		}
		catch (final ZipException e)
		{
			throw new IllegalStateException(format(ENGLISH, "Could not read jar / zip archive '%1$s' because of ZipException '%2$s'", zipFilePath.toString(), e.getMessage()), e);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException(format(ENGLISH, "Could not read jar / zip archive '%1$s' because of '%2$s'", zipFilePath.toString(), e.getMessage()), e);
		}
	}

	private void processRegularFile(@NotNull final Path file, @NotNull final Path dependencyPath)
	{
		final Path relativeFilePath = relativeToRootPath(dependencyPath, file);
		final String name = relativeFilePath.getFileName().toString();
		if (isJavaFile(name))
		{
			javaSourceFileParser.parseFile(file, dependencyPath);
		}
		else
		{
			javaClassFileParser.parseFile(file, dependencyPath);
		}
	}

	private void processZipEntry(@NotNull final ZipFile zipFile, @NotNull final ZipEntry zipEntry)
	{
		if (zipEntry.isDirectory())
		{
			return;
		}

		final String name = zipEntry.getName();

		if (isJavaFile(name))
		{
			javaSourceFileParser.parseFile(zipFile, zipEntry);
		}
		else if (isClassFile(name))
		{
			javaClassFileParser.parseFile(zipFile, zipEntry);
		}
	}

}
