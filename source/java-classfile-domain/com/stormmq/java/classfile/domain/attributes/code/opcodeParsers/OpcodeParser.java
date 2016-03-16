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
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.OperandStackItem;
import com.stormmq.java.classfile.domain.attributes.code.typing.ByteCharOrShort;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.ArrayLoadReferenceOpcodeParser.ArrayLoadReference;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.ArrayStoreReferenceOpcodeParser.ArrayStoreReference;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.BinaryOperationOpcodeParser.binaryOperationOpcodeParser;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.BytePushOpcodeParser.BytePush;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.DuplicateOpcodeParser.Duplicate;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.IntegerBitOperationOpcodeParser.integerBitOperationOpcodeParser;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.InvokeDynamicNotAllowedOpcodeParser.InvokeDynamicNotAllowed;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.LoadDoubleWidthConstantOpcodeParser.LoadWideDoubleWidthConstant;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.LoadSingleWidthConstantOpcodeParser.LoadNarrowSingleWidthConstant;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.LoadSingleWidthConstantOpcodeParser.LoadWideSingleWidthConstant;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.LocalVariableIntegerIncrementOpcodeParser.LocalVariableIntegerIncrement;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.NarrowLoadReferenceVariableOpcodeParser.NarrowLoadReferenceVariable;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.NarrowStoreReferenceVariableOpcodeParser.NarrowStoreReferenceVariable;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.NoOperationOpcodeParser.NoOperation;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.Pop2OpcodeParser.Pop2;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.PopOpcodeParser.Pop;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.ShortPushOpcodeParser.ShortPush;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.SwapOpcodeParser.Swap;
import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.UnaryOperationOpcodeParser.unaryOperationOpcodeParser;
import static com.stormmq.java.classfile.domain.attributes.code.operandStackItems.constantOperandStackItems.NullReferenceConstantOperandStackItem.NullConstant;
import static com.stormmq.java.classfile.domain.attributes.code.operandStackItems.operations.BinaryOperation.*;
import static com.stormmq.java.classfile.domain.attributes.code.operandStackItems.operations.Comparison.*;
import static com.stormmq.java.classfile.domain.attributes.code.operandStackItems.operations.IntegerBitOperation.*;
import static com.stormmq.java.classfile.domain.attributes.code.operandStackItems.operations.UnaryOperation.negate;
import static com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory._double;
import static com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory._float;
import static com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory._int;
import static com.stormmq.java.classfile.domain.attributes.code.typing.ComputationalCategory._long;

public interface OpcodeParser
{
	char length() throws InvalidOpcodeException;

	void parse(@NotNull final OperandStack operandStack, @NotNull final CodeReader codeReader, @NotNull final Set<Character> lineNumbers, @NotNull final Map<Character, LocalVariableAtProgramCounter> localVariablesAtProgramCounter, @NotNull final RuntimeConstantPool runtimeConstantPool) throws InvalidOpcodeException, UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException, OverflowInvalidOperandStackException, NotEnoughBytesInvalidOperandStackException;

	// Constants

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


	// Loads

	short iload = 21;

	short lload = 22;

	short fload = 23;

	short dload = 24;

	short aload = 25;

	short iload_0 = 26;

	short iload_1 = 27;

	short iload_2 = 28;

	short iload_3 = 29;

	short lload_0 = 30;

	short lload_1 = 31;

	short lload_2 = 32;

	short lload_3 = 33;

	short fload_0 = 34;

	short fload_1 = 35;

	short fload_2 = 36;

	short fload_3 = 37;

	short dload_0 = 38;

	short dload_1 = 39;

	short dload_2 = 40;

	short dload_3 = 41;

	short aload_0 = 42;

	short aload_1 = 43;

	short aload_2 = 44;

	short aload_3 = 45;

	short iaload = 46;

	short laload = 47;

	short faload = 48;

	short daload = 49;

	short aaload = 50;

	short baload = 51;

	short caload = 52;

	short saload = 53;


	// Stores
	short istore = 54;

	short lstore = 55;

	short fstore = 56;

	short dstore = 57;

	short astore = 58;

	short istore_0 = 59;

	short istore_1 = 60;

	short istore_2 = 61;

	short istore_3 = 62;

