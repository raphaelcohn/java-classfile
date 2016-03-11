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

package com.stormmq.java.classfile.domain.attributes.annotations;

import com.stormmq.java.classfile.domain.attributes.annotations.targetInformations.TargetInformation;
import com.stormmq.java.classfile.domain.attributes.annotations.typePathElements.TypePathElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public final class TypeAnnotation
{
	@NotNull public static final TypeAnnotation[] EmptyTypeAnnotations = {};

	@NotNull private final TargetType targetType;
	@NotNull private final TargetInformation targetInformation;
	@NotNull private final TypePathElement[] typePath;
	@NotNull private final AnnotationValue annotationValue;

	// An empty typePath implies the annotation is on the type itself
	public TypeAnnotation(@NotNull final TargetType targetType, @NotNull final TargetInformation targetInformation, @NonNls @NotNull final TypePathElement[] typePath, @NotNull final AnnotationValue annotationValue)
	{
		this.targetType = targetType;
		this.targetInformation = targetInformation;
		this.typePath = typePath;
		this.annotationValue = annotationValue;
	}
}
