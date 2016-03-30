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

import com.stormmq.java.classfile.domain.InvalidInternalTypeNameException;
import com.stormmq.java.classfile.domain.attributes.UnknownAttributeData;
import com.stormmq.java.classfile.domain.attributes.UnknownAttributes;
import com.stormmq.java.classfile.domain.attributes.annotations.*;
import com.stormmq.java.classfile.domain.attributes.code.LineNumberEntry;
import com.stormmq.java.classfile.domain.attributes.code.localVariables.*;
import com.stormmq.java.classfile.domain.attributes.code.stackMapFrames.StackMapFrame;
import com.stormmq.java.classfile.domain.attributes.method.MethodParameter;
import com.stormmq.java.classfile.domain.attributes.type.BootstrapMethod;
import com.stormmq.java.classfile.domain.descriptors.FieldDescriptor;
import com.stormmq.java.classfile.domain.descriptors.MethodDescriptor;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.*;

import java.lang.annotation.RetentionPolicy;
import java.util.*;
import java.util.function.IntFunction;

import static com.stormmq.java.classfile.domain.attributes.annotations.AnnotationValue.EmptyParameterAnnotations;
import static com.stormmq.java.classfile.domain.attributes.annotations.AnnotationValues.NoAnnotationValues;
import static com.stormmq.java.classfile.domain.attributes.annotations.AnnotationValues.convertAnnotationValues;
import static com.stormmq.java.classfile.domain.attributes.annotations.TypeAnnotation.EmptyTypeAnnotations;
import static com.stormmq.java.classfile.domain.attributes.code.stackMapFrames.StackMapFrame.ImplicitStackMap;
import static com.stormmq.java.classfile.domain.attributes.method.MethodParameter.EmptyMethodParameters;
import static com.stormmq.java.classfile.domain.attributes.type.BootstrapMethod.EmptyBootstrapMethods;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.attributesParsers.ArrayMerge.arrayMerge;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Collections.*;

public final class Attributes
{
	@NonNls @NotNull public static final String AnnotationDefault = "AnnotationDefault";
	@NonNls @NotNull public static final String Code = "Code";
	@NonNls @NotNull public static final String ConstantValue = "ConstantValue";
	@NonNls @NotNull public static final String Deprecated = "Deprecated";
	@NonNls @NotNull public static final String Exceptions = "Exceptions";
	@NonNls @NotNull public static final String LineNumberTable = "LineNumberTable";
	@NonNls @NotNull public static final String LocalVariableTable = "LocalVariableTable";
	@NonNls @NotNull public static final String LocalVariableTypeTable = "LocalVariableTypeTable";
	@NonNls @NotNull public static final String RuntimeVisibleAnnotations = "RuntimeVisibleAnnotations";
	@NonNls @NotNull public static final String RuntimeInvisibleAnnotations = "RuntimeInvisibleAnnotations";
	@NonNls @NotNull public static final String RuntimeVisibleParameterAnnotations = "RuntimeVisibleParameterAnnotations";
	@NonNls @NotNull public static final String RuntimeInvisibleParameterAnnotations = "RuntimeInvisibleParameterAnnotations";
	@NonNls @NotNull public static final String RuntimeVisibleTypeAnnotations = "RuntimeVisibleTypeAnnotations";
	@NonNls @NotNull public static final String RuntimeInvisibleTypeAnnotations = "RuntimeInvisibleTypeAnnotations";
	@NonNls @NotNull public static final String Signature = "Signature";
	@NonNls @NotNull public static final String Synthetic = "Synthetic";
	@NonNls @NotNull public static final String EnclosingMethod = "EnclosingMethod";
	@NonNls @NotNull public static final String InnerClasses = "InnerClasses";
	@NonNls @NotNull public static final String BootstrapMethods = "BootstrapMethods";
	@NonNls @NotNull public static final String MethodParameters = "MethodParameters";
	@NonNls @NotNull public static final String StackMapTable = "StackMapTable";
	@NonNls @NotNull public static final String SourceDebugExtension = "SourceDebugExtension";
	@NonNls @NotNull public static final String SourceFile = "SourceFile";
	@NotNull private static final Set<KnownReferenceTypeName> EmptyExceptions = emptySet();
	@NotNull private static final LineNumberEntry[] EmptyLineNumberEntries = {};

