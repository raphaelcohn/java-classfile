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
import com.stormmq.java.classfile.domain.attributes.code.constants.*;
import com.stormmq.java.classfile.domain.attributes.code.invalidOperandStackExceptions.*;
import com.stormmq.java.classfile.domain.attributes.code.localVariables.LocalVariableAtProgramCounter;
import com.stormmq.java.classfile.domain.attributes.code.operandStack.OperandStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.BinaryOperationOpcodeParser.binaryOperationOpcodeParser;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.BytePushOpcodeParser.BytePush;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.IntegerBitOperationOpcodeParser.integerBitOperationOpcodeParser;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.InvokeDynamicNotAllowedOpcodeParser.InvokeDynamicNotAllowed;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.LoadDoubleWidthConstantOpcodeParser.LoadWideDoubleWidthConstant;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.LoadSingleWidthConstantOpcodeParser.LoadNarrowSingleWidthConstant;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.LoadSingleWidthConstantOpcodeParser.LoadWideSingleWidthConstant;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.NoOperationOpcodeParser.NoOperation;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.PushConstantOpcodeParser.pushConstantOpcodeParser;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.ShortPushOpcodeParser.ShortPush;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.UnaryOperationOpcodeParser.unaryOperationOpcodeParser;
import static com.stormmq.java.classfile.domain.attributes.code.operandStackItems.operations.BinaryOperation.*;
import static com.stormmq.java.classfile.domain.attributes.code.operandStackItems.operations.IntegerBitOperation.*;
import static com.stormmq.java.classfile.domain.attributes.code.operandStackItems.operations.UnaryOperation.negate;
import static com.stormmq.java.classfile.domain.attributes.code.operandStackItems.constantOperandStackItems.NullReferenceConstantOperandStackItem.NullConstant;
import static com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory.*;

public interface OpcodeParser
{
	char length() throws InvalidOpcodeException;

	void parse(@NotNull final OperandStack operandStack, @NotNull final CodeReader codeReader, @NotNull final Set<Character> lineNumbers, @NotNull final Map<Character, LocalVariableAtProgramCounter> localVariablesAtProgramCounter, @NotNull final RuntimeConstantPool runtimeConstantPool) throws InvalidOpcodeException, UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException, OverflowInvalidOperandStackException, NotEnoughBytesInvalidOperandStackException;

	short nop = 0;

	short aconst_null = 1;

	short iconst_m1 = 2;

	short iconst_0 = 3;

	short iconst_1 = 4;

	short iconst_2 = 5;

	short iconst_3 = 6;

	short iconst_4 = 7;

	short iconst_5 = 8;

	short lconst_0 = 9;

	short lconst_1 = 10;

	short fconst_0 = 11;

	short fconst_1 = 12;

	short fconst_2 = 13;

	short dconst_0 = 14;

	short dconst_1 = 15;

	short bipush = 16;

	short sipush = 17;

	short ldc = 18;

	short ldc_w = 19;

	short ldc2_w = 20;




	short iadd = 96;

	short ladd = 97;

	short fadd = 98;

	short dadd = 99;

	short isub = 100;

	short lsub = 101;

	short fsub = 102;

	short dsub = 103;

	short imul = 104;

	short lmul = 105;

	short fmul = 106;

	short dmul = 107;

	short idiv = 108;

	short ldiv = 109;

	short fdiv = 110;

	short ddiv = 111;

	short irem = 112;

	short lrem = 113;

	short frem = 114;

	short drem = 115;

	short ineg = 116;

	short lneg = 117;

	short fneg = 118;

	short dneg = 119;

	short ishl = 120;

	short lshl = 121;

	short ishr = 122;

	short lshr = 123;

	short iushr = 124;

	short lushr = 125;

	short iand = 126;

	short land = 127;

	short ior = 128;

	short lor = 129;

	short ixor = 130;

	short lxor = 131;

	short iinc = 132;


	short invokedynamic = 186;

	short breakpoint = 202;

	short impdep1 = 254;

	short impdep2 = 255;

