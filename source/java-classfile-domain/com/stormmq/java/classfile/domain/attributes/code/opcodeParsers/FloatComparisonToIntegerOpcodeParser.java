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
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.numericOperandStackItems.FloatComparisonToIntegerNumericOperandStackItem;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.numericOperandStackItems.NumericOperandStackItem;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import static com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory._float;

public final class FloatComparisonToIntegerOpcodeParser extends AbstractOneOpcodeParser
{
	private final int nanValue;

	public FloatComparisonToIntegerOpcodeParser(final int nanValue)
	{
		this.nanValue = nanValue;
	}

	@Override
	public void parse(@NotNull final OperandStack operandStack, @NotNull final CodeReader codeReader, @NotNull final Set<Character> lineNumbers, @NotNull final Map<Character, LocalVariableAtProgramCounter> localVariablesAtProgramCounter, @NotNull final RuntimeConstantPool runtimeConstantPool) throws InvalidOpcodeException, UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException, OverflowInvalidOperandStackException, NotEnoughBytesInvalidOperandStackException
	{
		final NumericOperandStackItem<Float> value2 = operandStack.popNumeric(_float);
		final NumericOperandStackItem<Float> value1 = operandStack.popNumeric(_float);
		operandStack.pushWithCertainty(new FloatComparisonToIntegerNumericOperandStackItem(value1, value2, nanValue));
	}
}
