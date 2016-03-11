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

package com.stormmq.java.classfile.domain.attributes.code;

import com.stormmq.java.classfile.domain.attributes.UnknownAttributes;
import com.stormmq.java.classfile.domain.attributes.annotations.TypeAnnotation;
import com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.InvalidOpcodeException;
import com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.OpcodeParser;
import com.stormmq.java.classfile.domain.attributes.code.operandStack.OperandStack;
import com.stormmq.java.classfile.domain.attributes.code.stackMapFrames.StackMapFrame;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Locale;

import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.OpcodeParser.Java6AndEarlierOpcodeParser;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.OpcodeParser.Java7AndLaterOpcodeParsers;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.OpcodeParser.unsigned8BitInteger;

public final class Code
{
	public static final long MaximumCodeLength = 65535L;

	private final char maximumDepthOfTheOperandStackOfTheMethod;
	private final char maximumLocals;
	private final long codeLength;
	@NotNull private final ByteBuffer code;
	@NotNull private final ExceptionCode[] exceptionCode;
	private final List<LineNumberEntry[]> lineNumberEntries;
	private final List<LocalVariable[]> localVariables;
	private final List<LocalVariableType[]> localVariableTypes;
	private final StackMapFrame[] stackMapFrames;
	@NotNull private final UnknownAttributes unknownAttributes;
	@NotNull private final TypeAnnotation[] visibleTypeAnnotations;
	@NotNull private final TypeAnnotation[] invisibleTypeAnnotations;
	private final boolean opcode186IsPermittedBecauseThisIsForJava7OrLater;

	public Code(final char maximumDepthOfTheOperandStackOfTheMethod, final char maximumLocals, final long codeLength, @NotNull final ByteBuffer code, @NotNull final ExceptionCode[] exceptionCode, final List<LineNumberEntry[]> lineNumberEntries, final List<LocalVariable[]> localVariables, final List<LocalVariableType[]> localVariableTypes, final StackMapFrame[] stackMapFrames, @NotNull final UnknownAttributes unknownAttributes, @NotNull final TypeAnnotation[] visibleTypeAnnotations, @NotNull final TypeAnnotation[] invisibleTypeAnnotations, final boolean opcode186IsPermittedBecauseThisIsForJava7OrLater)
	{
		if (codeLength <= 0L)
		{
			throw new IllegalArgumentException(String.format(Locale.ENGLISH, "code length can not be zero or less bytes (ie not '%1$s')", codeLength));
		}
		if (codeLength > MaximumCodeLength)
		{
			throw new IllegalArgumentException(String.format(Locale.ENGLISH, "code length can not be more than '%1$s' bytes (ie not '%2$s')", MaximumCodeLength, codeLength));
		}
		this.maximumDepthOfTheOperandStackOfTheMethod = maximumDepthOfTheOperandStackOfTheMethod;
		this.maximumLocals = maximumLocals;
		this.codeLength = codeLength;
		this.code = code;
		this.exceptionCode = exceptionCode;
		this.lineNumberEntries = lineNumberEntries;
		this.localVariables = localVariables;
		this.localVariableTypes = localVariableTypes;
		this.stackMapFrames = stackMapFrames;
		this.unknownAttributes = unknownAttributes;
		this.visibleTypeAnnotations = visibleTypeAnnotations;
		this.invisibleTypeAnnotations = invisibleTypeAnnotations;
		this.opcode186IsPermittedBecauseThisIsForJava7OrLater = opcode186IsPermittedBecauseThisIsForJava7OrLater;
	}

	public void parseCode() throws InvalidOpcodeException
	{
		final OperandStack operandStack = new OperandStack(maximumDepthOfTheOperandStackOfTheMethod);

		final OpcodeParser[] opcodeParsers = opcode186IsPermittedBecauseThisIsForJava7OrLater ? Java7AndLaterOpcodeParsers : Java6AndEarlierOpcodeParser;

		code.position(0);

		int programCounter = 0;
		do
		{
			final int opcode = unsigned8BitInteger(code);
			opcodeParsers[opcode].parse(operandStack);


			programCounter += 1;
		}
		while (programCounter < codeLength);
	}

}
