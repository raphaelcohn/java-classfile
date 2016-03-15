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

package com.stormmq.java.classfile.parser.javaClassFileParsers.attributesParsers;

import com.stormmq.java.classfile.domain.attributes.code.localVariables.LocalVariableInformation;
import com.stormmq.java.classfile.domain.descriptors.FieldDescriptor;
import com.stormmq.java.classfile.domain.names.FieldName;
import com.stormmq.java.classfile.domain.signatures.Signature;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LocalVariableEntry
{
	private final char startProgramCounter;
	private final char length;
	@NotNull private final FieldName localVariableName;
	@Nullable private final FieldDescriptor localVariableDescriptor;
	@Nullable private final Signature localVariableSignature;
	public final char localVariableIndex;

	public LocalVariableEntry(final char startProgramCounter, final char length, @NotNull final FieldName localVariableName, @Nullable final FieldDescriptor localVariableDescriptor, @Nullable final Signature localVariableSignature, final char localVariableIndex)
	{
		this.startProgramCounter = startProgramCounter;
		this.length = length;
		this.localVariableName = localVariableName;
		this.localVariableDescriptor = localVariableDescriptor;
		this.localVariableSignature = localVariableSignature;
		this.localVariableIndex = localVariableIndex;
	}

	@Override
	public boolean equals(@Nullable final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final LocalVariableEntry that = (LocalVariableEntry) o;

		if (startProgramCounter != that.startProgramCounter)
		{
			return false;
		}
		if (length != that.length)
		{
			return false;
		}
		if (localVariableIndex != that.localVariableIndex)
		{
			return false;
		}
		if (!localVariableName.equals(that.localVariableName))
		{
			return false;
		}
		if (localVariableDescriptor != null ? !localVariableDescriptor.equals(that.localVariableDescriptor) : that.localVariableDescriptor != null)
		{
			return false;
		}
		if (localVariableSignature != null ? !localVariableSignature.equals(that.localVariableSignature) : that.localVariableSignature != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = startProgramCounter;
		result = 31 * result + length;
		result = 31 * result + localVariableName.hashCode();
		result = 31 * result + (localVariableDescriptor != null ? localVariableDescriptor.hashCode() : 0);
		result = 31 * result + (localVariableSignature != null ? localVariableSignature.hashCode() : 0);
		result = 31 * result + localVariableIndex;
		return result;
	}

	public void validateNotAfterEndOfCode(final long codeLength) throws InvalidJavaClassFileException
	{
		if (startProgramCounter >= codeLength)
		{
			throw new InvalidJavaClassFileException("Variable entry exceeds code length");
		}
	}

	public void validateDoesNotHaveALocalVariableIndexWhichIsTooLarge(final char maximumLocals) throws InvalidJavaClassFileException
	{
		if (localVariableIndex >= maximumLocals)
		{
			throw new InvalidJavaClassFileException("Variable entry exceeds maximum locals");
		}
	}

	public void validateLengthMatches(final char length) throws InvalidJavaClassFileException
	{
		if (this.length != length)
		{
			throw new InvalidJavaClassFileException("Differing lengths");
		}
	}

	public char startProgramCounter()
	{
		return startProgramCounter;
	}

	public char localVariableIndex()
	{
		return localVariableIndex;
	}

	@NotNull
	public LocalVariableInformation toLocalVariableInformation()
	{
		return new LocalVariableInformation(localVariableName, localVariableDescriptor, localVariableSignature);
	}

	@NotNull
	public LocalVariableInformation validateIsCompatibleWith(@NotNull final LocalVariableInformation localVariableInformation) throws InvalidJavaClassFileException
	{
		if (localVariableInformation.doesNotHave(localVariableName))
		{
			throw new InvalidJavaClassFileException("Local variable has different name");
		}

		if (localVariableDescriptor != null)
		{
			if (localVariableInformation.doesNotHave(localVariableDescriptor))
			{
				throw new InvalidJavaClassFileException("Local variable has different descriptor");
			}
			return localVariableInformation.with(localVariableDescriptor);
		}

		if (localVariableSignature != null)
		{

			if (localVariableInformation.doesNotHave(localVariableSignature))
			{
				throw new InvalidJavaClassFileException("Local variable has different signature");
			}
			return localVariableInformation.with(localVariableSignature);
		}

		throw new IllegalStateException("Both can not be null");
	}

	public char length()
	{
		return length;
	}
}
