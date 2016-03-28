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
import com.stormmq.java.classfile.domain.attributes.code.codeReaders.ByteBufferCodeReader;
import com.stormmq.java.classfile.domain.attributes.code.codeReaders.CodeReader;
import com.stormmq.java.classfile.domain.attributes.code.constants.RuntimeConstantPool;
import com.stormmq.java.classfile.domain.attributes.code.invalidOperandStackExceptions.*;
import com.stormmq.java.classfile.domain.attributes.code.localVariables.*;
import com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.InvalidOpcodeException;
import com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.OpcodeParser;
import com.stormmq.java.classfile.domain.attributes.code.operandStack.OperandStack;
import com.stormmq.java.classfile.domain.attributes.code.operandStack.TrackingOperandStack;
import com.stormmq.java.classfile.domain.attributes.code.stackMapFrames.StackMapFrame;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.OpcodeParser.chooseOpcodeParsers;
import static java.util.Collections.emptySet;

public final class Code
{
	public static final long MaximumCodeLength = 65535L;
	@NotNull private static final Set<Character> NoLineNumbers = emptySet();

	private final char maximumDepthOfTheOperandStackOfTheMethod;
	private final char maximumLocals;
	private final long codeLength;
	@NotNull private final ByteBuffer code;
	@NotNull private final ExceptionCode[] exceptionCode;
	private final Map<Character, Set<Character>> programCounterToLineNumberEntryMap;
	private final LocalVariables localVariables;
	private final StackMapFrame[] stackMapFrames;
	@NotNull private final UnknownAttributes unknownAttributes;
	@NotNull private final TypeAnnotation[] visibleTypeAnnotations;
	@NotNull private final TypeAnnotation[] invisibleTypeAnnotations;
	private final boolean opcode186IsPermittedBecauseThisIsForJava7OrLater;
	@NotNull private final RuntimeConstantPool runtimeConstantPool;

	public Code(@NotNull final RuntimeConstantPool runtimeConstantPool, final char maximumDepthOfTheOperandStackOfTheMethod, final char maximumLocals, final long codeLength, @NotNull final ByteBuffer code, @NotNull final ExceptionCode[] exceptionCode, @NotNull final Map<Character, Set<Character>> programCounterToLineNumberEntryMap, @NotNull final LocalVariables localVariables, final StackMapFrame[] stackMapFrames, @NotNull final UnknownAttributes unknownAttributes, @NotNull final TypeAnnotation[] visibleTypeAnnotations, @NotNull final TypeAnnotation[] invisibleTypeAnnotations, final boolean opcode186IsPermittedBecauseThisIsForJava7OrLater)
	{
		this.runtimeConstantPool = runtimeConstantPool;
		if (codeLength <= 0L)
		{
			throw new IllegalArgumentException(Formatting.format("code length can not be zero or less bytes (ie not '%1$s')", codeLength));
		}

		if (codeLength > MaximumCodeLength)
		{
			throw new IllegalArgumentException(Formatting.format("code length can not be more than '%1$s' bytes (ie not '%2$s')", MaximumCodeLength, codeLength));
		}

		this.maximumDepthOfTheOperandStackOfTheMethod = maximumDepthOfTheOperandStackOfTheMethod;
		this.maximumLocals = maximumLocals;
		this.codeLength = codeLength;
		this.code = code;
		this.exceptionCode = exceptionCode;
		this.programCounterToLineNumberEntryMap = programCounterToLineNumberEntryMap;
		this.localVariables = localVariables;
		this.stackMapFrames = stackMapFrames;
		this.unknownAttributes = unknownAttributes;
		this.visibleTypeAnnotations = visibleTypeAnnotations;
		this.invisibleTypeAnnotations = invisibleTypeAnnotations;
		this.opcode186IsPermittedBecauseThisIsForJava7OrLater = opcode186IsPermittedBecauseThisIsForJava7OrLater;
	}

	public void parseCode(final boolean isStrictFloatingPoint) throws InvalidOpcodeException, UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException, NotEnoughBytesInvalidOperandStackException, OverflowInvalidOperandStackException
	{
		final OperandStack operandStack = new TrackingOperandStack(maximumDepthOfTheOperandStackOfTheMethod);

		final OpcodeParser[] opcodeParsers = chooseOpcodeParsers(opcode186IsPermittedBecauseThisIsForJava7OrLater, isStrictFloatingPoint);

		code.position(0);

		final CodeReader codeReader = new ByteBufferCodeReader(code);

		char programCounter = 0;
		do
		{
			final short opcode = codeReader.readUnsigned8BitInteger();
			final OpcodeParser opcodeParser = opcodeParsers[opcode];
			final char length = opcodeParser.length();

			final int futureProgramCounter = programCounter + length;
			if (futureProgramCounter > codeLength)
			{
				throw new NotEnoughBytesInvalidOperandStackException(futureProgramCounter - codeLength);
			}

			final Set<Character> lineNumbers = programCounterToLineNumberEntryMap.getOrDefault(programCounter, NoLineNumbers);
			final Set<LocalVariableAtProgramCounter> localVariablesAtProgramCounter = localVariables.getForProgramCounter(programCounter, length);

			opcodeParser.parse(operandStack, codeReader, lineNumbers, localVariablesAtProgramCounter, runtimeConstantPool);
			programCounter = (char) futureProgramCounter;
		}
		while (programCounter != codeLength);
	}
}
