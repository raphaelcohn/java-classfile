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

import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.byteReaders.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.JavaClassFileContainsDataTooLongToReadException;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import static com.stormmq.java.classfile.parser.byteReaders.ByteReader.ByteMask;
import static java.lang.Float.intBitsToFloat;
import static java.lang.Integer.MAX_VALUE;
import static java.nio.file.Files.readAllBytes;

// This class is NOT thread safe
public final class SimpleJavaClassFileReader implements JavaClassFileReader
{
	@NotNull private static final String MalformedExceptionMessage = "Malformed Modified UTF-8 String";
	private static final int xC0 = 0xC0;
	private static final int x80 = 0x80;
	private static final int x1F = 0x1F;
	private static final int x3F = 0x3F;
	private static final int x0F = 0x0F;

	@NotNull
	public static JavaClassFileReader classFileReaderForFile(@NotNull final Path path) throws IOException
	{
		return new SimpleJavaClassFileReader(new ByteArrayByteReader(readAllBytes(path)));
	}

	@NotNull private final ByteReader byteReader;

	public SimpleJavaClassFileReader(@NotNull final ByteReader byteReader)
	{
		this.byteReader = byteReader;
	}
	
	@Override
	public long bytesReadSoFar()
	{
		return byteReader.bytesReadSoFar();
	}

	@Override
	public float readBigEndianFloat(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return intBitsToFloat(readBigEndianSigned32BitInteger(what));
	}
	
