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

package com.stormmq.java.classfile.domain.attributes.code.localVariables;

import com.stormmq.java.classfile.domain.names.FieldName;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLocalVariable
{
	public final char startProgramCounter;
	public final char length;
	@NotNull public final FieldName localVariableName;
	public final char localVariableIndex;

	protected AbstractLocalVariable(final char startProgramCounter, final char length, @NotNull final FieldName localVariableName, final char localVariableIndex)
	{
		this.startProgramCounter = startProgramCounter;
		this.length = length;
		this.localVariableName = localVariableName;
		this.localVariableIndex = localVariableIndex;
	}

	public final void validateNotAfterEndOfCode(final long codeLength) throws MismatchedLocalVariableLengthException
	{
		if (startProgramCounter >= codeLength)
		{
			throw new MismatchedLocalVariableLengthException("Variable entry exceeds code length");
		}
	}

	public final void validateDoesNotHaveALocalVariableIndexWhichIsTooLarge(final char maximumLocals) throws MismatchedLocalVariableLengthException
	{
		if (localVariableIndex >= maximumLocals)
		{
			throw new MismatchedLocalVariableLengthException("Variable entry exceeds maximum locals");
		}
	}
}