	@NotNull private final Map<String, Object> attributes;
	@NotNull private final Map<String, List<UnknownAttributeData>> unknownAttributes;

	public Attributes(@NotNull final Map<String, Object> attributes, @NotNull final Map<String, List<UnknownAttributeData>> unknownAttributes)
	{
		this.attributes = attributes;
		this.unknownAttributes = unknownAttributes;
	}

	@Nullable
	public Object annotationDefault(final boolean isAnnotation) throws InvalidJavaClassFileException
	{
		if (!isAnnotation && hasAttribute(AnnotationDefault))
		{
			throw new InvalidJavaClassFileException("Only annotations can have an annotation default");
		}
		return getAttributeValueNullable(AnnotationDefault, null);
	}

	@NotNull
	public BootstrapMethod[] bootstrapMethods()
	{
		return getAttributeValueNotNull(BootstrapMethods, EmptyBootstrapMethods);
	}

	@Nullable
	public com.stormmq.java.classfile.domain.attributes.code.Code code()
	{
		return getAttributeValueNullable(Code, null);
	}

	@Nullable
	public Object constantValue(final boolean isInstance, final boolean permitConstantsInInstanceFields) throws InvalidJavaClassFileException
	{
		if (isInstance)
		{
			if (hasAttribute(ConstantValue) && !permitConstantsInInstanceFields)
			{
				throw new InvalidJavaClassFileException("Instance fields should not contain constants");
			}
		}
		return getAttributeValueNullable(ConstantValue, null);
	}

	public boolean isDeprecated()
	{
		return hasAttribute(Deprecated);
	}

	@Nullable
	public com.stormmq.java.classfile.domain.attributes.type.enclosingMethods.EnclosingMethod enclosingMethod()
	{
		return getAttributeValueNullable(EnclosingMethod, null);
	}

	@NotNull
	public Set<KnownReferenceTypeName> exceptions(final boolean isAnnotation) throws InvalidJavaClassFileException
	{
		final Set<KnownReferenceTypeName> exceptions = getAttributeValueNotNull(Exceptions, EmptyExceptions);
		if (isAnnotation && !exceptions.isEmpty())
		{
			throw new InvalidJavaClassFileException("An annotation can not have exceptions");
		}
		return exceptions;
	}

	@SuppressWarnings("ForLoopReplaceableByForEach")
	@NotNull
	public Map<Character, Set<Character>> lineNumberEntries(final long codeLength) throws InvalidJavaClassFileException
	{
		final LineNumberEntry[] lineNumberEntries = getArrayMerged(LineNumberEntry[]::new, LineNumberTable, EmptyLineNumberEntries);

		final int length = lineNumberEntries.length;
		if (length == 0)
		{
			return emptyMap();
		}

		final Map<Character, Set<Character>> programCounterToLineNumberEntryMap = new HashMap<>(length);
		for (int index = 0; index < length; index++)
		{
			final LineNumberEntry lineNumberEntry = lineNumberEntries[index];
			if (lineNumberEntry.isAfterEndOfCode(codeLength))
			{
				throw new InvalidJavaClassFileException("LineNumberEntry exceeds code length");
			}
			lineNumberEntry.add(programCounterToLineNumberEntryMap);
		}

		return programCounterToLineNumberEntryMap;
	}

