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

package com.stormmq.java.classfile.parser.javaClassFileParsers.versionedClassFileParsers;

import com.stormmq.java.classfile.domain.*;
import com.stormmq.java.classfile.domain.attributes.AttributeLocation;
import com.stormmq.java.classfile.domain.attributes.UnknownAttributes;
import com.stormmq.java.classfile.domain.attributes.annotations.AnnotationValue;
import com.stormmq.java.classfile.domain.attributes.annotations.TypeAnnotation;
import com.stormmq.java.classfile.domain.attributes.code.Code;
import com.stormmq.java.classfile.domain.attributes.method.MethodParameter;
import com.stormmq.java.classfile.domain.attributes.type.BootstrapMethod;
import com.stormmq.java.classfile.domain.attributes.type.enclosingMethods.EnclosingMethod;
import com.stormmq.java.classfile.domain.descriptors.FieldDescriptor;
import com.stormmq.java.classfile.domain.descriptors.MethodDescriptor;
import com.stormmq.java.classfile.domain.information.*;
import com.stormmq.java.classfile.domain.names.FieldName;
import com.stormmq.java.classfile.domain.names.MethodName;
import com.stormmq.java.classfile.domain.signatures.Signature;
import com.stormmq.java.classfile.domain.uniqueness.FieldUniqueness;
import com.stormmq.java.classfile.domain.uniqueness.MethodUniqueness;
import com.stormmq.java.classfile.parser.JavaClassFileReader;
import com.stormmq.java.classfile.parser.javaClassFileParsers.attributesParsers.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constantParsers.ConstantParser;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.Constant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.JavaClassFileContainsDataTooLongToReadException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.functions.InvalidExceptionBiIntConsumer;
import com.stormmq.java.parsing.utilities.*;
import com.stormmq.java.parsing.utilities.names.typeNames.TypeName;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import com.stormmq.string.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.stormmq.java.classfile.domain.TypeKind.*;
import static com.stormmq.java.classfile.domain.attributes.AttributeLocation.*;
import static com.stormmq.java.classfile.domain.names.MethodName.InstanceInitializer;
import static com.stormmq.java.classfile.domain.names.MethodName.StaticInstanceInitializer;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.FieldAccessFlags.*;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.MethodAccessFlags.*;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.TypeAccessFlags.*;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constantParsers.ConstantParser.constantParsers;
import static com.stormmq.java.parsing.utilities.Completeness.Abstract;
import static com.stormmq.java.parsing.utilities.Completeness.Final;
import static com.stormmq.java.parsing.utilities.Visibility.Public;

public final class ModernClassFileVersionedClassFileParser implements VersionedClassFileParser
{
	@NotNull private static final Map<JavaClassFileVersion, AttributesParser> TypeAttributesParsers = initialiseAttributesParsers(Type);
	@NotNull private static final Map<JavaClassFileVersion, AttributesParser> FieldAttributesParsers = initialiseAttributesParsers(Field);
	@NotNull private static final Map<JavaClassFileVersion, AttributesParser> MethodAttributesParsers = initialiseAttributesParsers(Method);
	@NotNull private static final Map<JavaClassFileVersion, ConstantParser[]> ConstantParsers = initialiseConstantParsers();

	@NotNull
	private static Map<JavaClassFileVersion, AttributesParser> initialiseAttributesParsers(@NotNull final AttributeLocation attributeLocation)
	{
		final EnumMap<JavaClassFileVersion, AttributesParser> map = new EnumMap<>(JavaClassFileVersion.class);
		for (final JavaClassFileVersion javaClassFileVersion : JavaClassFileVersion.values())
		{
			map.put(javaClassFileVersion, new AttributesParser(new AttributeParserMappings(javaClassFileVersion, attributeLocation)));
		}
		return map;
	}

