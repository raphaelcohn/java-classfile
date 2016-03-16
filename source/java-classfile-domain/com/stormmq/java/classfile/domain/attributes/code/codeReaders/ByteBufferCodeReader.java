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

package com.stormmq.java.classfile.domain.attributes.code.codeReaders;

import com.stormmq.java.classfile.domain.attributes.code.invalidOperandStackExceptions.NotEnoughBytesInvalidOperandStackException;
import org.jetbrains.annotations.NotNull;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public final class ByteBufferCodeReader implements CodeReader
{
	@NotNull private final ByteBuffer code;

	public ByteBufferCodeReader(@NotNull final ByteBuffer code)
	{
		this.code = code;
	}

	@Override
	public byte readSignedBBitInteger() throws NotEnoughBytesInvalidOperandStackException
	{
		try
		{
			return code.get();
		}
		catch (final BufferUnderflowException e)
		{
			throw new NotEnoughBytesInvalidOperandStackException(1, e);
		}
	}

	@SuppressWarnings("NumericCastThatLosesPrecision")
	@Override
	public short readUnsigned8BitInteger() throws NotEnoughBytesInvalidOperandStackException
	{
		return (short) (readSignedBBitInteger() & ByteMask);
	}

	@Override
	public char readBigEndianUnsigned16BitInteger() throws NotEnoughBytesInvalidOperandStackException
	{
		return (char) ((readUnsigned8BitInteger() << 8) + readUnsigned8BitInteger());
	}

	@Override
	public short readBigEndianSigned16BitInteger() throws NotEnoughBytesInvalidOperandStackException
	{
		try
		{
			return code.getShort();
		}
		catch (final BufferUnderflowException e)
		{
			throw new NotEnoughBytesInvalidOperandStackException(2, e);
		}
	}

	@Override
	public int readBigEndianSigned32BitInteger() throws NotEnoughBytesInvalidOperandStackException
	{
		try
		{
			return code.getInt();
		}
		catch (final BufferUnderflowException e)
		{
			throw new NotEnoughBytesInvalidOperandStackException(4, e);
		}
	}
}
