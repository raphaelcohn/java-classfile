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

package com.stormmq.java.classfile.domain.information;

import com.stormmq.java.classfile.domain.attributes.UnknownAttributes;
import com.stormmq.java.classfile.domain.attributes.annotations.AnnotationValue;
import com.stormmq.java.classfile.domain.attributes.annotations.TypeAnnotation;
import com.stormmq.java.classfile.domain.attributes.code.Code;
import com.stormmq.java.classfile.domain.attributes.code.invalidOperandStackExceptions.*;
import com.stormmq.java.classfile.domain.attributes.code.opcodeParsers.InvalidOpcodeException;
import com.stormmq.java.classfile.domain.attributes.method.MethodParameter;
import com.stormmq.java.classfile.domain.signatures.Signature;
import com.stormmq.java.classfile.domain.uniqueness.MethodUniqueness;
import com.stormmq.java.parsing.utilities.Completeness;
import com.stormmq.java.parsing.utilities.Visibility;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class MethodInformation
{
	@NotNull private final MethodUniqueness methodUniqueness;
	@NotNull private final Visibility visibility;
	private final boolean isSynthetic;
	private final boolean isBridge;
	private final boolean isVarArgs;
	@NotNull private final Completeness completeness;
	private final boolean isSynchronized;
	private final boolean isNative;
	private final boolean isStatic;
	private final boolean isStrictFloatingPoint;
	private final boolean isSyntheticAttribute;
	private final boolean isDeprecated;
	@Nullable private final Signature signature;
	@NotNull private final AnnotationValue[] visibleAnnotations;
	@NotNull private final AnnotationValue[] invisibleAnnotations;
	@NotNull private final AnnotationValue[][] visibleParameterAnnotations;
	@NotNull private final AnnotationValue[][] invisibleParameterAnnotations;
	@NotNull private final TypeAnnotation[] visibleTypeAnnotations;
	@NotNull private final TypeAnnotation[] invisibleTypeAnnotations;
	@NotNull private final Set<KnownReferenceTypeName> exceptions;
	@NotNull private final MethodParameter[] methodParameters;
	@Nullable private final Object annotationDefault;
	@NotNull private final UnknownAttributes unknownAttributes;
	@Nullable private final Code code;

	public MethodInformation(@NotNull final MethodUniqueness methodUniqueness, @NotNull final Visibility visibility, final boolean isSynthetic, final boolean isBridge, final boolean isVarArgs, @NotNull final Completeness completeness, final boolean isSynchronized, final boolean isNative, final boolean isStatic, final boolean isStrictFloatingPoint, final boolean isSyntheticAttribute, final boolean isDeprecated, @Nullable final Signature signature, @NotNull final AnnotationValue[] visibleAnnotations, @NotNull final AnnotationValue[] invisibleAnnotations, @NotNull final AnnotationValue[][] visibleParameterAnnotations, @NotNull final AnnotationValue[][] invisibleParameterAnnotations, @NotNull final TypeAnnotation[] visibleTypeAnnotations, @NotNull final TypeAnnotation[] invisibleTypeAnnotations, @NotNull final Set<KnownReferenceTypeName> exceptions, @NotNull final MethodParameter[] methodParameters, @Nullable final Code code, @Nullable final Object annotationDefault, @NotNull final UnknownAttributes unknownAttributes)
	{
		this.methodUniqueness = methodUniqueness;
		this.code = code;
		this.visibility = visibility;
		this.isSynthetic = isSynthetic;
		this.isBridge = isBridge;
		this.isVarArgs = isVarArgs;
		this.completeness = completeness;
		this.isSynchronized = isSynchronized;
		this.isNative = isNative;
		this.isStatic = isStatic;
		this.isStrictFloatingPoint = isStrictFloatingPoint;
		this.isSyntheticAttribute = isSyntheticAttribute;
		this.isDeprecated = isDeprecated;
		this.signature = signature;
		this.visibleAnnotations = visibleAnnotations;
		this.invisibleAnnotations = invisibleAnnotations;
		this.visibleParameterAnnotations = visibleParameterAnnotations;
		this.invisibleParameterAnnotations = invisibleParameterAnnotations;
		this.visibleTypeAnnotations = visibleTypeAnnotations;
		this.invisibleTypeAnnotations = invisibleTypeAnnotations;
		this.exceptions = exceptions;
		this.methodParameters = methodParameters;
		this.annotationDefault = annotationDefault;
		this.unknownAttributes = unknownAttributes;
	}

	public void parseCode() throws MismatchedTypeInvalidOperandStackException, NotEnoughBytesInvalidOperandStackException, InvalidOpcodeException, UnderflowInvalidOperandStackException, OverflowInvalidOperandStackException
	{
		if (code == null)
		{
			return;
		}
		code.parseCode(isStrictFloatingPoint);
	}
}