	@NotNull OpcodeParser[] Java7AndLaterOpcodeParsers = initialiseOpcodeParsers(true, false);
	@NotNull OpcodeParser[] Java6AndEarlierOpcodeParser = initialiseOpcodeParsers(false, false);
	@NotNull OpcodeParser[] Java7AndLaterOpcodeParsersStrictFloatingPoint = initialiseOpcodeParsers(true, true);
	@NotNull OpcodeParser[] Java6AndEarlierOpcodeParserStrictFloatingPoint = initialiseOpcodeParsers(false, true);

	@NotNull
	static OpcodeParser[] chooseOpcodeParsers(final boolean opcode186IsPermittedBecauseThisIsForJava7OrLater, final boolean isStrictFloatingPoint)
	{
		if (opcode186IsPermittedBecauseThisIsForJava7OrLater)
		{
			if (isStrictFloatingPoint)
			{
				return Java7AndLaterOpcodeParsersStrictFloatingPoint;
			}
			else
			{
				return Java7AndLaterOpcodeParsers;
			}
		}
		if (isStrictFloatingPoint)
		{
			return Java6AndEarlierOpcodeParserStrictFloatingPoint;
		}
		else
		{
			return Java6AndEarlierOpcodeParser;
		}
	}

	@NotNull
	static OpcodeParser[] initialiseOpcodeParsers(final boolean opcode186IsPermittedBecauseThisIsForJava7OrLater, final boolean isStrictFloatingPoint)
	{
		final short length = 256;

		final OpcodeParser[] opcodeParsers = new OpcodeParser[length];

		opcodeParsers[breakpoint] = new ReservedOpcodeParser(breakpoint, "breakpoint");
		opcodeParsers[impdep1] = new ReservedOpcodeParser(impdep1, "impdep1");
		opcodeParsers[impdep2] = new ReservedOpcodeParser(impdep2, "impdep2");

		opcodeParsers[invokedynamic] = opcode186IsPermittedBecauseThisIsForJava7OrLater ? InvokeDynamicOpcodeParser.InvokeDynamicAllowed : InvokeDynamicNotAllowed;

		// Constants
		opcodeParsers[nop] = NoOperation;
		opcodeParsers[aconst_null] = new PushConstantOpcodeParser(NullConstant);
		opcodeParsers[iconst_m1] = pushConstantOpcodeParser(_int, -1);
		opcodeParsers[iconst_0] = pushConstantOpcodeParser(_int, 0);
		opcodeParsers[iconst_1] = pushConstantOpcodeParser(_int, 1);
		opcodeParsers[iconst_2] = pushConstantOpcodeParser(_int, 2);
		opcodeParsers[iconst_3] = pushConstantOpcodeParser(_int, 3);
		opcodeParsers[iconst_4] = pushConstantOpcodeParser(_int, 4);
		opcodeParsers[iconst_5] = pushConstantOpcodeParser(_int, 5);
		opcodeParsers[lconst_0] = pushConstantOpcodeParser(_long, 0L);
		opcodeParsers[lconst_1] = pushConstantOpcodeParser(_long, 1L);
		opcodeParsers[fconst_0] = pushConstantOpcodeParser(_float, 0.0f);
		opcodeParsers[fconst_1] = pushConstantOpcodeParser(_float, 1.0f);
		opcodeParsers[fconst_2] = pushConstantOpcodeParser(_float, 2.0f);
		opcodeParsers[dconst_0] = pushConstantOpcodeParser(_double, 0.0d);
		opcodeParsers[dconst_1] = pushConstantOpcodeParser(_double, 1.0d);
		opcodeParsers[bipush] = BytePush;
		opcodeParsers[sipush] = ShortPush;
		opcodeParsers[ldc] = LoadNarrowSingleWidthConstant;
		opcodeParsers[ldc_w] = LoadWideSingleWidthConstant;
		opcodeParsers[ldc2_w] = LoadWideDoubleWidthConstant;

		// Math
		opcodeParsers[iadd] = binaryOperationOpcodeParser(_int, add, isStrictFloatingPoint);
		opcodeParsers[ladd] = binaryOperationOpcodeParser(_long, add, isStrictFloatingPoint);
		opcodeParsers[fadd] = binaryOperationOpcodeParser(_float, add, isStrictFloatingPoint);
		opcodeParsers[dadd] = binaryOperationOpcodeParser(_double, add, isStrictFloatingPoint);
		opcodeParsers[isub] = binaryOperationOpcodeParser(_int, subtract, isStrictFloatingPoint);
		opcodeParsers[lsub] = binaryOperationOpcodeParser(_long, subtract, isStrictFloatingPoint);
		opcodeParsers[fsub] = binaryOperationOpcodeParser(_float, subtract, isStrictFloatingPoint);
		opcodeParsers[dsub] = binaryOperationOpcodeParser(_double, subtract, isStrictFloatingPoint);
		opcodeParsers[imul] = binaryOperationOpcodeParser(_int, multiply, isStrictFloatingPoint);
		opcodeParsers[lmul] = binaryOperationOpcodeParser(_long, multiply, isStrictFloatingPoint);
		opcodeParsers[fmul] = binaryOperationOpcodeParser(_float, multiply, isStrictFloatingPoint);
		opcodeParsers[dmul] = binaryOperationOpcodeParser(_double, multiply, isStrictFloatingPoint);
		opcodeParsers[idiv] = binaryOperationOpcodeParser(_int, divide, isStrictFloatingPoint);
		opcodeParsers[ldiv] = binaryOperationOpcodeParser(_long, divide, isStrictFloatingPoint);
		opcodeParsers[fdiv] = binaryOperationOpcodeParser(_float, divide, isStrictFloatingPoint);
		opcodeParsers[ddiv] = binaryOperationOpcodeParser(_double, divide, isStrictFloatingPoint);
		opcodeParsers[irem] = binaryOperationOpcodeParser(_int, remainder, isStrictFloatingPoint);
		opcodeParsers[lrem] = binaryOperationOpcodeParser(_long, remainder, isStrictFloatingPoint);
		opcodeParsers[frem] = binaryOperationOpcodeParser(_float, remainder, isStrictFloatingPoint);
		opcodeParsers[drem] = binaryOperationOpcodeParser(_double, remainder, isStrictFloatingPoint);
		opcodeParsers[ineg] = unaryOperationOpcodeParser(_int, negate);
		opcodeParsers[lneg] = unaryOperationOpcodeParser(_long, negate);
		opcodeParsers[fneg] = unaryOperationOpcodeParser(_float, negate);
		opcodeParsers[dneg] = unaryOperationOpcodeParser(_double, negate);
		opcodeParsers[ishl] = integerBitOperationOpcodeParser(_int, shiftLeft);
		opcodeParsers[lshl] = integerBitOperationOpcodeParser(_long, shiftLeft);
		opcodeParsers[ishr] = integerBitOperationOpcodeParser(_int, shiftRight);
		opcodeParsers[lshr] = integerBitOperationOpcodeParser(_long, shiftRight);
		opcodeParsers[iushr] = integerBitOperationOpcodeParser(_int, unsignedShiftRight);
		opcodeParsers[lushr] = integerBitOperationOpcodeParser(_long, unsignedShiftRight);
		opcodeParsers[iand] = integerBitOperationOpcodeParser(_int, and);
		opcodeParsers[land] = integerBitOperationOpcodeParser(_long, and);
		opcodeParsers[ior] = integerBitOperationOpcodeParser(_int, or);
		opcodeParsers[lor] = integerBitOperationOpcodeParser(_long, or);
		opcodeParsers[ixor] = integerBitOperationOpcodeParser(_int, xor);
		opcodeParsers[lxor] = integerBitOperationOpcodeParser(_long, xor);
		opcodeParsers[iinc] = new LocalVariableIntegerIncrementOpcodeParser();

		for (short opcode = 0; opcode < length; opcode++)
		{
			if (opcodeParsers[opcode] == null)
			{
				opcodeParsers[opcode] = new InternalOpcodeParser(opcode);
			}
		}

		return opcodeParsers;
	}

}
