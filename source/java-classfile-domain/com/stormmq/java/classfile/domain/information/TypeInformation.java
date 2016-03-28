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

import com.stormmq.java.classfile.domain.*;
import com.stormmq.java.classfile.domain.attributes.UnknownAttributes;
import com.stormmq.java.classfile.domain.attributes.annotations.AnnotationValue;
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

import java.util.Map;
import java.util.Set;

public final class TypeInformation
{
	@NotNull private final TypeKind typeKind;
	@NotNull private final Visibility visibility;
	@NotNull private final Completeness completeness;
	private final boolean isSynthetic;
	private final boolean hasLegacySuperFlagSetting;
	@NotNull private final KnownReferenceTypeName thisClassTypeName;
	@Nullable private final KnownReferenceTypeName superClassTypeName;
	@NotNull private final Set<KnownReferenceTypeName> interfaces;
	@NotNull private final Map<FieldUniqueness, FieldInformation> fields;
	@NotNull private final Map<MethodUniqueness, MethodInformation> methods;
	private final boolean isSyntheticAttribute;
	private final boolean isDeprecated;
	@Nullable private final Signature signature;
	@NotNull private final AnnotationValue[] visibleAnnotations;
	@NotNull private final AnnotationValue[] invisibleAnnotations;
	@NotNull private final TypeAnnotation[] typeAnnotations;
	@NotNull private final TypeAnnotation[] visibleTypeAnnotations;
	@NotNull private final UnknownAttributes unknownAttributes;
	@Nullable private final String sourceFile;
	@Nullable private final EnclosingMethod enclosingMethod;
	@Nullable private final String sourceDebugExtension;
	@NotNull private final BootstrapMethod[] bootstrapMethods;

	public TypeInformation(@NotNull final TypeKind typeKind, @NotNull final Visibility visibility, @NotNull final Completeness completeness, final boolean isSynthetic, final boolean hasLegacySuperFlagSetting, @NotNull final KnownReferenceTypeName thisClassTypeName, @Nullable final KnownReferenceTypeName superClassTypeName, @NotNull final Set<KnownReferenceTypeName> interfaces, @NotNull final Map<FieldUniqueness, FieldInformation> fields, @NotNull final Map<MethodUniqueness, MethodInformation> methods, final boolean isSyntheticAttribute, final boolean isDeprecated, @Nullable final Signature signature, @NotNull final AnnotationValue[] visibleAnnotations, @NotNull final AnnotationValue[] invisibleAnnotations, @NotNull final TypeAnnotation[] typeAnnotations, @NotNull final TypeAnnotation[] visibleTypeAnnotations, @NotNull final UnknownAttributes unknownAttributes, @Nullable final String sourceFile, @Nullable final EnclosingMethod enclosingMethod, @Nullable final String sourceDebugExtension, @NotNull final BootstrapMethod[] bootstrapMethods)
	{
		this.typeKind = typeKind;
		this.visibility = visibility;
		this.completeness = completeness;
		this.isSynthetic = isSynthetic;
		this.hasLegacySuperFlagSetting = hasLegacySuperFlagSetting;
		this.thisClassTypeName = thisClassTypeName;
		this.superClassTypeName = superClassTypeName;
		this.interfaces = interfaces;
		this.fields = fields;
		this.methods = methods;
		this.isSyntheticAttribute = isSyntheticAttribute;
		this.isDeprecated = isDeprecated;
		this.signature = signature;
		this.visibleAnnotations = visibleAnnotations;
		this.invisibleAnnotations = invisibleAnnotations;
		this.typeAnnotations = typeAnnotations;
		this.visibleTypeAnnotations = visibleTypeAnnotations;
		this.unknownAttributes = unknownAttributes;
		this.sourceFile = sourceFile;
		this.enclosingMethod = enclosingMethod;
		this.sourceDebugExtension = sourceDebugExtension;
		this.bootstrapMethods = bootstrapMethods;
	}
}