	@NotNull
	public LocalVariables localVariableWithSignatures(final long codeLength, final char maximumLocals) throws InvalidJavaClassFileException
	{
		final List<DescriptorLocalVariable> descriptorLocalVariables = variablesByVariableIndex(maximumLocals, codeLength, LocalVariableTable);
		final List<SignatureLocalVariable> signatureLocalVariables = variablesByVariableIndex(maximumLocals, codeLength, LocalVariableTypeTable);

		return new LocalVariables(codeLength, maximumLocals, descriptorLocalVariables, signatureLocalVariables);
	}

	@NotNull
	public MethodParameter[] methodParameters(final boolean isAnnotation, final int methodDescriptorParameterCount) throws InvalidJavaClassFileException
	{
		return parameterLike(isAnnotation, methodDescriptorParameterCount, MethodParameters, EmptyMethodParameters);
	}

	@NotNull
	public AnnotationValues runtimeAnnotations() throws InvalidJavaClassFileException
	{
		// In theory, the JVM spec does not prevent the same annotation being in both sections but it is likely to represent a compilation bug
		final Map<KnownReferenceTypeName, RetentionPolicyAndValues> invisible = annotations(RuntimeInvisibleAnnotations, CLASS);
		final Map<KnownReferenceTypeName, RetentionPolicyAndValues> visible = annotations(RuntimeVisibleAnnotations, RUNTIME);
		return mergeInvisibleAndVisibleAnnotations(invisible, visible);
	}

	@NotNull
	public AnnotationValues[] runtimeParameterAnnotations(final boolean isAnnotation, final int methodParameterCount) throws InvalidJavaClassFileException
	{
		final Map<KnownReferenceTypeName, RetentionPolicyAndValues>[] invisible = parameterAnnotations(isAnnotation, methodParameterCount, RuntimeInvisibleParameterAnnotations, CLASS);
		final Map<KnownReferenceTypeName, RetentionPolicyAndValues>[] visible = parameterAnnotations(isAnnotation, methodParameterCount, RuntimeVisibleParameterAnnotations, RUNTIME);

		// Lengths may not match up
		final int invisibleLength = invisible.length;
		final int visibleLength = visible.length;
		if (invisibleLength == 0)
		{
			if (visibleLength == 0)
			{
				return NoAnnotationValues;
			}
			return runtimeParameterAnnotationsOneSided(visible, visibleLength);
		}

		if (visibleLength == 0)
		{
			return runtimeParameterAnnotationsOneSided(invisible, invisibleLength);
		}

		if (invisibleLength != visibleLength)
		{
			throw new IllegalStateException("visible and invisible parameter attribute lengths must match if neither are zero length");
		}

		final AnnotationValues[] result = new AnnotationValues[invisibleLength];
		for (int index = 0; index < invisibleLength; index++)
		{
			result[index] = mergeInvisibleAndVisibleAnnotations(invisible[index], visible[index]);
		}
		return result;
	}

	@NotNull
	private static AnnotationValues[] runtimeParameterAnnotationsOneSided(@NotNull final Map<KnownReferenceTypeName, RetentionPolicyAndValues>[] maps, final int length)
	{
		final AnnotationValues[] result = new AnnotationValues[length];
		for(int index = 0; index < length; index++)
		{
			result[index] = new AnnotationValues(maps[index]);
		}
		return result;
	}

	@NotNull
	public TypeAnnotation[] runtimeInvisibleTypeAnnotations()
	{
		return getAttributeValueNotNull(RuntimeInvisibleTypeAnnotations, EmptyTypeAnnotations);
	}

	@NotNull
	public TypeAnnotation[] runtimeVisibleTypeAnnotations()
	{
		return getAttributeValueNotNull(RuntimeVisibleTypeAnnotations, EmptyTypeAnnotations);
	}

	@Nullable
	public com.stormmq.java.classfile.domain.signatures.Signature signature(@NotNull final KnownReferenceTypeName thisClassTypeName, @Nullable final KnownReferenceTypeName superClassTypeName, @NotNull final Set<KnownReferenceTypeName> interfaces)
	{
		@Nullable final com.stormmq.java.classfile.domain.signatures.Signature signature = getSignature();
		if (signature == null)
		{
			return null;
		}
		return signature.validate(thisClassTypeName, superClassTypeName, interfaces);
	}