	short lstore_0 = 63;

	short lstore_1 = 64;

	short lstore_2 = 65;

	short lstore_3 = 66;

	short fstore_0 = 67;

	short fstore_1 = 68;

	short fstore_2 = 69;

	short fstore_3 = 70;

	short dstore_0 = 71;

	short dstore_1 = 72;

	short dstore_2 = 73;

	short dstore_3 = 74;

	short astore_0 = 75;

	short astore_1 = 76;

	short astore_2 = 77;

	short astore_3 = 78;

	short iastore = 79;

	short lastore = 80;

	short fastore = 81;

	short dastore = 82;

	short aastore = 83;

	short bastore = 84;

	short castore = 85;

	short sastore = 86;


	// Stack

	short pop = 87;

	short pop2 = 88;

	short dup = 89;

	short dup_x1 = 90;

	short dup_x2 = 91;

	short dup2 = 92;

	short dup2_x1 = 93;

	short dup2_x2 = 94;

	short swap = 95;


	// Math

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


	// Conversions

	short i2l = 133;

	short i2f = 134;

	short i2d = 135;

	short l2i = 136;

	short l2f = 137;

	short l2d = 138;

	short f2i = 139;

	short f2l = 140;

	short f2d = 141;

	short d2i = 142;

	short d2l = 143;

	short d2f = 144;

	short i2b = 145;

	short i2c = 146;

	short i2s = 147;


	// Comparisons

	short lcmp = 148;

	short fcmpl = 149;

	short fcmpg = 150;

	short dcmpl = 151;

	short dcmpg = 152;

	short ifeq = 153;

	short ifne = 154;

	short iflt = 155;

	short ifge = 156;

	short ifgt = 157;

	short ifle = 158;

	short if_icmpeq = 159;

	short if_icmpne = 160;

	short if_icmplt = 161;

	short if_icmpge = 162;

	short if_icmpgt = 163;

	short if_icmple = 164;

	short if_acmpeq = 165;

	short if_acmpne = 166;


	// Control

	short _goto = 167;

	short jsr = 168;

	short ret = 169;

	short tableswitch = 170;

	short lookupswitch = 171;

	short ireturn = 172;

	short lreturn = 173;

	short freturn = 174;

	short dreturn = 175;

	short areturn = 176;

	short _return = 177;


	// References

	short getstatic = 178;

	short putstatic = 179;

	short getfield = 180;

	short putfield = 181;

	short invokevirtual = 182;

	short invokespecial = 183;

	short invokestatic = 184;

	short invokeinterface = 185;

	short invokedynamic = 186;

	short _new = 187;

	short newarray = 188;

	short anewarray = 189;

	short arraylength = 190;

	short athrow = 191;

	short checkcast = 192;

	short _instanceof = 193;

	short monitorenter = 194;

	short monitorexit = 195;


	// Extended

	short wide = 196;

	short multianewarray = 197;

	short ifnull = 198;

	short ifnonnull = 199;

	short goto_w = 200;

	short jsr_w = 201;


	// Reserved

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

		opcodeParsers[invokedynamic] = opcode186IsPermittedBecauseThisIsForJava7OrLater ? InvokeDynamicOpcodeParser.InvokeDynamicAllowed : InvokeDynamicNotAllowed;

		constants(opcodeParsers);
		loads(opcodeParsers);
		store(opcodeParsers);
		stack(opcodeParsers);
		math(opcodeParsers, isStrictFloatingPoint);
		conversions(opcodeParsers);
		comparisons(opcodeParsers);
		control(opcodeParsers);
		references(opcodeParsers);
		extended(opcodeParsers);
		reserved(opcodeParsers);
		internal(opcodeParsers, length);

