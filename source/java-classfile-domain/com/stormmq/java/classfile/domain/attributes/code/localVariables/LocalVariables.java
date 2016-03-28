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

import com.stormmq.java.classfile.domain.descriptors.FieldDescriptor;
import com.stormmq.java.classfile.domain.names.FieldName;
import com.stormmq.java.classfile.domain.signatures.Signature;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;

public final class LocalVariables
{
	@NotNull private static final Set<LocalVariableAtProgramCounter> EmptyLocalVariablesAtProgramCounter = emptySet();

	private final long codeLength;
	private final char maximumLocals;
	@NotNull private final List<DescriptorLocalVariable> descriptorLocalVariables;
	@NotNull private final List<SignatureLocalVariable> signatureLocalVariables;

	public LocalVariables(final long codeLength, final char maximumLocals, @NotNull final List<DescriptorLocalVariable> descriptorLocalVariables, @NotNull final List<SignatureLocalVariable> signatureLocalVariables)
	{
		this.codeLength = codeLength;
		this.maximumLocals = maximumLocals;
		this.descriptorLocalVariables = descriptorLocalVariables;
		this.signatureLocalVariables = signatureLocalVariables;
	}

	@NotNull
	public Set<LocalVariableAtProgramCounter> getForProgramCounter(final char programCounter, final char opcodeLength)
	{
		for (final DescriptorLocalVariable descriptorLocalVariable : descriptorLocalVariables)
		{
			if (descriptorLocalVariable.startProgramCounter == programCounter)
			{
				final FieldName descriptorLocalVariableName = descriptorLocalVariable.localVariableName;
				final char descriptorLength = descriptorLocalVariable.length;
				final char descriptorLocalVariableIndex = descriptorLocalVariable.localVariableIndex;
				final FieldDescriptor localVariableDescriptor = descriptorLocalVariable.localVariableDescriptor;
			}
		}

		for (final SignatureLocalVariable signatureLocalVariable : signatureLocalVariables)
		{
			if (signatureLocalVariable.startProgramCounter == programCounter)
			{
				final FieldName descriptorLocalVariableName = signatureLocalVariable.localVariableName;
				final char descriptorLength = signatureLocalVariable.length;
				final char descriptorLocalVariableIndex = signatureLocalVariable.localVariableIndex;
				final Signature localVariableSignature = signatureLocalVariable.localVariableSignature;
			}
		}

		return EmptyLocalVariablesAtProgramCounter;
	}
}