	@Override
	@SuppressWarnings("NumericCastThatLosesPrecision")
	public short readUnsigned8BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return (short) readByte(what);
	}
	
	@SuppressWarnings("NumericCastThatLosesPrecision")
	@Override
	public short readBigEndianSigned16BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		readFully(2, what);
		return (short) (((byteAt(0) & ByteMask) << 8) + (byteAt(1) & ByteMask));
	}
	
	@Override
	public char readBigEndianUnsigned16BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		readFully(2, what);
		return (char) (((byteAt(0) & ByteMask) << 8) + (byteAt(1) & ByteMask));
	}

	@SuppressWarnings("MagicNumber")
	@Override
	public int readBigEndianSigned32BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		readFully(4, what);
		return ((byteAt(0) & ByteMask) << 24) + ((byteAt(1) & ByteMask) << 16) + ((byteAt(2) & ByteMask) << 8) + ((byteAt(3) & ByteMask));
	}

	@SuppressWarnings("MagicNumber")
	@Override
	public long readBigEndianUnsigned32BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return Integer.toUnsignedLong(readBigEndianSigned32BitInteger(what));
	}

	@SuppressWarnings("MagicNumber")
	@Override
	public long readBigEndianSigned64BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		readFully(8, what);
		return (((long) byteAt(0) << 56) + ((long)(byteAt(1) & ByteMask) << 48) + ((long)(byteAt(2) & ByteMask) << 40) + ((long)(byteAt(3) & ByteMask) << 32) + ((long)(byteAt(4) & ByteMask) << 24) + ((byteAt(5) & ByteMask) << 16) + ((byteAt(6) & ByteMask) <<  8) + ((byteAt(7) & ByteMask)));
	}

	@Override
	public long readBigEndianRawDouble(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return readBigEndianSigned64BitInteger(what);
	}

	@NotNull
	@Override
	public ByteBuffer readBytes(@NotNull @NonNls final String what, final long length) throws JavaClassFileContainsDataTooLongToReadException, InvalidJavaClassFileException
	{
		if (length > MAX_VALUE)
		{
			throw new JavaClassFileContainsDataTooLongToReadException();
		}
		@SuppressWarnings("NumericCastThatLosesPrecision") final int intLength = (int) length;

		return readBytesBuffer(what, intLength);
	}

	@Override
	@NotNull
	public String readModifiedUtf8StringWithPrefixedBigEndianUnsigned16BitLength(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		final char length = readBigEndianUnsigned16BitInteger(what);
		return parseModifiedUtf8String(what, length);
	}

	@Override
	@SuppressWarnings({"MagicNumber", "ThrowCaughtLocally", "NumericCastThatLosesPrecision"})
	@NotNull
	public String readModifiedUtf8String(@NotNull @NonNls final String what, final long length) throws JavaClassFileContainsDataTooLongToReadException, InvalidJavaClassFileException
	{
		if (length > MAX_VALUE)
		{
			throw new JavaClassFileContainsDataTooLongToReadException();
		}

		return parseModifiedUtf8String(what, (int) length);
	}

	@SuppressWarnings("MagicNumber")
	@NotNull
	private String parseModifiedUtf8String(@NonNls @NotNull final String what, final int length) throws InvalidJavaClassFileException
	{
		final ByteBuffer byteBuffer = readBytesBuffer(what, length);

		final char[] characters = new char[length];

		int count = 0;
		int charactersCount = 0;

		// Optimisation that assumes most strings are US-ASCII
		while (count < length)
		{
			final int char1 = getByteAsInteger(byteBuffer, count);
			if (char1 > 127)
			{
				break;
			}
			count++;
			characters[charactersCount] = (char) char1;
			charactersCount++;
		}

		while (count < length)
		{
			final int char1 = getByteAsInteger(byteBuffer, count);
			final int char2;
			final int char3;
			final char character;
			switch (char1 >> 4)
			{
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					/* 0xxxxxxx*/

					count++;

					character = (char) char1;
					break;

				case 12:
				case 13:
					/* 110x xxxx   10xx xxxx*/

					count += 2;

					guardForCompleteSequence(length, count);

					char2 = getByteAsInteger(byteBuffer, count - 1);
					guardForMalformedCharacter(char2);

					character = (char) ((char1 & x1F) << 6 | extractPartOfCodepoint(char2));
					break;

				case 14:
					/* 1110 xxxx  10xx xxxx  10xx xxxx */

					count += 3;

					guardForCompleteSequence(length, count);

					char2 = getByteAsInteger(byteBuffer, count - 2);
					guardForMalformedCharacter(char2);

					char3 = getByteAsInteger(byteBuffer, count - 1);
					guardForMalformedCharacter(char3);

					character = (char) ((char1 & x0F) << 12 | extractPartOfCodepoint(char2) << 6 | extractPartOfCodepoint(char3));
					break;

				default:
					/* 10xx xxxx,  1111 xxxx */

					throw newMalformedInput();
			}

			characters[charactersCount] = character;
			charactersCount++;
		}
		return new String(characters, 0, charactersCount);
	}

	private static int getByteAsInteger(@NotNull final ByteBuffer byteBuffer, final int count)
	{
		return byteBuffer.get(count) & ByteMask;
	}

	private static int extractPartOfCodepoint(final int char3)
	{
		return char3 & x3F;
	}

	private static void guardForMalformedCharacter(final int character) throws InvalidJavaClassFileException
	{
		if ((character & xC0) != x80)
		{
			throw newMalformedInput();
		}
	}

	private static void guardForCompleteSequence(final int length, final int count) throws InvalidJavaClassFileException
	{
		if (count > length)
		{
			throw new InvalidJavaClassFileException(MalformedExceptionMessage, new UTFDataFormatException("Missing end of sequence of encoded characters (underflow)"));
		}
	}

	@NotNull
	private static InvalidJavaClassFileException newMalformedInput()
	{
		return new InvalidJavaClassFileException(MalformedExceptionMessage, new UTFDataFormatException("Malformed input around byte"));
	}
	
	private int readByte(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		try
		{
			return byteReader.readByte(what);
		}
		catch (final EOFException e)
		{
			throw new InvalidJavaClassFileException(Formatting.format("Could not readByte '%1$s'", what), e);
		}
	}

	private void readFully(final int length, @NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		try
		{
			byteReader.readFully(what, length);
		}
		catch (final EOFException e)
		{
			throw new InvalidJavaClassFileException(Formatting.format("Could not readFully '%1$s'", what), e);
		}
	}

	@NotNull
	private ByteBuffer readBytesBuffer(@NonNls @NotNull final String what, final int length) throws InvalidJavaClassFileException
	{
		try
		{
			return byteReader.readBytesBuffer(what, length);
		}
		catch (final EOFException e)
		{
			throw new InvalidJavaClassFileException(Formatting.format("Could not readBytesBuffer '%1$s'", what), e);
		}
	}

	private int byteAt(final int index)
	{
		return byteReader.byteAt(index);
	}
}
