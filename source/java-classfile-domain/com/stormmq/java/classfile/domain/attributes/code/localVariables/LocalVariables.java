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

import com.stormmq.java.classfile.domain.attributes.code.invalidOperandStackExceptions.MismatchedVariableInvalidOperandStackException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Locale.ENGLISH;

public final class LocalVariables
{
	@NotNull private static final Map<Character, LocalVariableAtProgramCounter> EmptyLocalVariablesAtProgramCounter = emptyMap();

	@NotNull private final LocalVariableInformation[] localVariableInformationByLocalVariableIndex;
	@NotNull private final Map<Character, Map<Character, Character>> localVariableLengthsByProgramCounter;

	public LocalVariables(@NotNull final LocalVariableInformation[] localVariableInformationByLocalVariableIndex, @NotNull final Map<Character, Map<Character, Character>> localVariableLengthsByProgramCounter)
	{
		this.localVariableInformationByLocalVariableIndex = localVariableInformationByLocalVariableIndex;
		this.localVariableLengthsByProgramCounter = localVariableLengthsByProgramCounter;
	}

	@NotNull
	public Map<Character, LocalVariableAtProgramCounter> atProgramCounter(final char programCounter, final long codeLength, final char opcodeLength) throws MismatchedVariableInvalidOperandStackException
	{
		final long remainingCodeLength = codeLength - programCounter;
		@Nullable final Map<Character, Character> localVariableLengths = localVariableLengthsByProgramCounter.get(programCounter);
		if (localVariableLengths == null)
		{
			return EmptyLocalVariablesAtProgramCounter;
		}

		final int size = localVariableLengths.size();
		final Map<Character, LocalVariableAtProgramCounter> uniqueLocalVariables = new HashMap<>(size);
		for (final Entry<Character, Character> entry : localVariableLengths.entrySet())
		{
			final char localVariableIndex = entry.getKey();
			final char length = entry.getValue();
			if (length > remainingCodeLength)
			{
				throw new MismatchedVariableInvalidOperandStackException(format(ENGLISH, "The local variable at index '%1$s' has a length '%2$s' which exceeds the remaining code length '%3$s' at program counter '%4$s'", localVariableIndex, length, remainingCodeLength, programCounter));
			}

			// Not sure this check is justified - can length cover multiple opcodes?
			if (length != opcodeLength)
			{
				throw new MismatchedVariableInvalidOperandStackException(format(ENGLISH, "The local variable at index '%1$s' has a length '%2$s' which differs from the opcode length '%3$s' at program counter '%4$s'", localVariableIndex, length, opcodeLength, programCounter));
			}

			for(char offset = 1; offset < length; offset++)
			{
				if (localVariableLengthsByProgramCounter.containsKey((char) (programCounter + offset)))
				{
					throw new MismatchedVariableInvalidOperandStackException(format(ENGLISH, "The local variable at index '%1$s' has a length '%2$s' which at offset '%3$s' from program counter '%4$s' contains a local variable", localVariableIndex, length, offset, programCounter));
				}
			}

			uniqueLocalVariables.put(localVariableIndex, new LocalVariableAtProgramCounter(localVariableInformationByLocalVariableIndex[localVariableIndex], length));
		}
		return uniqueLocalVariables;
	}
}