	@NotNull
	private static Map<JavaClassFileVersion, ConstantParser[]> initialiseConstantParsers()
	{
		final EnumMap<JavaClassFileVersion, ConstantParser[]> map = new EnumMap<>(JavaClassFileVersion.class);
		for (final JavaClassFileVersion javaClassFileVersion : JavaClassFileVersion.values())
		{
			map.put(javaClassFileVersion, constantParsers(javaClassFileVersion));
		}
		return map;
	}

	@NotNull private final JavaClassFileReader javaClassFileReader;
	@NotNull private final JavaClassFileVersion javaClassFileVersion;
	private final boolean permitConstantsInInstanceFields;

	@NotNull private final AttributesParser typeAttributesParser;
	@NotNull private final AttributesParser methodAttributesParser;
	@NotNull private final AttributesParser fieldAttributesParser;
	@NotNull private final ConstantParser[] constantParsers;

	public ModernClassFileVersionedClassFileParser(@NotNull final JavaClassFileReader javaClassFileReader, @NotNull final JavaClassFileVersion javaClassFileVersion, final boolean permitConstantsInInstanceFields)
	{
		this.javaClassFileReader = javaClassFileReader;
		this.javaClassFileVersion = javaClassFileVersion;
		this.permitConstantsInInstanceFields = permitConstantsInInstanceFields;

		typeAttributesParser = TypeAttributesParsers.get(javaClassFileVersion);
		methodAttributesParser = MethodAttributesParsers.get(javaClassFileVersion);
		fieldAttributesParser = FieldAttributesParsers.get(javaClassFileVersion);
		constantParsers = ConstantParsers.get(javaClassFileVersion);
	}

	@Override
	@NotNull
	public TypeInformation parse() throws InvalidJavaClassFileException, JavaClassFileContainsDataTooLongToReadException
	{
		final ConstantPool constantPool = newConstantPool();
		final ConstantPoolJavaClassFileReader constantPoolJavaClassFileReader = parseConstantPool(constantPool);

		final char typeAccessFlags = constantPoolJavaClassFileReader.readAccessFlags(TypeAccessFlagsValidityMask);
		final boolean isTypeSynthetic = isTypeSynthetic(typeAccessFlags);
		final TypeKind typeKind = typeKind(typeAccessFlags, javaClassFileVersion);
		final boolean hasLegacySuperFlagSetting = hasLegacySuperFlagSetting(typeAccessFlags, javaClassFileVersion);
		final Visibility typeVisibility = typeVisibility(typeAccessFlags);
		final Completeness typeCompleteness = typeCompleteness(typeAccessFlags);

		final TypeInterfacesParser typeInterfacesParser = new TypeInterfacesParser(constantPoolJavaClassFileReader, typeKind);
		@NotNull final KnownReferenceTypeName thisClassTypeName = typeInterfacesParser.parseThisClass();
		@Nullable final KnownReferenceTypeName superClassTypeName = typeInterfacesParser.parseSuperClass(thisClassTypeName);
		final Set<KnownReferenceTypeName> interfaces = typeInterfacesParser.parseInterfaces(thisClassTypeName, superClassTypeName);

		final boolean isAnnotation = typeKind == Annotation;
		final boolean isEnum = typeKind == Enum;
		final boolean isInterfaceOrAnnotation = typeKind == Interface || isAnnotation;
		final Map<FieldUniqueness, FieldInformation> fields = parseFields(constantPoolJavaClassFileReader, isInterfaceOrAnnotation, thisClassTypeName);
		final boolean isInnerClass = typeKind == Class && isInnerClass(fields, thisClassTypeName);
		final Map<MethodUniqueness, MethodInformation> methods = parseMethods(constantPoolJavaClassFileReader, isInterfaceOrAnnotation, thisClassTypeName, isAnnotation, isEnum, isInnerClass);
		final Attributes attributes = typeAttributesParser.parseAttributes(constantPoolJavaClassFileReader);

		final boolean isSyntheticAttribute = attributes.isSynthetic();
		final boolean isDeprecated = attributes.isDeprecated();
		@Nullable final Signature signature = attributes.signature(thisClassTypeName, superClassTypeName, interfaces);
		@NotNull final AnnotationValue[] visibleAnnotations = attributes.runtimeVisibleAnnotations();
		@NotNull final AnnotationValue[] invisibleAnnotations = attributes.runtimeInvisibleAnnotations();
		@NotNull final TypeAnnotation[] visibleTypeAnnotations = attributes.runtimeVisibleTypeAnnotations();
		@NotNull final TypeAnnotation[] invisibleTypeAnnotations = attributes.runtimeInvisibleTypeAnnotations();
		final UnknownAttributes unknownAttributes = attributes.unknownAttributes();
		@Nullable final String sourceFile = attributes.sourceFile();
		@Nullable final EnclosingMethod enclosingMethod = attributes.enclosingMethod();
		@Nullable final String sourceDebugExtension = attributes.sourceDebugExtension();
		@NotNull final BootstrapMethod[] bootstrapMethods = attributes.bootstrapMethods();

		// TODO: There must be exactly one BootstrapMethods attribute in the attributes table of a ClassFile structure if the constant_pool table of the ClassFile structure has at least one CONSTANT_InvokeDynamic_info entry (§4.4.10).
		// TODO: The value of the bootstrap_method_attr_index item must be a valid index into the bootstrap_methods array of the bootstrap method table (§4.7.23) of this class file.

		return new TypeInformation(typeKind, typeVisibility, typeCompleteness, isTypeSynthetic, hasLegacySuperFlagSetting, thisClassTypeName, superClassTypeName, interfaces, fields, methods, isSyntheticAttribute, isDeprecated, signature, visibleAnnotations, invisibleAnnotations, visibleTypeAnnotations, invisibleTypeAnnotations, unknownAttributes, sourceFile, enclosingMethod, sourceDebugExtension, bootstrapMethods);
	}

