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

import com.stormmq.java.classfile.domain.InternalTypeName;
import com.stormmq.java.classfile.domain.InvalidInternalTypeNameException;
import com.stormmq.java.classfile.domain.descriptors.FieldDescriptor;
import com.stormmq.java.classfile.domain.names.MethodName;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.stormmq.string.Formatting.format;

public final class AnnotationValue
{
	@NotNull public static final AnnotationValue[] EmptyAnnotationValues = {};
	@NotNull public static final AnnotationValue[][] EmptyParameterAnnotations = new AnnotationValue[0][0];

	@NotNull private final FieldDescriptor typeIndexFieldDescriptor;
	@NotNull private final Map<MethodName, Object> fieldValues;

	public AnnotationValue(@NotNull final FieldDescriptor typeIndexFieldDescriptor, @NotNull final Map<MethodName, Object> fieldValues)
	{
		this.typeIndexFieldDescriptor = typeIndexFieldDescriptor;
		this.fieldValues = fieldValues;
	}

	public boolean is(@NotNull final InternalTypeName annotationInternalTypeName)
	{
		return typeIndexFieldDescriptor.is(annotationInternalTypeName);
	}

	@Override
	@NotNull
	public String toString()
	{
		return format("%1$s(%2$s, %3$s)", getClass().getSimpleName(), typeIndexFieldDescriptor, fieldValues);
	}

	@NotNull
	public KnownReferenceTypeName knownReferenceTypeName() throws InvalidInternalTypeNameException
	{
		return typeIndexFieldDescriptor.knownReferenceTypeName();
	}

	@NotNull
	public Map<MethodName, Object> values()
	{
		return fieldValues;
	}
}