		return opcodeParsers;
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	static void constants(@NotNull final OpcodeParser[] opcodeParsers)
	{
		opcodeParsers[nop] = NoOperation;
		opcodeParsers[aconst_null] = new PushConstantOpcodeParser(NullConstant);
		final Integer number = -1;
		opcodeParsers[iconst_m1] = new PushConstantOpcodeParser(_int, number);
		opcodeParsers[iconst_0] = new PushConstantOpcodeParser(_int, 0);
		opcodeParsers[iconst_1] = new PushConstantOpcodeParser(_int, 1);
		opcodeParsers[iconst_2] = new PushConstantOpcodeParser(_int, 2);
		opcodeParsers[iconst_3] = new PushConstantOpcodeParser(_int, 3);
		opcodeParsers[iconst_4] = new PushConstantOpcodeParser(_int, 4);
		opcodeParsers[iconst_5] = new PushConstantOpcodeParser(_int, 5);
		opcodeParsers[lconst_0] = new PushConstantOpcodeParser(_long, 0L);
		opcodeParsers[lconst_1] = new PushConstantOpcodeParser(_long, 1L);
		opcodeParsers[fconst_0] = new PushConstantOpcodeParser(_float, 0.0f);
		opcodeParsers[fconst_1] = new PushConstantOpcodeParser(_float, 1.0f);
		opcodeParsers[fconst_2] = new PushConstantOpcodeParser(_float, 2.0f);
		opcodeParsers[dconst_0] = new PushConstantOpcodeParser(_double, 0.0d);
		opcodeParsers[dconst_1] = new PushConstantOpcodeParser(_double, 1.0d);
		opcodeParsers[bipush] = BytePush;
		opcodeParsers[sipush] = ShortPush;
		opcodeParsers[ldc] = LoadNarrowSingleWidthConstant;
		opcodeParsers[ldc_w] = LoadWideSingleWidthConstant;
		opcodeParsers[ldc2_w] = LoadWideDoubleWidthConstant;
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	static void loads(@NotNull final OpcodeParser[] opcodeParsers)
	{
		opcodeParsers[iload] = new NarrowLoadNumericVariableOpcodeParser(_int);
		opcodeParsers[lload] = new NarrowLoadNumericVariableOpcodeParser(_long);
		opcodeParsers[fload] = new NarrowLoadNumericVariableOpcodeParser(_float);
		opcodeParsers[dload] = new NarrowLoadNumericVariableOpcodeParser(_double);
		opcodeParsers[aload] = NarrowLoadReferenceVariable;
		opcodeParsers[iload_0] = new NarrowFixedLoadNumericVariableOpcodeParser(_int, (char) 0);
		opcodeParsers[iload_1] = new NarrowFixedLoadNumericVariableOpcodeParser(_int, (char) 1);
		opcodeParsers[iload_2] = new NarrowFixedLoadNumericVariableOpcodeParser(_int, (char) 2);
		opcodeParsers[iload_3] = new NarrowFixedLoadNumericVariableOpcodeParser(_int, (char) 3);
		opcodeParsers[lload_0] = new NarrowFixedLoadNumericVariableOpcodeParser(_long, (char) 0);
		opcodeParsers[lload_1] = new NarrowFixedLoadNumericVariableOpcodeParser(_long, (char) 1);
		opcodeParsers[lload_2] = new NarrowFixedLoadNumericVariableOpcodeParser(_long, (char) 2);
		opcodeParsers[lload_3] = new NarrowFixedLoadNumericVariableOpcodeParser(_long, (char) 3);
		opcodeParsers[fload_0] = new NarrowFixedLoadNumericVariableOpcodeParser(_float, (char) 0);
		opcodeParsers[fload_1] = new NarrowFixedLoadNumericVariableOpcodeParser(_float, (char) 1);
		opcodeParsers[fload_2] = new NarrowFixedLoadNumericVariableOpcodeParser(_float, (char) 2);
		opcodeParsers[fload_3] = new NarrowFixedLoadNumericVariableOpcodeParser(_float, (char) 3);
		opcodeParsers[dload_0] = new NarrowFixedLoadNumericVariableOpcodeParser(_double, (char) 0);
		opcodeParsers[dload_1] = new NarrowFixedLoadNumericVariableOpcodeParser(_double, (char) 1);
		opcodeParsers[dload_2] = new NarrowFixedLoadNumericVariableOpcodeParser(_double, (char) 2);
		opcodeParsers[dload_3] = new NarrowFixedLoadNumericVariableOpcodeParser(_double, (char) 3);
		opcodeParsers[aload_0] = new NarrowFixedLoadReferenceVariableOpcodeParser((char) 0);
		opcodeParsers[aload_1] = new NarrowFixedLoadReferenceVariableOpcodeParser((char) 1);
		opcodeParsers[aload_2] = new NarrowFixedLoadReferenceVariableOpcodeParser((char) 2);
		opcodeParsers[aload_3] = new NarrowFixedLoadReferenceVariableOpcodeParser((char) 3);
		opcodeParsers[iaload] = new ArrayLoadNumericOpcodeParser(_int);
		opcodeParsers[laload] = new ArrayLoadNumericOpcodeParser(_long);
		opcodeParsers[faload] = new ArrayLoadNumericOpcodeParser(_float);
		opcodeParsers[daload] = new ArrayLoadNumericOpcodeParser(_double);
		opcodeParsers[aaload] = ArrayLoadReference;
		opcodeParsers[baload] = new ArrayByteShortOrCharLoadNumericOpcodeParser(ByteCharOrShort._byte); // also used for boolean arrays
		opcodeParsers[caload] = new ArrayByteShortOrCharLoadNumericOpcodeParser(ByteCharOrShort._char);
		opcodeParsers[saload] = new ArrayByteShortOrCharLoadNumericOpcodeParser(ByteCharOrShort._char);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	static void store(@NotNull final OpcodeParser[] opcodeParsers)
	{
		opcodeParsers[istore] = new NarrowStoreNumericVariableOpcodeParser(_int);
		opcodeParsers[lstore] = new NarrowStoreNumericVariableOpcodeParser(_long);
		opcodeParsers[fstore] = new NarrowStoreNumericVariableOpcodeParser(_float);
		opcodeParsers[dstore] = new NarrowStoreNumericVariableOpcodeParser(_double);
		opcodeParsers[astore] = NarrowStoreReferenceVariable;
		opcodeParsers[istore_0] = new NarrowFixedStoreNumericVariableOpcodeParser(_int, (char) 0);
		opcodeParsers[istore_0] = new NarrowFixedStoreNumericVariableOpcodeParser(_int, (char) 0);
		opcodeParsers[istore_1] = new NarrowFixedStoreNumericVariableOpcodeParser(_int, (char) 1);
		opcodeParsers[istore_2] = new NarrowFixedStoreNumericVariableOpcodeParser(_int, (char) 2);
		opcodeParsers[istore_3] = new NarrowFixedStoreNumericVariableOpcodeParser(_int, (char) 3);
		opcodeParsers[lstore_0] = new NarrowFixedStoreNumericVariableOpcodeParser(_long, (char) 0);
		opcodeParsers[lstore_1] = new NarrowFixedStoreNumericVariableOpcodeParser(_long, (char) 1);
		opcodeParsers[lstore_2] = new NarrowFixedStoreNumericVariableOpcodeParser(_long, (char) 2);
		opcodeParsers[lstore_3] = new NarrowFixedStoreNumericVariableOpcodeParser(_long, (char) 3);
		opcodeParsers[fstore_0] = new NarrowFixedStoreNumericVariableOpcodeParser(_float, (char) 0);
		opcodeParsers[fstore_1] = new NarrowFixedStoreNumericVariableOpcodeParser(_float, (char) 1);
		opcodeParsers[fstore_2] = new NarrowFixedStoreNumericVariableOpcodeParser(_float, (char) 2);
		opcodeParsers[fstore_3] = new NarrowFixedStoreNumericVariableOpcodeParser(_float, (char) 3);
		opcodeParsers[dstore_0] = new NarrowFixedStoreNumericVariableOpcodeParser(_double, (char) 0);
		opcodeParsers[dstore_1] = new NarrowFixedStoreNumericVariableOpcodeParser(_double, (char) 1);
		opcodeParsers[dstore_2] = new NarrowFixedStoreNumericVariableOpcodeParser(_double, (char) 2);
		opcodeParsers[dstore_3] = new NarrowFixedStoreNumericVariableOpcodeParser(_double, (char) 3);
		opcodeParsers[astore_0] = new NarrowFixedStoreReferenceVariableOpcodeParser((char) 0);
		opcodeParsers[astore_1] = new NarrowFixedStoreReferenceVariableOpcodeParser((char) 1);
		opcodeParsers[astore_2] = new NarrowFixedStoreReferenceVariableOpcodeParser((char) 2);
		opcodeParsers[astore_3] = new NarrowFixedStoreReferenceVariableOpcodeParser((char) 3);
		opcodeParsers[iastore] = new ArrayStoreNumericOpcodeParser(_int);
		opcodeParsers[lastore] = new ArrayStoreNumericOpcodeParser(_long);
		opcodeParsers[fastore] = new ArrayStoreNumericOpcodeParser(_float);
		opcodeParsers[dastore] = new ArrayStoreNumericOpcodeParser(_double);
		opcodeParsers[aastore] = ArrayStoreReference;
		opcodeParsers[bastore] = new ArrayByteShortOrCharStoreNumericOpcodeParser(ByteCharOrShort._byte); // also used for boolean arrays
		opcodeParsers[castore] = new ArrayByteShortOrCharStoreNumericOpcodeParser(ByteCharOrShort._char);
		opcodeParsers[sastore] = new ArrayByteShortOrCharStoreNumericOpcodeParser(ByteCharOrShort._char);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	static void stack(@NotNull final OpcodeParser[] opcodeParsers)
	{
		opcodeParsers[pop] = Pop;
		opcodeParsers[pop2] = Pop2;
		opcodeParsers[dup] = Duplicate;
		opcodeParsers[dup_x1] = new AbstractOneOpcodeParser()
		{
			@Override
			public void parse(@NotNull final OperandStack operandStack, @NotNull final CodeReader codeReader, @NotNull final Set<Character> lineNumbers, @NotNull final Map<Character, LocalVariableAtProgramCounter> localVariablesAtProgramCounter, @NotNull final RuntimeConstantPool runtimeConstantPool) throws InvalidOpcodeException, UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException, OverflowInvalidOperandStackException, NotEnoughBytesInvalidOperandStackException
			{
				final OperandStackItem value1 = operandStack.popCategory1ComputationalType();
				final OperandStackItem value2 = operandStack.popCategory1ComputationalType();
				operandStack.pushWithCertainty(value1);
				operandStack.pushWithCertainty(value2);
				operandStack.push(value1);
			}
		};
		opcodeParsers[dup_x2] = new AbstractOneOpcodeParser()
		{
			@Override
			public void parse(@NotNull final OperandStack operandStack, @NotNull final CodeReader codeReader, @NotNull final Set<Character> lineNumbers, @NotNull final Map<Character, LocalVariableAtProgramCounter> localVariablesAtProgramCounter, @NotNull final RuntimeConstantPool runtimeConstantPool) throws InvalidOpcodeException, UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException, OverflowInvalidOperandStackException, NotEnoughBytesInvalidOperandStackException
			{
				final OperandStackItem value1 = operandStack.popCategory1ComputationalType();
				final OperandStackItem value2 = operandStack.pop();
				if (value2.isCategory1())
				{
					final OperandStackItem value3 = operandStack.popCategory1ComputationalType();
					operandStack.pushWithCertainty(value1);
					operandStack.pushWithCertainty(value3);
					operandStack.pushWithCertainty(value2);
					operandStack.push(value1);
				}
				else
				{
					operandStack.pushWithCertainty(value1);
					operandStack.pushWithCertainty(value2);
					operandStack.push(value1);
				}
			}
		};
		opcodeParsers[dup2] = new AbstractOneOpcodeParser()
		{
			@Override
			public void parse(@NotNull final OperandStack operandStack, @NotNull final CodeReader codeReader, @NotNull final Set<Character> lineNumbers, @NotNull final Map<Character, LocalVariableAtProgramCounter> localVariablesAtProgramCounter, @NotNull final RuntimeConstantPool runtimeConstantPool) throws InvalidOpcodeException, UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException, OverflowInvalidOperandStackException, NotEnoughBytesInvalidOperandStackException
			{
				final OperandStackItem value = operandStack.pop();
				if (value.isCategory1())
				{
					final OperandStackItem value1 = value;
					final OperandStackItem value2 = operandStack.popCategory1ComputationalType();
					operandStack.pushWithCertainty(value2);
					operandStack.pushWithCertainty(value1);
					operandStack.push(value2);
					operandStack.push(value1);
				}
				else
				{
					operandStack.pushWithCertainty(value);
					operandStack.push(value);
				}
			}
		};
		opcodeParsers[dup2_x1] = new AbstractOneOpcodeParser()
		{
			@Override
			public void parse(@NotNull final OperandStack operandStack, @NotNull final CodeReader codeReader, @NotNull final Set<Character> lineNumbers, @NotNull final Map<Character, LocalVariableAtProgramCounter> localVariablesAtProgramCounter, @NotNull final RuntimeConstantPool runtimeConstantPool) throws InvalidOpcodeException, UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException, OverflowInvalidOperandStackException, NotEnoughBytesInvalidOperandStackException
			{
				final OperandStackItem value1 = operandStack.pop();
				if (value1.isCategory1())
				{
					final OperandStackItem value2 = operandStack.popCategory1ComputationalType();
					final OperandStackItem value3 = operandStack.popCategory1ComputationalType();
					operandStack.pushWithCertainty(value2);
					operandStack.pushWithCertainty(value1);
					operandStack.pushWithCertainty(value3);
					operandStack.push(value2);
					operandStack.push(value1);
				}
				else
				{
					final OperandStackItem value2 = operandStack.popCategory2ComputationalType();
					operandStack.pushWithCertainty(value1);
					operandStack.pushWithCertainty(value2);
					operandStack.push(value1);
				}
			}
		};
		opcodeParsers[dup_x2] = new AbstractOneOpcodeParser()
		{
			@Override
			public void parse(@NotNull final OperandStack operandStack, @NotNull final CodeReader codeReader, @NotNull final Set<Character> lineNumbers, @NotNull final Map<Character, LocalVariableAtProgramCounter> localVariablesAtProgramCounter, @NotNull final RuntimeConstantPool runtimeConstantPool) throws InvalidOpcodeException, UnderflowInvalidOperandStackException, MismatchedTypeInvalidOperandStackException, OverflowInvalidOperandStackException, NotEnoughBytesInvalidOperandStackException
			{
				final OperandStackItem value1 = operandStack.pop();
				if (value1.isCategory1())
				{
					final OperandStackItem value2 = operandStack.pop();
					if (value2.isCategory1())
					{
						final OperandStackItem value3 = operandStack.pop();
						// "Form 1"
						if (value3.isCategory1())
						{
							final OperandStackItem value4 = operandStack.popCategory1ComputationalType();
							operandStack.pushWithCertainty(value2);
							operandStack.pushWithCertainty(value1);
							operandStack.pushWithCertainty(value4);
							operandStack.pushWithCertainty(value3);
							operandStack.push(value2);
							operandStack.push(value1);
						}
						// "Form 3"
						else
						{
							operandStack.pushWithCertainty(value2);
							operandStack.pushWithCertainty(value1);
							operandStack.pushWithCertainty(value3);
							operandStack.push(value2);
							operandStack.push(value1);
						}
					}
					else
					{
						throw new InvalidOpcodeException("dup_x2 has category 1 value1 but category 2 value2");
					}
				}
				else
				{
					final OperandStackItem value2 = operandStack.pop();
					// "Form 2"
					if (value2.isCategory1())
					{
						final OperandStackItem value3 = operandStack.popCategory1ComputationalType();
						operandStack.pushWithCertainty(value1);
						operandStack.pushWithCertainty(value3);
						operandStack.pushWithCertainty(value2);
						operandStack.push(value1);
					}
					// "Form 4"
					else
					{
						operandStack.pushWithCertainty(value1);
						operandStack.pushWithCertainty(value2);
						operandStack.push(value1);
					}
				}
			}
		};

		opcodeParsers[swap] = Swap;
	}

	static void math(@NotNull final OpcodeParser[] opcodeParsers, final boolean isStrictFloatingPoint)
	{
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
		opcodeParsers[iinc] = LocalVariableIntegerIncrement;
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	static void conversions(@NotNull final OpcodeParser[] opcodeParsers)
	{
		opcodeParsers[i2l] = new NumericConversionOpcodeParser(_int, _long);
		opcodeParsers[i2f] = new NumericConversionOpcodeParser(_int, _float);
		opcodeParsers[i2d] = new NumericConversionOpcodeParser(_int, _double);
		opcodeParsers[l2i] = new NumericConversionOpcodeParser(_long, _int);
		opcodeParsers[l2f] = new NumericConversionOpcodeParser(_long, _float);
		opcodeParsers[l2d] = new NumericConversionOpcodeParser(_long, _double);
		opcodeParsers[f2i] = new NumericConversionOpcodeParser(_float, _int);
		opcodeParsers[f2l] = new NumericConversionOpcodeParser(_float, _long);
		opcodeParsers[f2d] = new NumericConversionOpcodeParser(_float, _double);
		opcodeParsers[d2i] = new NumericConversionOpcodeParser(_double, _int);
		opcodeParsers[d2l] = new NumericConversionOpcodeParser(_double, _long);
		opcodeParsers[d2f] = new NumericConversionOpcodeParser(_double, _float);
		opcodeParsers[i2b] = new DowncastNumericConversionOpcodeParser(ByteCharOrShort._byte);
		opcodeParsers[i2c] = new DowncastNumericConversionOpcodeParser(ByteCharOrShort._char);
		opcodeParsers[i2s] = new DowncastNumericConversionOpcodeParser(ByteCharOrShort._short);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	static void comparisons(@NotNull final OpcodeParser[] opcodeParsers)
	{
		opcodeParsers[lcmp] = new LongComparisonToIntegerOpcodeParser();
		opcodeParsers[fcmpl] = new FloatComparisonToIntegerOpcodeParser(-1);
		opcodeParsers[fcmpg] = new FloatComparisonToIntegerOpcodeParser(1);
		opcodeParsers[dcmpl] = new DoubleComparisonToIntegerOpcodeParser(-1);
		opcodeParsers[dcmpg] = new DoubleComparisonToIntegerOpcodeParser(1);
		opcodeParsers[ifeq] = new IntegerIfZeroOpcodeParser(equal);
		opcodeParsers[ifne] = new IntegerIfZeroOpcodeParser(notEqual);
		opcodeParsers[iflt] = new IntegerIfZeroOpcodeParser(lessThan);
		opcodeParsers[ifge] = new IntegerIfZeroOpcodeParser(greaterThanOrEqual);
		opcodeParsers[ifgt] = new IntegerIfZeroOpcodeParser(greaterThan);
		opcodeParsers[ifle] = new IntegerIfZeroOpcodeParser(lessThanOrEqual);
		opcodeParsers[if_icmpeq] = new IntegerIfOpcodeParser(equal);
		opcodeParsers[if_icmpne] = new IntegerIfOpcodeParser(notEqual);
		opcodeParsers[if_icmplt] = new IntegerIfOpcodeParser(lessThan);
		opcodeParsers[if_icmpge] = new IntegerIfOpcodeParser(greaterThanOrEqual);
		opcodeParsers[if_icmpgt] = new IntegerIfOpcodeParser(greaterThan);
		opcodeParsers[if_icmple] = new IntegerIfOpcodeParser(lessThanOrEqual);
		opcodeParsers[if_acmpeq] = new ReferenceIfOpcodeParser(true);
		opcodeParsers[if_acmpne] = new ReferenceIfOpcodeParser(false);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	static void control(@NotNull final OpcodeParser[] opcodeParsers)
	{
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	static void references(@NotNull final OpcodeParser[] opcodeParsers)
	{
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	static void extended(@NotNull final OpcodeParser[] opcodeParsers)
	{


		opcodeParsers[ifnull] = new IfNullOpcodeParser(true);
		opcodeParsers[ifnonnull] = new IfNullOpcodeParser(true);


	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	static void reserved(@NotNull final OpcodeParser[] opcodeParsers)
	{
		opcodeParsers[breakpoint] = new ReservedOpcodeParser(breakpoint, "breakpoint");
		opcodeParsers[impdep1] = new ReservedOpcodeParser(impdep1, "impdep1");
		opcodeParsers[impdep2] = new ReservedOpcodeParser(impdep2, "impdep2");
	}

	static void internal(@NotNull final OpcodeParser[] opcodeParsers, final short length)
	{
		for (short opcode = 0; opcode < length; opcode++)
		{
			if (opcodeParsers[opcode] == null)
			{
				opcodeParsers[opcode] = new InternalOpcodeParser(opcode);
			}
		}
	}

}
