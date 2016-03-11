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

package com.stormmq.java.classfile.domain.attributes.code.operandStack;

import org.jetbrains.annotations.NotNull;

public final class OperandStack
{
	@NotNull private final OperandStackItem[] operandStack;
	private final int maximumPointer;
	private int pointer;

	public OperandStack(final char maximumDepthOfTheOperandStackOfTheMethod)
	{
		operandStack = new OperandStackItem[maximumDepthOfTheOperandStackOfTheMethod];
		maximumPointer = maximumDepthOfTheOperandStackOfTheMethod - 1;
		pointer = -1;
	}

	@SuppressWarnings("AssignmentToNull")
	@NotNull
	public OperandStackItem pop() throws StackUnderflowException
	{
		if (pointer == -1)
		{
			throw new StackUnderflowException();
		}
		final OperandStackItem operandStackItem = operandStack[pointer];
		operandStack[pointer] = null;
		pointer--;
		return operandStackItem;
	}

	public void push(@NotNull final OperandStackItem operandStackItem) throws StackOverflowException
	{
		if (pointer == maximumPointer)
		{
			throw new StackOverflowException();
		}
		pointer++;
		operandStack[pointer] = operandStackItem;
	}
}
