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

import com.stormmq.java.classfile.domain.TypeKind;
import com.stormmq.java.classfile.domain.attributes.UnknownAttributes;
import com.stormmq.java.classfile.domain.attributes.annotations.AnnotationValues;
import com.stormmq.java.classfile.domain.attributes.annotations.TypeAnnotation;
import com.stormmq.java.classfile.domain.attributes.type.BootstrapMethod;
import com.stormmq.java.classfile.domain.attributes.type.enclosingMethods.EnclosingMethod;
import com.stormmq.java.classfile.domain.signatures.Signature;
import com.stormmq.java.classfile.domain.uniqueness.FieldUniqueness;
import com.stormmq.java.classfile.domain.uniqueness.MethodUniqueness;
import com.stormmq.java.parsing.utilities.Completeness;
import com.stormmq.java.parsing.utilities.Visibility;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

import static java.util.Comparator.reverseOrder;

public final class ConcreteTypeInformation implements TypeInformation
{
	@SuppressWarnings("WeakerAccess") @NotNull public final TypeKind typeKind;
	@SuppressWarnings("WeakerAccess") @NotNull public final Visibility visibility;
	@SuppressWarnings("WeakerAccess") @NotNull public final Completeness completeness;
	@SuppressWarnings("WeakerAccess") public final boolean isSynthetic;
	@SuppressWarnings("WeakerAccess") public final boolean hasLegacySuperFlagSetting;
	@SuppressWarnings("WeakerAccess") @NotNull public final KnownReferenceTypeName thisClassTypeName;
	@SuppressWarnings("WeakerAccess") @Nullable public final KnownReferenceTypeName superClassTypeName;
	@SuppressWarnings("WeakerAccess") @NotNull public final Set<KnownReferenceTypeName> interfaces;
	@SuppressWarnings("WeakerAccess") @NotNull public final SortedMap<FieldUniqueness, FieldInformation> fieldsInReverseSortOrder;
	@SuppressWarnings("WeakerAccess") @NotNull public final Map<MethodUniqueness, MethodInformation> methods;
	@SuppressWarnings("WeakerAccess") public final boolean isSyntheticAttribute;
	@SuppressWarnings("WeakerAccess") public final boolean isDeprecated;
	@SuppressWarnings("WeakerAccess") @Nullable public final Signature signature;
	@SuppressWarnings("WeakerAccess") @NotNull public final AnnotationValues runtimeAnnotationValues;
	@SuppressWarnings("WeakerAccess") @NotNull public final TypeAnnotation[] typeAnnotations;
	@SuppressWarnings("WeakerAccess") @NotNull public final TypeAnnotation[] visibleTypeAnnotations;
	@SuppressWarnings("WeakerAccess") @NotNull public final UnknownAttributes unknownAttributes;
	@SuppressWarnings("WeakerAccess") @Nullable public final String sourceFile;
	@SuppressWarnings("WeakerAccess") @Nullable public final EnclosingMethod enclosingMethod;
	@SuppressWarnings("WeakerAccess") @Nullable public final String sourceDebugExtension;
	@SuppressWarnings("WeakerAccess") @NotNull public final BootstrapMethod[] bootstrapMethods;

	public ConcreteTypeInformation(@NotNull final TypeKind typeKind, @NotNull final Visibility visibility, @NotNull final Completeness completeness, final boolean isSynthetic, final boolean hasLegacySuperFlagSetting, @NotNull final KnownReferenceTypeName thisClassTypeName, @Nullable final KnownReferenceTypeName superClassTypeName, @NotNull final Set<KnownReferenceTypeName> interfaces, @NotNull final Map<FieldUniqueness, FieldInformation> fields, @NotNull final Map<MethodUniqueness, MethodInformation> methods, final boolean isSyntheticAttribute, final boolean isDeprecated, @Nullable final Signature signature, @NotNull final AnnotationValues runtimeAnnotationValues, @NotNull final TypeAnnotation[] typeAnnotations, @NotNull final TypeAnnotation[] visibleTypeAnnotations, @NotNull final UnknownAttributes unknownAttributes, @Nullable final String sourceFile, @Nullable final EnclosingMethod enclosingMethod, @Nullable final String sourceDebugExtension, @NotNull final BootstrapMethod[] bootstrapMethods)
	{
		this.typeKind = typeKind;
		this.visibility = visibility;
		this.completeness = completeness;
		this.isSynthetic = isSynthetic;
		this.hasLegacySuperFlagSetting = hasLegacySuperFlagSetting;
		this.thisClassTypeName = thisClassTypeName;
		this.superClassTypeName = superClassTypeName;
		this.interfaces = interfaces;
		fieldsInReverseSortOrder = new TreeMap<>(reverseOrder());
		fieldsInReverseSortOrder.putAll(fields);
		this.methods = methods;
		this.isSyntheticAttribute = isSyntheticAttribute;
		this.isDeprecated = isDeprecated;
		this.signature = signature;
		this.runtimeAnnotationValues = runtimeAnnotationValues;
		this.typeAnnotations = typeAnnotations;
		this.visibleTypeAnnotations = visibleTypeAnnotations;
		this.unknownAttributes = unknownAttributes;
		this.sourceFile = sourceFile;
		this.enclosingMethod = enclosingMethod;
		this.sourceDebugExtension = sourceDebugExtension;
		this.bootstrapMethods = bootstrapMethods;
	}

	@Override
	public boolean hasAnnotation(@NotNull final KnownReferenceTypeName annotationTypeName)
	{
		return runtimeAnnotationValues.hasAnnotation(annotationTypeName);
	}

	@Override
	public int numberOfStaticAndInstanceFields()
	{
		return fieldsInReverseSortOrder.size();
	}

	@Override
	public void forEachStaticFieldInReverseOrder(@NotNull final BiConsumer<FieldUniqueness, FieldInformation> action)
	{
		fieldsInReverseSortOrder.forEach((fieldUniqueness, fieldInformation) ->
		{
			if (fieldInformation.isStatic)
			{
				action.accept(fieldUniqueness, fieldInformation);
			}
		});
	}

	@Override
	public void forEachInstanceFieldInReverseOrder(@NotNull final BiConsumer<FieldUniqueness, FieldInformation> action)
	{
		fieldsInReverseSortOrder.forEach((fieldUniqueness, fieldInformation) ->
		{
			if (fieldInformation.isInstance())
			{
				action.accept(fieldUniqueness, fieldInformation);
			}
		});
	}

	@NotNull
	@Override
	public KnownReferenceTypeName thisClassTypeName()
	{
		return thisClassTypeName;
	}

	@NotNull
	@Override
	public KnownReferenceTypeName superClassTypeName()
	{
		return superClassTypeName;
	}
}