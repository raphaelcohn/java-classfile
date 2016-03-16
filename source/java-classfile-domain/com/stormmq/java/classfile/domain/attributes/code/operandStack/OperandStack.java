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
import org.jetbrains.annotations.NotNull;

public interface OperandStack
{
	int EmptyStackPointer = -1;

	int currentStackPointer();

	@SuppressWarnings("AssignmentToNull")
	@NotNull
	OperandStackItem pop() throws UnderflowInvalidOperandStackException;

	@NotNull
	OperandStackItem popCategory1ComputationalType() throws UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException;

	@NotNull
	OperandStackItem popCategory2ComputationalType() throws UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException;

	@NotNull
	<N extends Number> NumericOperandStackItem<N> popNumeric(@NotNull final ComputationalCategory computationalCategory) throws UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException;

	@NotNull
	ReferenceOperandStackItem popReference() throws UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException;

	int push(@NotNull final OperandStackItem operandStackItem) throws OverflowInvalidOperandStackException;

	int pushWithCertainty(@NotNull final OperandStackItem operandStackItem);

	void unchanged(@NotNull final DoNothingOperandStackItem doNothingOperandStackItem);
}
