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
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.referenceOperandStackItems.ReferenceOperandStackItem;
import com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory.reference;

public final class TrackingOperandStack implements OperandStack
{
	@NotNull @NonNls private static final String LeftHandMessage = "Operand stack is mismatched; we expected ";

	@NotNull private final OperandStackItem[] operandStack;
	private final int maximumPointer;
	private int stackPointer;

	public TrackingOperandStack(final char maximumDepthOfTheOperandStackOfTheMethod)
	{
		operandStack = new OperandStackItem[maximumDepthOfTheOperandStackOfTheMethod];
		maximumPointer = maximumDepthOfTheOperandStackOfTheMethod - 1;
		stackPointer = EmptyStackPointer;
	}

	@Override
	public int currentStackPointer()
	{
		return stackPointer;
	}

	@Override
	@SuppressWarnings("AssignmentToNull")
	@NotNull
	public OperandStackItem pop() throws UnderflowInvalidOperandStackException
	{
		if (stackPointer == EmptyStackPointer)
		{
			throw new UnderflowInvalidOperandStackException();
		}
		final OperandStackItem operandStackItem = operandStack[stackPointer];
		operandStack[stackPointer] = null;
		stackPointer--;
		return operandStackItem;
	}

	@Override
	@NotNull
	public OperandStackItem popCategory1ComputationalType() throws UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException
	{
		final OperandStackItem pop = pop();
		if (pop.isCategory1())
		{
			return pop;
		}
		throw new MismatchedTypeInvalidOperandStackException("Operand stack is mismatched; we expected a Category 1 Computational Type");
	}

	@Override
	@NotNull
	public OperandStackItem popCategory2ComputationalType() throws UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException
	{
		final OperandStackItem pop = pop();
		if (pop.isCategory1())
		{
			throw new MismatchedTypeInvalidOperandStackException("Operand stack is mismatched; we expected a Category 2 Computational Type");
		}
		return pop;
	}

	@SuppressWarnings("unchecked")
	@Override
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
			return (NumericOperandStackItem<N>) popped;
		}
		throw new MismatchedTypeInvalidOperandStackException(computationalCategory, LeftHandMessage);
	}

	@Override
	@NotNull
	public ReferenceOperandStackItem popReference() throws UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException
	{
		final OperandStackItem pop = pop();
		if (pop instanceof ReferenceOperandStackItem)
		{
			return  (ReferenceOperandStackItem) pop;
		}
		throw new MismatchedTypeInvalidOperandStackException(reference, LeftHandMessage);
	}

	@Override
	public int push(@NotNull final OperandStackItem operandStackItem) throws OverflowInvalidOperandStackException
	{
		if (stackPointer == maximumPointer)
		{
			throw new OverflowInvalidOperandStackException();
		}
		stackPointer++;
		final int oldStackPointer = operandStackItem.push(stackPointer);
		operandStack[stackPointer] = operandStackItem;
		return oldStackPointer;
	}

	@Override
	public int pushWithCertainty(@NotNull final OperandStackItem operandStackItem)
	{
		final int oldStackPointer;
		try
		{
			oldStackPointer = push(operandStackItem);
		}
		catch (final OverflowInvalidOperandStackException e)
		{
			throw new IllegalStateException("There is supposed to be space on the operand stack", e);
		}
		return oldStackPointer;
	}

	@Override
	public void unchanged(@NotNull final DoNothingOperandStackItem doNothingOperandStackItem)
	{
	}
}