	@Nullable
	public com.stormmq.java.classfile.domain.signatures.Signature signature(@NotNull final FieldDescriptor fieldDescriptor)
	{
		@Nullable final com.stormmq.java.classfile.domain.signatures.Signature signature = getSignature();
		if (signature == null)
		{
			return null;
		}
		return signature.validate(fieldDescriptor);
	}

	@Nullable
	public com.stormmq.java.classfile.domain.signatures.Signature signature(@NotNull final MethodDescriptor methodDescriptor)
	{
		@Nullable final com.stormmq.java.classfile.domain.signatures.Signature signature = getSignature();
		if (signature == null)
		{
			return null;
		}
		return signature.validate(methodDescriptor);
	}

	@Nullable
	public String sourceDebugExtension()
	{
		return getAttributeValueNullable(SourceDebugExtension, null);
	}

	@Nullable
	public String sourceFile()
	{
		return getAttributeValueNullable(SourceFile, null);
	}

	private com.stormmq.java.classfile.domain.signatures.Signature getSignature()
	{
		return getAttributeValueNullable(Signature, null);
	}

	@NotNull
	public StackMapFrame[] stackMapFrames()
	{
		return getAttributeValueNotNull(StackMapTable, ImplicitStackMap);
	}

	public boolean isSynthetic()
	{
		return hasAttribute(Synthetic);
	}

	@NotNull
	public UnknownAttributes unknownAttributes()
	{
		return new UnknownAttributes(unknownAttributes);
	}

	private boolean hasAttribute(@NotNull @NonNls final String attributeName)
	{
		return attributes.containsKey(attributeName);
	}

	@NotNull
	private <T> T[] parameterLike(final boolean isAnnotation, final int methodParameterCount, @NotNull final String attributeName, @NotNull final T[] empty) throws InvalidJavaClassFileException
	{
		final T[] values = getAttributeValueNotNull(attributeName, empty);
		final int length = values.length;
		if (isAnnotation)
		{
			if (length != 0)
			{
				throw new InvalidJavaClassFileException("An annotation can not have " + attributeName);
			}
		}
		else
		{
			if (length != 0)
			{
				if (length != methodParameterCount)
				{
					throw new InvalidJavaClassFileException(attributeName + " length must match method descriptor parameter count");
				}
			}
		}
		return values;
	}

	@NotNull
	private static AnnotationValues mergeInvisibleAndVisibleAnnotations(@NotNull final Map<KnownReferenceTypeName, RetentionPolicyAndValues> invisible, @NotNull final Map<KnownReferenceTypeName, RetentionPolicyAndValues> visible) throws InvalidJavaClassFileException
	{
		final int invisibleSize = invisible.size();
		final int visibleSize = visible.size();
		if (invisibleSize == 0)
		{
			if (visibleSize == 0)
			{
				return AnnotationValues.EmptyAnnotationValues;
			}
			return new AnnotationValues(visible);
		}
		if (visibleSize == 0)
		{
			return new AnnotationValues(invisible);
		}

		// An annotation can be present in both invisible and visible; if the values differ, too bad
		final HashMap<KnownReferenceTypeName, RetentionPolicyAndValues> merged = new HashMap<>(invisibleSize + visibleSize);
		merged.putAll(invisible);
		merged.putAll(visible);
		return new AnnotationValues(merged);
	}

	@NotNull
	private Map<KnownReferenceTypeName, RetentionPolicyAndValues> annotations(@NotNull @NonNls final String attributeName, @NotNull final RetentionPolicy ofRetentionPolicy) throws InvalidJavaClassFileException
	{
		@Nullable final Object value = attributes.get(attributeName);
		if (value == null)
		{
			return AnnotationValues.Empty;
		}
		return newAnnotationValues(ofRetentionPolicy, (AnnotationValue[]) value);
	}

