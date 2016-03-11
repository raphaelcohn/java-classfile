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
import com.stormmq.java.classfile.domain.attributes.code.stackMapFrames.StackMapFrame;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class Code
{
	private final char maximumStack;
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

	public Code(final char maximumStack, final char maximumLocals, final long codeLength, @NotNull final ByteBuffer code, @NotNull final ExceptionCode[] exceptionCode, final List<LineNumberEntry[]> lineNumberEntries, final List<LocalVariable[]> localVariables, final List<LocalVariableType[]> localVariableTypes, final StackMapFrame[] stackMapFrames, @NotNull final UnknownAttributes unknownAttributes, @NotNull final TypeAnnotation[] visibleTypeAnnotations, @NotNull final TypeAnnotation[] invisibleTypeAnnotations)
	{
		this.maximumStack = maximumStack;
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
	}
}
