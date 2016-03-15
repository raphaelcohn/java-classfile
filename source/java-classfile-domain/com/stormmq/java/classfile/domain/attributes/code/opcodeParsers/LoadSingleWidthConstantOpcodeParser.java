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

package com.stormmq.java.classfile.domain.attributes.code.opcodeParsers;

import com.stormmq.java.classfile.domain.attributes.code.codeReaders.CodeReader;
import com.stormmq.java.classfile.domain.attributes.code.constants.*;
import com.stormmq.java.classfile.domain.attributes.code.invalidOperandStackExceptions.*;
import com.stormmq.java.classfile.domain.attributes.code.localVariables.LocalVariableAtProgramCounter;
import com.stormmq.java.classfile.domain.attributes.code.operandStack.OperandStack;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.constantOperandStackItems.ConstantOperandStackItem;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import static com.stormmq.java.classfile.domain.attributes.code.constants.SingleWidthConstantForLoadUser.SingleWidthConstantForLoadUserInstance;

public enum LoadSingleWidthConstantOpcodeParser implements OpcodeParser
{
	LoadNarrowSingleWidthConstant((char) 2)
	{
		@Override
		protected char readIndex(@NotNull final CodeReader codeReader) throws NotEnoughBytesInvalidOperandStackException
		{
			return (char) codeReader.readUnsigned8BitInteger();
		}
	},
	LoadWideSingleWidthConstant((char) 3)
	{
		@Override
		protected char readIndex(@NotNull final CodeReader codeReader) throws NotEnoughBytesInvalidOperandStackException
		{
			return codeReader.readBigEndianUnsigned16BitInteger();
		}
	},
	;

	private final char length;

	LoadSingleWidthConstantOpcodeParser(final char length)
	{
		this.length = length;
	}

	@Override
	public final char length() throws InvalidOpcodeException
	{
		return length;
	}

	@Override
	public void parse(@NotNull final OperandStack operandStack, @NotNull final CodeReader codeReader, @NotNull final Set<Character> lineNumbers, @NotNull final Map<Character, LocalVariableAtProgramCounter> localVariablesAtProgramCounter, @NotNull final RuntimeConstantPool runtimeConstantPool) throws InvalidOpcodeException, UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException, OverflowInvalidOperandStackException, NotEnoughBytesInvalidOperandStackException
	{
		final char index = readIndex(codeReader);
		final ConstantOperandStackItem constantOperandStackItem;
		try
		{
			constantOperandStackItem = runtimeConstantPool.singleWidthConstantForLoad(index, SingleWidthConstantForLoadUserInstance);
		}
		catch (final InvalidConstantException e)
		{
			throw new InvalidOpcodeException("Could not resolve single width constant", e);
		}

		operandStack.push(constantOperandStackItem);
	}

	protected abstract char readIndex(@NotNull final CodeReader codeReader) throws NotEnoughBytesInvalidOperandStackException;
}
