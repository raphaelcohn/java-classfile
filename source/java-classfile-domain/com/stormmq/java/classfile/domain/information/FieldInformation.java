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

import com.stormmq.java.classfile.domain.attributes.annotations.*;
import com.stormmq.java.classfile.domain.fieldConstants.FieldConstant;
import com.stormmq.java.classfile.domain.signatures.Signature;
import com.stormmq.java.classfile.domain.uniqueness.FieldUniqueness;
import com.stormmq.java.parsing.utilities.FieldFinality;
import com.stormmq.java.parsing.utilities.Visibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

import static com.stormmq.java.parsing.utilities.Visibility.Private;
import static com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName.knownReferenceTypeName;

public final class FieldInformation
{
	@NotNull public final FieldUniqueness fieldUniqueness;
	public final boolean isSynthetic;
	@NotNull public final Visibility fieldVisibility;
	@NotNull public final FieldFinality fieldFinality;
	public final boolean isTransient;
	public final boolean isFinal;
	public final boolean isStatic;
	public final boolean isDeprecated;
	public final boolean isSyntheticAttribute;
	@Nullable public final Signature signature;
	@Nullable public final FieldConstant constantValue;
	@NotNull public final AnnotationValues runtimeAnnotationValues;
	@NotNull public final TypeAnnotation[] visibleTypeAnnotations;
	@NotNull public final TypeAnnotation[] invisibleTypeAnnotations;

	public FieldInformation(@NotNull final FieldUniqueness fieldUniqueness, final boolean isSynthetic, @NotNull final Visibility fieldVisibility, @NotNull final FieldFinality fieldFinality, final boolean isTransient, final boolean isFinal, final boolean isStatic, final boolean isDeprecated, final boolean isSyntheticAttribute, @Nullable final Signature signature, @Nullable final FieldConstant constantValue, @NotNull final AnnotationValues runtimeAnnotationValues, @NotNull final TypeAnnotation[] visibleTypeAnnotations, @NotNull final TypeAnnotation[] invisibleTypeAnnotations)
	{
		this.fieldUniqueness = fieldUniqueness;
		this.isSynthetic = isSynthetic;
		this.fieldVisibility = fieldVisibility;
		this.fieldFinality = fieldFinality;
		this.isTransient = isTransient;
		this.isFinal = isFinal;
		this.isStatic = isStatic;
		this.isDeprecated = isDeprecated;
		this.isSyntheticAttribute = isSyntheticAttribute;
		this.signature = signature;
		this.constantValue = constantValue;
		this.runtimeAnnotationValues = runtimeAnnotationValues;
		this.visibleTypeAnnotations = visibleTypeAnnotations;
		this.invisibleTypeAnnotations = invisibleTypeAnnotations;
	}

	public boolean isInstance()
	{
		return !isStatic;
	}

	public boolean isPrivate()
	{
		return fieldVisibility == Private;
	}

	@NotNull
	public <T> T annotationValue(@NotNull final Class<? extends Annotation> annotationClass, @NotNull final T defaultValue)
	{
		return runtimeAnnotationValues.annotationValue(knownReferenceTypeName(annotationClass.getName()), defaultValue);
	}
}
