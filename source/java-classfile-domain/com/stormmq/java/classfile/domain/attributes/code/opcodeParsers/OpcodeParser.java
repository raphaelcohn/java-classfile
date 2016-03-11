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

import com.stormmq.java.classfile.domain.attributes.code.operandStack.*;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

import static com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.InvokeDynamicNotAllowedOpcodeParser.InvokeDynamicNotAllowed;

public interface OpcodeParser
{
	int ByteMask = 0xFF;

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

	short invokedynamic = 186;

	short breakpoint = 202;

	short impdep1 = 254;

	short impdep2 = 255;

	@NotNull OpcodeParser[] Java7AndLaterOpcodeParsers = initialiseOpcodeParsers(true);

	@NotNull OpcodeParser[] Java6AndEarlierOpcodeParser = initialiseOpcodeParsers(false);

	@NotNull
	static OpcodeParser[] initialiseOpcodeParsers(final boolean opcode186IsPermittedBecauseThisIsForJava7OrLater)
	{
		final short length = 256;

		final OpcodeParser[] opcodeParsers = new OpcodeParser[length];

		for(short opcode = 0; opcode < length; opcode ++)
		{
			opcodeParsers[opcode] = new InternalOpcodeParser(opcode);
		}

		opcodeParsers[breakpoint] = new ReservedOpcodeParser(breakpoint, "breakpoint");
		opcodeParsers[impdep1] = new ReservedOpcodeParser(impdep1, "impdep1");
		opcodeParsers[impdep2] = new ReservedOpcodeParser(impdep2, "impdep2");

		opcodeParsers[invokedynamic] = opcode186IsPermittedBecauseThisIsForJava7OrLater ? code1 ->
		{
			throw new UnsupportedOperationException("Implement me!");
		} : InvokeDynamicNotAllowed;


		opcodeParsers[nop] = operandStack -> {};

		opcodeParsers[iconst_0] = new IntegerConstantOpcodeParser(0);
		opcodeParsers[iconst_1] = new IntegerConstantOpcodeParser(1);
		opcodeParsers[iconst_2] = new IntegerConstantOpcodeParser(2);
		opcodeParsers[iconst_3] = new IntegerConstantOpcodeParser(3);
		opcodeParsers[iconst_4] = new IntegerConstantOpcodeParser(4);
		opcodeParsers[iconst_5] = new IntegerConstantOpcodeParser(5);
		opcodeParsers[lconst_0] = new LongConstantOpcodeParser(0);
		opcodeParsers[lconst_1] = new LongConstantOpcodeParser(1);


		return opcodeParsers;
	}

	@SuppressWarnings("NumericCastThatLosesPrecision")
	static short unsigned8BitInteger(@NotNull final ByteBuffer code)
	{
		return (short) (code.get() & ByteMask);
	}

	void parse(@NotNull final OperandStack operandStack) throws InvalidOpcodeException, StackOverflowException;

}
