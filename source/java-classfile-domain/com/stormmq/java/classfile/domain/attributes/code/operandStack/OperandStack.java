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

import com.stormmq.java.classfile.domain.attributes.code.invalidOperandStackExceptions.*;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.DoNothingOperandStackItem;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.OperandStackItem;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.numericOperandStackItems.NumericOperandStackItem;
import com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory.reference;

public final class OperandStack
{
	@NotNull @NonNls private static final String LeftHandMessage = "Operand stack is mismatched; we expected ";

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
	private OperandStackItem pop() throws UnderflowInvalidOperandStackException
	{
		if (pointer == -1)
		{
			throw new UnderflowInvalidOperandStackException();
		}
		final OperandStackItem operandStackItem = operandStack[pointer];
		operandStack[pointer] = null;
		pointer--;
		return operandStackItem;
	}

	@NotNull
	public <N extends Number> NumericOperandStackItem<N> popNumeric(@NotNull final ComputationalCategory computationalCategory) throws UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException
	{
		final OperandStackItem pop = pop();
		if (pop instanceof NumericOperandStackItem)
		{
			final NumericOperandStackItem<?> popped = (NumericOperandStackItem<?>) pop;
			if (popped.isNotOfComputationalCategory(computationalCategory))
			{
				throw new MismatchedTypeInvalidOperandStackException(computationalCategory, LeftHandMessage);
			}
		}
		throw new MismatchedTypeInvalidOperandStackException(computationalCategory, LeftHandMessage);
	}

	@NotNull
	public ReferenceOperandStackItem popReference() throws UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException
	{
		final OperandStackItem pop = pop();
		if (pop instanceof ReferenceOperandStackItem)
		{
			return (ReferenceOperandStackItem) pop;
		}
		throw new MismatchedTypeInvalidOperandStackException(reference, LeftHandMessage);
	}

	public void push(@NotNull final OperandStackItem operandStackItem) throws OverflowInvalidOperandStackException
	{
		if (pointer == maximumPointer)
		{
			throw new OverflowInvalidOperandStackException();
		}
		pointer++;
		operandStack[pointer] = operandStackItem;
	}

	public void pushWithCertainty(@NotNull final OperandStackItem operandStackItem)
	{
		try
		{
			push(operandStackItem);
		}
		catch (final OverflowInvalidOperandStackException e)
		{
			throw new IllegalStateException("There is supposed to be space on the operand stack", e);
		}
	}

	public void unchanged(@NotNull final DoNothingOperandStackItem doNothingOperandStackItem)
	{
	}
}
