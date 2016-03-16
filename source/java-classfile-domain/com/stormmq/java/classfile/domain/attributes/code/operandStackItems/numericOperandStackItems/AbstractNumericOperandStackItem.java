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

package com.stormmq.java.classfile.domain.attributes.code.operandStackItems.numericOperandStackItems;

import com.stormmq.java.classfile.domain.attributes.code.invalidOperandStackExceptions.MismatchedTypeInvalidOperandStackException;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.AbstractOperandStackItem;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.operations.*;
import com.stormmq.java.classfile.domain.attributes.code.typing.ByteCharOrShort;
import com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory._int;

public abstract class AbstractNumericOperandStackItem<N extends Number> extends AbstractOperandStackItem implements NumericOperandStackItem<N>
{
	@NotNull private final ComputationalCategory computationalCategory;

	protected AbstractNumericOperandStackItem(@NotNull final ComputationalCategory computationalCategory)
	{
		if (computationalCategory.isNotNumeric)
		{
			throw new IllegalArgumentException("computationalCategory is not numeric");
		}
		this.computationalCategory = computationalCategory;
	}

	@Override
	public final boolean isNotOfComputationalCategory(@NotNull final ComputationalCategory computationalCategory)
	{
		return this.computationalCategory != computationalCategory;
	}

	@NotNull
	@Override
	public final NumericOperandStackItem<N> unaryOperation(@NotNull final UnaryOperation unaryOperation)
	{
		return new UnaryOperationNumericOperandStackItem<>(computationalCategory, this, unaryOperation);
	}

	@NotNull
	@Override
	public final NumericOperandStackItem<N> binaryOperation(@NotNull final BinaryOperation binaryOperation, @NotNull final NumericOperandStackItem<N> right) throws MismatchedTypeInvalidOperandStackException
	{
		if (right.isNotOfComputationalCategory(computationalCategory))
		{
			throw new MismatchedTypeInvalidOperandStackException(_int, "right must be of the same computational category for a binary operation, ");
		}
		return new BinaryOperationNumericOperandStackItem<>(computationalCategory, this, binaryOperation, right);
	}

	@NotNull
	@Override
	public final NumericOperandStackItem<N> integerBitOperation(@NotNull final IntegerBitOperation integerBitOperation, @NotNull final NumericOperandStackItem<N> right) throws MismatchedTypeInvalidOperandStackException
	{
		if (computationalCategory.isNotIntegerNumber)
		{
			throw new IllegalArgumentException("this computationalCategory is not an integer number");
		}
		if (right.isNotOfComputationalCategory(_int))
		{
			throw new MismatchedTypeInvalidOperandStackException(_int, "right for an integer bit operation must be of the computational category ");
		}
		return new IntegerBitOperationNumericOperandStackItem<>(computationalCategory, this, integerBitOperation, right);
	}

	@NotNull
	@Override
	public final <O extends Number> NumericOperandStackItem<O> convert(@NotNull final ComputationalCategory computationalCategory)
	{
		if (computationalCategory.isNotNumeric)
		{
			throw new IllegalArgumentException("computationalCategory is not numeric for conversion");
		}
		if (computationalCategory == this.computationalCategory)
		{
			throw new IllegalArgumentException("computationalCategory must not match");
		}
		return new ConvertedNumericOperandStackItem<>(computationalCategory, this);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	@Override
	public final NumericOperandStackItem<Integer> convert(@NotNull final ByteCharOrShort to) throws MismatchedTypeInvalidOperandStackException
	{
		if (computationalCategory != _int)
		{
			throw new IllegalArgumentException("this computationalCategory is not a 32-bit integer number");
		}
		if (isAlreadyByteCharOrShort())
		{
			throw new MismatchedTypeInvalidOperandStackException("Can not convert an already converted type");
		}
		return new FromSub32BitIntegerNumericOperandStackItem(_int, to, (NumericOperandStackItem<Integer>) this);
	}

	protected boolean isAlreadyByteCharOrShort()
	{
		return false;
	}

	@Override
	public final boolean isCategory1()
	{
		return computationalCategory.isCategory1;
	}

}
