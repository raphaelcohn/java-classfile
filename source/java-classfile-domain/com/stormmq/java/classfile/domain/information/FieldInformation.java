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
import com.stormmq.java.classfile.domain.signatures.Signature;
import com.stormmq.java.classfile.domain.uniqueness.FieldUniqueness;
import com.stormmq.java.parsing.utilities.FieldFinality;
import com.stormmq.java.parsing.utilities.Visibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FieldInformation
{
	@NotNull private final FieldUniqueness fieldUniqueness;
	private final boolean isSynthetic;
	@NotNull private final Visibility fieldVisibility;
	@NotNull private final FieldFinality fieldFinality;
	private final boolean isTransient;
	private final boolean isFinal;
	public final boolean isStatic;
	private final boolean isDeprecated;
	private final boolean isSyntheticAttribute;
	@Nullable private final Signature signature;
	@Nullable private final Object constantValue;
	@NotNull private final AnnotationValues runtimeAnnotationValues;
	@NotNull private final TypeAnnotation[] visibleTypeAnnotations;
	@NotNull private final TypeAnnotation[] invisibleTypeAnnotations;

	public FieldInformation(@NotNull final FieldUniqueness fieldUniqueness, final boolean isSynthetic, @NotNull final Visibility fieldVisibility, @NotNull final FieldFinality fieldFinality, final boolean isTransient, final boolean isFinal, final boolean isStatic, final boolean isDeprecated, final boolean isSyntheticAttribute, @Nullable final Signature signature, @Nullable final Object constantValue, @NotNull final AnnotationValues runtimeAnnotationValues, @NotNull final TypeAnnotation[] visibleTypeAnnotations, @NotNull final TypeAnnotation[] invisibleTypeAnnotations)
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
}
