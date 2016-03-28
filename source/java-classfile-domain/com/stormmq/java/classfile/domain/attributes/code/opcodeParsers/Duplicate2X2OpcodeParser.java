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
import com.stormmq.java.classfile.domain.attributes.code.constants.RuntimeConstantPool;
import com.stormmq.java.classfile.domain.attributes.code.invalidOperandStackExceptions.*;
import com.stormmq.java.classfile.domain.attributes.code.localVariables.LocalVariableAtProgramCounter;
import com.stormmq.java.classfile.domain.attributes.code.operandStack.OperandStack;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.OperandStackItem;
import org.jetbrains.annotations.*;

import java.util.Set;

public final class Duplicate2X2OpcodeParser extends AbstractOneOpcodeParser
{
	@NotNull public static final OpcodeParser Duplicate2X2 = new Duplicate2X2OpcodeParser();

	private Duplicate2X2OpcodeParser()
	{
	}

	@Override
	public void parse(@NotNull final OperandStack operandStack, @NotNull final CodeReader codeReader, @NotNull final Set<Character> lineNumbers, @NotNull final Set<LocalVariableAtProgramCounter> localVariablesAtProgramCounter, @NotNull final RuntimeConstantPool runtimeConstantPool) throws InvalidOpcodeException, UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException, OverflowInvalidOperandStackException
	{
		final OperandStackItem value1 = operandStack.pop();
		if (value1.isCategory1())
		{
			final OperandStackItem value2 = operandStack.pop();
			if (value2.isCategory1())
			{
				final OperandStackItem value3 = operandStack.pop();
				// "Form 1"
				if (value3.isCategory1())
				{
					final OperandStackItem value4 = operandStack.popCategory1ComputationalType();
					operandStack.pushWithCertainty(value2);
					operandStack.pushWithCertainty(value1);
					operandStack.pushWithCertainty(value4);
					operandStack.pushWithCertainty(value3);
					operandStack.push(value2);
					operandStack.push(value1);
				}
				// "Form 3"
				else
				{
					operandStack.pushWithCertainty(value2);
					operandStack.pushWithCertainty(value1);
					operandStack.pushWithCertainty(value3);
					operandStack.push(value2);
					operandStack.push(value1);
				}
			}
			else
			{
				throw new InvalidOpcodeException("dup_x2 has category 1 value1 but category 2 value2");
			}
		}
		else
		{
			final OperandStackItem value2 = operandStack.pop();
			// "Form 2"
			if (value2.isCategory1())
			{
				final OperandStackItem value3 = operandStack.popCategory1ComputationalType();
				operandStack.pushWithCertainty(value1);
				operandStack.pushWithCertainty(value3);
				operandStack.pushWithCertainty(value2);
				operandStack.push(value1);
			}
			// "Form 4"
			else
			{
				operandStack.pushWithCertainty(value1);
				operandStack.pushWithCertainty(value2);
				operandStack.push(value1);
			}
		}
	}
}
