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
import com.stormmq.java.classfile.domain.attributes.code.invalidOperandStackExceptions.NotEnoughBytesInvalidOperandStackException;
import com.stormmq.java.classfile.domain.attributes.code.localVariables.LocalVariableAtProgramCounter;
import com.stormmq.java.classfile.domain.attributes.code.operandStack.OperandStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class InvokeDynamicOpcodeParser extends AbstractFourOpcodeParser
{
	@SuppressWarnings({"ThrowableInstanceNeverThrown", "SpellCheckingInspection"}) @NotNull private static final InvalidOpcodeException ThirdByteInvalid = new InvalidOpcodeException("The third byte for invokedynamic must always be zero");
	@SuppressWarnings({"ThrowableInstanceNeverThrown", "SpellCheckingInspection"}) @NotNull private static final InvalidOpcodeException FourthByteInvalid = new InvalidOpcodeException("The fourth byte for invokedynamic must always be zero");
	@NotNull public static final OpcodeParser InvokeDynamicAllowed = new InvokeDynamicOpcodeParser();

	private InvokeDynamicOpcodeParser()
	{
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public void parse(@NotNull final OperandStack operandStack, @NotNull final CodeReader codeReader, @NotNull final Set<Character> lineNumbers, @NotNull final Set<LocalVariableAtProgramCounter> localVariablesAtProgramCounter, @NotNull final RuntimeConstantPool runtimeConstantPool) throws NotEnoughBytesInvalidOperandStackException, InvalidOpcodeException
	{
		final char index = codeReader.readBigEndianUnsigned16BitInteger();
		validateIsAlwaysZero(codeReader, ThirdByteInvalid);
		validateIsAlwaysZero(codeReader, FourthByteInvalid);

		// https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.invokedynamic
		// look up index in constant pool...

		// look up bootstrap method..., call with 4 + n arguments, returns a CallSite
		// Interpret operand stack as if reference to CallSite + n arguments, do invoke kind as per bootstrap method

		// pop 4 arguments + n (n is 0 or more)

		// call bootstrap method ONCE, returns a CallSite object (ie for a given input set of arguments on the stack, get a given callsite object back)
		// So we could look up the bootstrap method, and pop the necessary arguments, and create a dummy CallSite object [to be resolved]
		// We may be able to cheat here, if we known that all the predecessor arguments of the stack are constants - otherwise we have to do resolution at runtime

		throw new UnsupportedOperationException("invokedynamic is not yet fully supported");
	}

	private static void validateIsAlwaysZero(@NotNull final CodeReader codeReader, @NotNull final InvalidOpcodeException invalidOpcodeException) throws NotEnoughBytesInvalidOperandStackException, InvalidOpcodeException
	{
		if (codeReader.readSignedBBitInteger() != 0)
		{
			throw invalidOpcodeException;
		}
	}
}
