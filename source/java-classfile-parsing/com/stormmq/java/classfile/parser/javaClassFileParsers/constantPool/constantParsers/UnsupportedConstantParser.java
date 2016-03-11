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

package com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constantParsers;

import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPool;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.Constant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolJavaClassFileReader;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.classfile.parser.JavaClassFileReader.unsigned8BitIntegerToString;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class UnsupportedConstantParser implements ConstantParser
{
	private final short tag;

	public UnsupportedConstantParser(final short tag)
	{
		this.tag = tag;
	}

	@NotNull
	@Override
	public Constant parse(@NotNull final ConstantPoolIndex constantPoolIndex, @NotNull final ConstantPoolJavaClassFileReader javaClassFileReader, @NotNull final ConstantPool constantPool) throws InvalidJavaClassFileException
	{
		throw new InvalidJavaClassFileException(format(ENGLISH, "The tag '%1$s' at constant pool index '%2$s' is unsupported", unsigned8BitIntegerToString(tag), constantPoolIndex));
	}
}