	@NotNull
	private static Map<KnownReferenceTypeName, RetentionPolicyAndValues> newAnnotationValues(@NotNull final RetentionPolicy ofRetentionPolicy, @NotNull final AnnotationValue[] value) throws InvalidJavaClassFileException
	{
		try
		{
			return convertAnnotationValues(ofRetentionPolicy, (AnnotationValue[]) value);
		}
		catch (final InvalidInternalTypeNameException e)
		{
			throw new InvalidJavaClassFileException("Annotation Value with non-Annotation type", e);
		}
		catch (final DuplicateAnnotationValueException e)
		{
			throw new InvalidJavaClassFileException("Duplicate annotation value", e);
		}
	}

	@NotNull
	private Map<KnownReferenceTypeName, RetentionPolicyAndValues>[] parameterAnnotations(final boolean isAnnotation, final int methodParameterCount, @NotNull @NonNls final String attributeName, @NotNull final RetentionPolicy ofRetentionPolicy) throws InvalidJavaClassFileException
	{
		final AnnotationValue[][] annotationValuesValues = parameterLike(isAnnotation, methodParameterCount, attributeName, EmptyParameterAnnotations);
		final int length = annotationValuesValues.length;
		@SuppressWarnings("unchecked") final Map<KnownReferenceTypeName, RetentionPolicyAndValues>[] annotationValues = new Map[length];
		for (int index = 0; index < length; index++)
		{
			annotationValues[index] = newAnnotationValues(ofRetentionPolicy, annotationValuesValues[index]);
		}
		return annotationValues;
	}

	@NotNull
	private <V extends AbstractLocalVariable> List<V> variablesByVariableIndex(final char maximumLocals, final long codeLength, @NotNull final String attributeName) throws InvalidJavaClassFileException
	{
		@SuppressWarnings("unchecked") @Nullable final List<V[]> variableTables = (List<V[]>) attributes.get(attributeName);
		if (variableTables == null)
		{
			return emptyList();
		}

		if (variableTables.size() > maximumLocals)
		{
			if (variableTables.size() == 1)
			{
				if (variableTables.get(0).length == 0)
				{
					return emptyList();
				}
			}
			throw new InvalidJavaClassFileException("There may be no more than one " + attributeName + " attribute per local variable in the attributes table of a Code attribute.");
		}

		final List<V> variables = new ArrayList<>(maximumLocals);

		for (final V[] variableTable : variableTables)
		{
			for (final V localVariable : variableTable)
			{
				try
				{
					localVariable.validateNotAfterEndOfCode(codeLength);
					localVariable.validateDoesNotHaveALocalVariableIndexWhichIsTooLarge(maximumLocals);
				}
				catch (final MismatchedLocalVariableLengthException e)
				{
					throw new InvalidJavaClassFileException("Invalid local variable", e);
				}

				variables.add(localVariable);
			}
		}

		return variables;
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private <V> V[] getArrayMerged(@NotNull final IntFunction<V[]> arrayCreator, @NotNull @NonNls final String attributeName, @NotNull final V[] empty)
	{
		@Nullable final Object value = attributes.get(attributeName);
		if (value == null)
		{
			return empty;
		}
		return arrayMerge(arrayCreator, (List<V[]>) value);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private <V> V getAttributeValueNotNull(@NotNull @NonNls final String attributeName, @NotNull final V defaultValue)
	{
		@Nullable final Object value = attributes.get(attributeName);
		if (value == null)
		{
			return defaultValue;
		}
		return (V) value;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	private<V> V getAttributeValueNullable(@NotNull @NonNls final String attributeName, @Nullable final V defaultValue)
	{
		@Nullable final Object value = attributes.get(attributeName);
		if (value == null)
		{
			return defaultValue;
		}
		return (V) value;
	}
}
