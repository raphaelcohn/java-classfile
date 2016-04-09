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
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.JavaClassFileContainsDataTooLongToReadException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public interface JavaClassFileReader
{
	@NotNull
	static String unsigned8BitIntegerToString(final short unsigned8BitInteger)
	{
		return Integer.toString(unsigned8BitInteger);
	}

	@NotNull
	static String unsigned16BitIntegerToString(final char unsigned16BitInteger)
	{
		return Integer.toString(unsigned16BitInteger);
	}

	long bytesReadSoFar();

	@NotNull
	String readModifiedUtf8StringWithPrefixedBigEndianUnsigned16BitLength(@NotNull @NonNls String what) throws InvalidJavaClassFileException;

	float readBigEndianFloat(@NotNull @NonNls String what) throws InvalidJavaClassFileException;

	short readUnsigned8BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException;

	short readBigEndianSigned16BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException;

	char readBigEndianUnsigned16BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException;

	int readBigEndianSigned32BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException;

	long readBigEndianUnsigned32BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException;

	long readBigEndianSigned64BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException;

	long readBigEndianRawDouble(@NotNull @NonNls final String what) throws InvalidJavaClassFileException;

	@NotNull
	ByteBuffer readBytes(@NotNull @NonNls final String what, final long length) throws InvalidJavaClassFileException;

	@NotNull
	String readModifiedUtf8String(@NotNull @NonNls final String what, final long length) throws InvalidJavaClassFileException;
}
