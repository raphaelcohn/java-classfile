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

import com.stormmq.functions.*;
import com.stormmq.java.classfile.domain.InvalidInternalTypeNameException;
import com.stormmq.java.classfile.domain.names.MethodName;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.RetentionPolicy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.stormmq.functions.MapHelper.putOnce;
import static com.stormmq.string.StringConstants.DefaultAnnotationMemberName;
import static java.util.Collections.emptyMap;

public final class AnnotationValues
{
	@NotNull public static final AnnotationValues[] NoAnnotationValues = {};
	@NotNull public static final Map<KnownReferenceTypeName, RetentionPolicyAndValues> Empty = emptyMap();
	@NotNull public static final AnnotationValues EmptyAnnotationValues = new AnnotationValues(Empty);
	@NotNull private static final MethodName ValueMethodName = new MethodName(DefaultAnnotationMemberName);

	@NotNull
	public static Map<KnownReferenceTypeName, RetentionPolicyAndValues> convertAnnotationValues(@NotNull final RetentionPolicy ofRetentionPolicy, @NotNull final AnnotationValue... annotationValues) throws InvalidInternalTypeNameException, DuplicateAnnotationValueException
	{
		final int length = annotationValues.length;
		if (length == 0)
		{
			return Empty;
		}
		final Map<KnownReferenceTypeName, RetentionPolicyAndValues> nameToValues = new LinkedHashMap<>(length);
		//noinspection ForLoopReplaceableByForEach
		for (int index = 0; index < length; index++)
		{
			final AnnotationValue annotationValue = annotationValues[index];
			final KnownReferenceTypeName knownReferenceTypeName = annotationValue.knownReferenceTypeName();
			final Map<MethodName, Object> values = annotationValue.values();
			try
			{
				putOnce(nameToValues, knownReferenceTypeName, (Supplier<RetentionPolicyAndValues>) () -> new RetentionPolicyAndValues(ofRetentionPolicy, values));
			}
			catch (final PutOnceViolationException ignored)
			{
				throw new DuplicateAnnotationValueException(knownReferenceTypeName);
			}
		}
		return nameToValues;
	}

	@NotNull private final Map<KnownReferenceTypeName, RetentionPolicyAndValues> nameToValues;

	public AnnotationValues(@NotNull final Map<KnownReferenceTypeName, RetentionPolicyAndValues> nameToValues)
	{
		this.nameToValues = nameToValues;
	}

	public boolean hasAnnotation(@NotNull final KnownReferenceTypeName annotationTypeName)
	{
		return nameToValues.containsKey(annotationTypeName);
	}

	@NotNull
	public <T> T annotationValue(@NotNull final KnownReferenceTypeName annotationTypeName, @NotNull final T defaultValue)
	{
		return annotationValue(annotationTypeName, ValueMethodName, defaultValue);
	}

	@NotNull
	public <T> T annotationValue(@NotNull final KnownReferenceTypeName annotationTypeName, @NotNull final MethodName methodName, @NotNull final T defaultValue)
	{
		final RetentionPolicyAndValues retentionPolicyAndValues = nameToValues.get(annotationTypeName);
		return retentionPolicyAndValues.value(methodName, defaultValue);
	}
}
