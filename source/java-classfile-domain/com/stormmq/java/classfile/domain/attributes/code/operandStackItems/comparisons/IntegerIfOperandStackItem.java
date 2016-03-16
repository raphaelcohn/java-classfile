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

package com.stormmq.java.classfile.domain.attributes.code.operandStackItems.comparisons;

import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.AbstractOperandStackItem;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.DoNothingOperandStackItem;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.numericOperandStackItems.NumericOperandStackItem;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.operations.Comparison;
import org.jetbrains.annotations.NotNull;

public final class IntegerIfOperandStackItem extends AbstractOperandStackItem implements DoNothingOperandStackItem
{
	@NotNull private final NumericOperandStackItem<Integer> left;
	@NotNull private final Comparison comparison;
	@NotNull private final NumericOperandStackItem<Integer> right;
	private final short branch;

	public IntegerIfOperandStackItem(@NotNull final NumericOperandStackItem<Integer> left, @NotNull final Comparison comparison, @NotNull final NumericOperandStackItem<Integer> right, final short branch)
	{
		this.left = left;
		this.comparison = comparison;
		this.right = right;
		this.branch = branch;
	}

	@Override
	public boolean isCategory1()
	{
		return false;
	}
}
