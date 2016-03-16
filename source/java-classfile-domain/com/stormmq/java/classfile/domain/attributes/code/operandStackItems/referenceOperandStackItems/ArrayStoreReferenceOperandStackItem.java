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

package com.stormmq.java.classfile.domain.attributes.code.operandStackItems.referenceOperandStackItems;

import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.AbstractOperandStackItem;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.numericOperandStackItems.NumericOperandStackItem;
import org.jetbrains.annotations.NotNull;

public final class ArrayStoreReferenceOperandStackItem extends AbstractOperandStackItem implements ReferenceOperandStackItem
{
	@NotNull private final ReferenceOperandStackItem arrayReference;
	@NotNull private final NumericOperandStackItem<Integer> index;

	public ArrayStoreReferenceOperandStackItem(@NotNull final ReferenceOperandStackItem arrayReference, @NotNull final NumericOperandStackItem<Integer> index)
	{
		this.arrayReference = arrayReference;
		this.index = index;
	}
}