	private static boolean isInnerClass(@NotNull final Map<FieldUniqueness, FieldInformation> fields, @NotNull final KnownReferenceTypeName thisClassTypeName)
	{
		for (final FieldUniqueness fieldUniqueness : fields.keySet())
		{
			if (fieldUniqueness.fieldName.equals(new FieldName("this$0")))
			{
				final InternalTypeName internalTypeName = fieldUniqueness.fieldDescriptor.internalTypeName;
				if (!internalTypeName.isArray())
				{
					final TypeName typeName = internalTypeName.typeName();
					if (typeName instanceof KnownReferenceTypeName)
					{
						final String value = ((KnownReferenceTypeName) typeName).fullyQualifiedNameUsingDotsAndDollarSigns();
						final String thisClassValue = thisClassTypeName.fullyQualifiedNameUsingDotsAndDollarSigns();
						if (thisClassValue.startsWith(value))
						{
							final String innerClassName = thisClassValue.substring(value.length());
							if (!innerClassName.isEmpty() && innerClassName.charAt(0) == '$')
							{
								if (innerClassName.substring(1).indexOf('$') == -1)
								{
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	@NotNull
	private ConstantPool newConstantPool() throws InvalidJavaClassFileException
	{
		final char constantPoolCount = javaClassFileReader.readBigEndianUnsigned16BitInteger("constant pool count");

		if (constantPoolCount == 0)
		{
			throw new InvalidJavaClassFileException("Constant pool count must be greater than zero");
		}

		return new ConstantPool(constantPoolCount);
	}

	@NotNull
	private ConstantPoolJavaClassFileReader parseConstantPool(@NotNull final ConstantPool constantPool) throws InvalidJavaClassFileException
	{
		@NotNull final ConstantPoolJavaClassFileReader constantPoolJavaClassFileReader = new ConstantPoolJavaClassFileReader(javaClassFileReader, constantPool);
		char constantPoolIndexUnsigned16BitInteger = 1;

		// <, not <=, because constantPoolCount is one greater than the number of entries
		final char constantPoolCount = constantPool.constantPoolCount();
		while (constantPoolIndexUnsigned16BitInteger < constantPoolCount)
		{
			final short tag = javaClassFileReader.readUnsigned8BitInteger("constant pool tag");

			final ConstantPoolIndex constantPoolIndex = new ConstantPoolIndex(constantPoolIndexUnsigned16BitInteger);
			final Constant constant = constantParsers[tag].parse(constantPoolIndex, constantPoolJavaClassFileReader, constantPool);
			constantPoolIndexUnsigned16BitInteger += constantPool.add(constantPoolIndex, constant);
		}

		constantPool.validateReferenceIndices();
		return constantPoolJavaClassFileReader;
	}

	@NotNull
	private Map<FieldUniqueness, FieldInformation> parseFields(@NotNull final ConstantPoolJavaClassFileReader constantPoolJavaClassFileReader, final boolean isInterfaceOrAnnotation, @NotNull final KnownReferenceTypeName thisClassTypeName) throws InvalidJavaClassFileException, JavaClassFileContainsDataTooLongToReadException
	{
		return constantPoolJavaClassFileReader.parseTableAsMapWith16BitLength((fields, index) ->
		{
			final char fieldAccessFlags = constantPoolJavaClassFileReader.readAccessFlags(FieldAccessFlagsValidityMask);
			final FieldName fieldName = constantPoolJavaClassFileReader.readFieldName("field name");
			final FieldDescriptor fieldDescriptor = constantPoolJavaClassFileReader.readFieldDescriptor("field descriptor");

			// This is odd. In Java code, fields must be unique by name, but in the specification, unique by name and type
			final FieldUniqueness fieldUniqueness = new FieldUniqueness(fieldName, fieldDescriptor);
			if (fields.containsKey(fieldUniqueness))
			{
				throw new InvalidJavaClassFileException(Formatting.format("The field '%1$s' in type '%2$s' is a duplicate", fieldUniqueness, thisClassTypeName));
			}

			final boolean isSynthetic = isFieldSynthetic(fieldAccessFlags);
			final Visibility fieldVisibility = fieldVisibility(fieldAccessFlags, isInterfaceOrAnnotation);
			final FieldFinality fieldFinality = fieldFinality(fieldAccessFlags);
			final boolean isTransient = isFieldTransient(fieldAccessFlags, isInterfaceOrAnnotation);
			final boolean isFinal = isFieldFinal(fieldAccessFlags, isInterfaceOrAnnotation);
			final boolean isStatic = isFieldStatic(fieldAccessFlags, isInterfaceOrAnnotation);

			final Attributes attributes = fieldAttributesParser.parseAttributes(constantPoolJavaClassFileReader);

			final boolean isSyntheticAttribute = attributes.isSynthetic();
			final boolean isDeprecated = attributes.isDeprecated();
			@Nullable final Signature signature = attributes.signature(fieldDescriptor);
			@NotNull final AnnotationValue[] visibleAnnotations = attributes.runtimeVisibleAnnotations();
			@NotNull final AnnotationValue[] invisibleAnnotations = attributes.runtimeInvisibleAnnotations();
			@NotNull final TypeAnnotation[] visibleTypeAnnotations = attributes.runtimeVisibleTypeAnnotations();
			@NotNull final TypeAnnotation[] invisibleTypeAnnotations = attributes.runtimeInvisibleTypeAnnotations();
			@Nullable final Object constantValue = attributes.constantValue(!isStatic, permitConstantsInInstanceFields);

			fields.put(fieldUniqueness, new FieldInformation(fieldUniqueness, isSynthetic, fieldVisibility, fieldFinality, isTransient, isFinal, isStatic, isDeprecated, isSyntheticAttribute, signature, constantValue, visibleAnnotations, invisibleAnnotations, visibleTypeAnnotations, invisibleTypeAnnotations));
		});
	}

	@NotNull
	private Map<MethodUniqueness, MethodInformation> parseMethods(@NotNull final ConstantPoolJavaClassFileReader constantPoolJavaClassFileReader, final boolean isInterfaceOrAnnotation, @NotNull final KnownReferenceTypeName thisClassTypeName, final boolean isAnnotation, final boolean isEnum, final boolean isInnerClass) throws InvalidJavaClassFileException, JavaClassFileContainsDataTooLongToReadException
	{
		return constantPoolJavaClassFileReader.parseTableAsMapWith16BitLength(new InvalidExceptionBiIntConsumer<Map<MethodUniqueness, MethodInformation>>()
		{
			private boolean staticInitializerEncountered = false;

			@Override
			public void accept(@NotNull final Map<MethodUniqueness, MethodInformation> methods, final int index) throws InvalidJavaClassFileException, JavaClassFileContainsDataTooLongToReadException
			{
				final char methodAccessFlags = constantPoolJavaClassFileReader.readAccessFlags(MethodAccessFlagsValidityMask);
				final MethodName methodName = constantPoolJavaClassFileReader.readMethodName("method name");
				final MethodDescriptor methodDescriptor = constantPoolJavaClassFileReader.readMethodDescriptor("method descriptor", false);

				// This is a little odd. In Java code, methods must be unique by name and parameters (excluding return type) but in the specification, by name and parameters and return type
				final MethodUniqueness methodUniqueness = new MethodUniqueness(methodName, methodDescriptor);

				if (isAnnotation)
				{
					if (methodDescriptor.hasParameters())
					{
						throw new InvalidJavaClassFileException(Formatting.format("The method '%1$s' in type '%2$s' is on an annotation but has parameters", methodUniqueness, thisClassTypeName));
					}

					if (methodDescriptor.hasVoidReturnType())
					{
						throw new InvalidJavaClassFileException(Formatting.format("The method '%1$s' in type '%2$s' is on an annotation but has a void return type", methodUniqueness, thisClassTypeName));
					}
				}

				if (methods.containsKey(methodUniqueness))
				{
					throw new InvalidJavaClassFileException(Formatting.format("The method '%1$s' in type '%2$s' is a duplicate", methodUniqueness, thisClassTypeName));
				}

				final Visibility methodVisibility = validateAccessFlags(methodAccessFlags, isInterfaceOrAnnotation, javaClassFileVersion, methodName);
				final boolean isSynthetic = isMethodSynthetic(methodAccessFlags);
				final boolean isBridge = isMethodBridge(methodAccessFlags);
				final boolean isVarArgs = isMethodVarArgs(methodAccessFlags);
				final Completeness methodCompleteness = methodCompleteness(methodAccessFlags);
				final boolean isSynchronized = isMethodSynchronized(methodAccessFlags);
				final boolean isNative = isMethodNative(methodAccessFlags);
				final boolean isStatic = isMethodStatic(methodAccessFlags);
				final boolean isStrictFloatingPoint = isMethodStrictFloatingPoint(methodAccessFlags);

				final Attributes attributes = methodAttributesParser.parseAttributes(constantPoolJavaClassFileReader);

				final boolean isSyntheticAttribute = attributes.isSynthetic();
				final boolean isDeprecated = attributes.isDeprecated();
				@Nullable final Signature signature = attributes.signature(methodDescriptor);
				final AnnotationValue[] visibleAnnotations = attributes.runtimeVisibleAnnotations();
				final AnnotationValue[] invisibleAnnotations = attributes.runtimeInvisibleAnnotations();

				// Weirdness for Enum constructors
				final int correction;
				if (isEnum && methodName.equals(InstanceInitializer))
				{
					correction = 2;
				}
				else if (isInnerClass && methodName.equals(InstanceInitializer))
				{
					correction = 1;
				}
				else
				{
					correction = 0;
				}
				final int methodParameterCount = methodDescriptor.parameterCount() - correction;

				final AnnotationValue[][] visibleParameterAnnotations = attributes.runtimeVisibleParameterAnnotations(isAnnotation, methodParameterCount);
				final AnnotationValue[][] invisibleParameterAnnotations = attributes.runtimeInvisibleParameterAnnotations(isAnnotation, methodParameterCount);
				final TypeAnnotation[] visibleTypeAnnotations = attributes.runtimeVisibleTypeAnnotations();
				final TypeAnnotation[] invisibleTypeAnnotations = attributes.runtimeInvisibleTypeAnnotations();

				final Set<KnownReferenceTypeName> exceptions = attributes.exceptions(isAnnotation);
				final MethodParameter[] methodParameters = attributes.methodParameters(isAnnotation, methodParameterCount);
				@Nullable final Object annotationDefault = attributes.annotationDefault(isAnnotation);
				final UnknownAttributes unknownAttributes = attributes.unknownAttributes();

				@Nullable final Code code = attributes.code();
				final boolean hasCode = code != null;
				if (hasCode)
				{
					if (isNative)
					{
						throw new InvalidJavaClassFileException(Formatting.format("The method '%1$s' in type '%2$s' is native but has a Code attribute", methodUniqueness, thisClassTypeName));
					}

					if (methodCompleteness == Abstract)
					{
						throw new InvalidJavaClassFileException(Formatting.format("The method '%1$s' in type '%2$s' is abstract but has a Code attribute", methodUniqueness, thisClassTypeName));
					}

					if (isAnnotation)
					{
						throw new InvalidJavaClassFileException(Formatting.format("The method '%1$s' in type '%2$s' is on an annotation but has a Code attribute", methodUniqueness, thisClassTypeName));
					}
				}
				else
				{
					if (methodCompleteness != Abstract && !isNative)
					{
						throw new InvalidJavaClassFileException(Formatting.format("The method '%1$s' in type '%2$s' is '%3$s' and is not native but has a Code attribute", methodUniqueness, thisClassTypeName, methodCompleteness));
					}
				}


				// TODO: validate parameters annos match up descriptor and parameter access stuff
				final MethodInformation methodInformation;
				if (methodName.equals(StaticInstanceInitializer))
				{
					if (methodDescriptor.hasParameters())
					{
						throw new InvalidJavaClassFileException(Formatting.format("The static initializer method has parameters in type '%1$s'", thisClassTypeName));
					}

					if (methodDescriptor.hasReturnTypeOtherThanVoid())
					{
						throw new InvalidJavaClassFileException(Formatting.format("The static initializer method has a return type other than void in type '%1$s'", thisClassTypeName));
					}

					if (staticInitializerEncountered)
					{
						throw new InvalidJavaClassFileException(Formatting.format("The static initializer method is duplicated in type '%1$s'", thisClassTypeName));
					}
					staticInitializerEncountered = true;

					// Class and interface initialization methods are called implicitly by the Java Virtual Machine. The value of their access_flags item is ignored except for the setting of the ACC_STRICT flag.
					methodInformation = new MethodInformation(methodUniqueness, Public, isSynthetic, isBridge, false, Final, false, false, true, isStrictFloatingPoint, isSyntheticAttribute, isDeprecated, signature, visibleAnnotations, invisibleAnnotations, visibleParameterAnnotations, invisibleParameterAnnotations, visibleTypeAnnotations, invisibleTypeAnnotations, exceptions, methodParameters, code, annotationDefault, unknownAttributes);
				}
				else
				{
					methodInformation = new MethodInformation(methodUniqueness, methodVisibility, isSynthetic, isBridge, isVarArgs, methodCompleteness, isSynchronized, isNative, isStatic, isStrictFloatingPoint, isSyntheticAttribute, isDeprecated, signature, visibleAnnotations, invisibleAnnotations, visibleParameterAnnotations, invisibleParameterAnnotations, visibleTypeAnnotations, invisibleTypeAnnotations, exceptions, methodParameters, code, annotationDefault, unknownAttributes);
				}
				methods.put(methodUniqueness, methodInformation);
			}
		});
	}

}
