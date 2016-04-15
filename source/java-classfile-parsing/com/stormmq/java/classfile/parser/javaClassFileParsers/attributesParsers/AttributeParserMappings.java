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

import com.stormmq.functions.*;
import com.stormmq.functions.collections.AddOnceViolationException;
import com.stormmq.java.classfile.domain.*;
import com.stormmq.java.classfile.domain.attributes.*;
import com.stormmq.java.classfile.domain.attributes.annotations.TargetType;
import com.stormmq.java.classfile.domain.attributes.annotations.TypeAnnotation;
import com.stormmq.java.classfile.domain.attributes.code.*;
import com.stormmq.java.classfile.domain.attributes.code.constants.BootstrapMethodArgument;
import com.stormmq.java.classfile.domain.attributes.code.localVariables.LocalVariables;
import com.stormmq.java.classfile.domain.attributes.code.stackMapFrames.*;
import com.stormmq.java.classfile.domain.attributes.code.stackMapFrames.verificationTypes.*;
import com.stormmq.java.classfile.domain.attributes.method.MethodParameter;
import com.stormmq.java.classfile.domain.attributes.type.BootstrapMethod;
import com.stormmq.java.classfile.domain.attributes.type.InnerTypeInformation;
import com.stormmq.java.classfile.domain.attributes.type.enclosingMethods.InsideEnclosingMethod;
import com.stormmq.java.classfile.domain.attributes.type.enclosingMethods.OutsideEnclosingMethod;
import com.stormmq.java.classfile.domain.descriptors.FieldDescriptor;
import com.stormmq.java.classfile.domain.names.FieldName;
import com.stormmq.java.classfile.domain.signatures.Signature;
import com.stormmq.java.classfile.domain.attributes.code.localVariables.DescriptorLocalVariable;
import com.stormmq.java.classfile.domain.attributes.code.localVariables.SignatureLocalVariable;
import com.stormmq.java.classfile.parser.JavaClassFileReader;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolJavaClassFileReader;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.NameAndTypeReferenceIndexConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.functions.InvalidJavaClassFileExceptionBiIntConsumer;
import com.stormmq.java.classfile.parser.javaClassFileParsers.functions.InvalidJavaClassFileExceptionFunction;
import com.stormmq.java.parsing.utilities.Completeness;
import com.stormmq.java.parsing.utilities.Visibility;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.IntFunction;

import static com.stormmq.functions.collections.MapHelper.putOnce;
import static com.stormmq.functions.collections.MapHelper.useMapValueExceptionally;
import static com.stormmq.functions.collections.CollectionHelper.addOnce;
import static com.stormmq.java.classfile.domain.attributes.code.constants.BootstrapMethodArgument.EmptyBootstrapMethodArgumentConstants;
import static com.stormmq.java.classfile.domain.JavaClassFileVersion.*;
import static com.stormmq.java.classfile.domain.attributes.AttributeLocation.*;
import static com.stormmq.java.classfile.domain.attributes.annotations.TargetType.allValidTargetTypesForLocationIndexedByTargetTypeTag;
import static com.stormmq.java.classfile.domain.attributes.code.Code.MaximumCodeLength;
import static com.stormmq.java.classfile.domain.attributes.code.ExceptionCode.EmptyExceptionCodes;
import static com.stormmq.java.classfile.domain.attributes.code.stackMapFrames.verificationTypes.FixedVerificationType.*;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.InnerTypeAccessFlags.*;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.ParameterAccessFlags.*;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.attributesParsers.Attributes.*;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.FieldSignatureParser.parseFieldSignature;
import static com.stormmq.string.Formatting.format;

public final class AttributeParserMappings
{
	@NotNull private static final AttributeLocation[] All = {Type, Field, Method, AttributeLocation.Code};
	@NotNull private static final AttributeLocation[] AllButCode = {Type, Field, Method};
	@NotNull private static final AttributeLocation[] OnlyType = {Type};
	@NotNull private static final AttributeLocation[] OnlyField = {Field};
	@NotNull private static final AttributeLocation[] OnlyMethod = {Method};
	@NotNull private static final AttributeLocation[] OnlyCode = {AttributeLocation.Code};
	private static final int NumberOfKnownAnnotations = 12;

	@NotNull private final JavaClassFileVersion javaClassFileVersion;
	@NotNull private final AttributeLocation attributeLocation;
	@NotNull private final Map<String, AttributeParser> map;
	@NotNull private final Set<String> notValidForThisVersion;
	@NotNull private final Set<String> notValidForThisLocation;
	@Nullable private final AttributesParser codeAttributesParser;

	public AttributeParserMappings(@NotNull final JavaClassFileVersion javaClassFileVersion, @NotNull final AttributeLocation attributeLocation)
	{
		this.javaClassFileVersion = javaClassFileVersion;
		this.attributeLocation = attributeLocation;
		map = new HashMap<>(NumberOfKnownAnnotations);
		notValidForThisVersion = new HashSet<>(NumberOfKnownAnnotations);
		notValidForThisLocation = new HashSet<>(NumberOfKnownAnnotations);
		codeAttributesParser = attributeLocation == AttributeLocation.Code ? null : new AttributesParser(new AttributeParserMappings(javaClassFileVersion, AttributeLocation.Code));

		final TargetType[] targetTypesForLocation = allValidTargetTypesForLocationIndexedByTargetTypeTag(attributeLocation);

		mapping(AnnotationDefault, Java5, OnlyMethod, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> javaClassFileReader.parseAnnotationElementValue());

		tableArrayMapping(BootstrapMethods, Java7, OnlyType, BootstrapMethod[]::new, (javaClassFileReader) ->
		{
			final MethodHandle methodHandle = javaClassFileReader.readMethodHandle("method handle reference");
			final BootstrapMethodArgument[] bootstrapMethodArguments = javaClassFileReader.parseTableAsArrayWith16BitLength(BootstrapMethodArgument[]::new, EmptyBootstrapMethodArgumentConstants, () -> javaClassFileReader.readBootstrapMethodArgument("bootstrap method argument reference"));

			return new BootstrapMethod(methodHandle, bootstrapMethodArguments);
		});

		mapping(Attributes.Code, Java1_0_2, OnlyMethod, (attributeLengthUnsigned32BitInteger, javaClassFileReader) ->
		{
			final char maximumStack = javaClassFileReader.readBigEndianUnsigned16BitInteger("maximum stack");
			final char maximumLocals = javaClassFileReader.readBigEndianUnsigned16BitInteger("maximum locals");
			final long codeLength = javaClassFileReader.readBigEndianUnsigned32BitInteger("code length");
			if (codeLength == 0L)
			{
				throw new InvalidJavaClassFileException("code length can not be zero bytes");
			}
			if (codeLength > MaximumCodeLength)
			{
				throw new InvalidJavaClassFileException("code length can not exceed 65,535 bytes");
			}
			final ByteBuffer code = javaClassFileReader.readBytes("code", codeLength);
			final ExceptionCode[] exceptionCode = javaClassFileReader.parseTableAsArrayWith16BitLength(ExceptionCode[]::new, EmptyExceptionCodes, () ->
			{
				final char startProgramCount = javaClassFileReader.readBigEndianUnsigned16BitInteger("exception start program count");
				final char endProgramCount = javaClassFileReader.readBigEndianUnsigned16BitInteger("exception end program count");
				final char handlerProgramCount = javaClassFileReader.readBigEndianUnsigned16BitInteger("handler program count");
				@Nullable final InternalTypeName catchInternalTypeName = javaClassFileReader.readNullableInternalTypeName("catch type");
				return new ExceptionCode(startProgramCount, endProgramCount, handlerProgramCount, catchInternalTypeName);
			});
			assert codeAttributesParser != null;
			final Attributes attributes = codeAttributesParser.parseAttributes(javaClassFileReader);

			final TypeAnnotation[] visibleTypeAnnotations = attributes.runtimeVisibleTypeAnnotations();
			final TypeAnnotation[] invisibleTypeAnnotations = attributes.runtimeInvisibleTypeAnnotations();
			final Map<Character, Set<Character>> programCounterToLineNumberEntryMap = attributes.lineNumberEntries(codeLength);
			final LocalVariables localVariables = attributes.localVariableWithSignatures(codeLength, maximumLocals);
			final StackMapFrame[] stackMapFrames = attributes.stackMapFrames();
			final UnknownAttributes unknownAttributes = attributes.unknownAttributes();

			return new Code(javaClassFileReader.constantPool(), maximumStack, maximumLocals, codeLength, code, exceptionCode, programCounterToLineNumberEntryMap, localVariables, stackMapFrames, unknownAttributes, visibleTypeAnnotations, invisibleTypeAnnotations, javaClassFileVersion.isJava7OrLater());
		});

		mapping(ConstantValue, Java1_0_2, OnlyField, (attributeLength, javaClassFileReader) ->
		{
			validateAttributeLength(attributeLength, "ConstantValue attribute must have a length of exactly 2", 2L);
			return javaClassFileReader.readFieldConstant("field constant value");
		});

		mapping(Attributes.Deprecated, Java1_1, AllButCode, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> parseFixedAttribute(attributeLengthUnsigned32BitInteger, Attributes.Deprecated));

		mapping(EnclosingMethod, Java5, OnlyType, (attributeLength, javaClassFileReader) ->
		{
			//noinspection MagicNumber
			validateAttributeLength(attributeLength, "EnclosingMethod attribute must have a length of exactly", 4L);

			final KnownReferenceTypeName enclosingTypeName = javaClassFileReader.readKnownReferenceTypeName("enclosing method type");
			@Nullable final NameAndTypeReferenceIndexConstant nameAndTypeReferenceIndexConstant = javaClassFileReader.readNullableNameAndType("enclosing method name and type");

			if (nameAndTypeReferenceIndexConstant == null)
			{
				return new OutsideEnclosingMethod(enclosingTypeName);
			}

			return new InsideEnclosingMethod(enclosingTypeName, nameAndTypeReferenceIndexConstant.methodName(), nameAndTypeReferenceIndexConstant.methodDescriptor());
		});

		tableSetMapping(Exceptions, Java1_0_2, OnlyMethod, (javaClassFileReader, set) ->
		{
			try
			{
				addOnce(set, javaClassFileReader.readKnownReferenceTypeName("method's exception"));
			}
			catch (final AddOnceViolationException e)
			{
				throw new InvalidJavaClassFileException("Same exception defined more than once", e);
			}
		});

		tableArrayMapping(InnerClasses, Java1_1, OnlyType, InnerTypeInformation[]::new, (javaClassFileReader) ->
		{
			final KnownReferenceTypeName innerTypeName = javaClassFileReader.readKnownReferenceTypeName("inner class");
			@Nullable final KnownReferenceTypeName outerTypeName = javaClassFileReader.readNullableKnownReferenceTypeName("potentially null internal type name");
			@Nullable final String innerSimpleName = javaClassFileReader.readNullableModifiedUtf8String("potentially null inner simple name");
			final char innerTypeAccessFlags = javaClassFileReader.readAccessFlags(InnerTypeAccessFlagsValidityMask);

			final boolean isInnerTypeSynthetic = isInnerTypeSynthetic(innerTypeAccessFlags);
			final TypeKind innerTypeKind = innerTypeKind(innerTypeAccessFlags);
			final Visibility innerTypeVisibility = innerTypeVisibility(innerTypeAccessFlags);
			final Completeness innerTypeCompleteness = innerTypeCompleteness(innerTypeAccessFlags);

			return new InnerTypeInformation(innerTypeName, outerTypeName, innerSimpleName, isInnerTypeSynthetic, innerTypeKind, innerTypeVisibility, innerTypeCompleteness);
		});

		tableArrayMapping(LineNumberTable, Java1_0_2, OnlyCode, LineNumberEntry[]::new, (javaClassFileReader) ->
		{
			final char startProgramCounter = javaClassFileReader.readBigEndianUnsigned16BitInteger("line number start program counter");
			final char lineNumber = javaClassFileReader.readBigEndianUnsigned16BitInteger("line number");

			return new LineNumberEntry(startProgramCounter, lineNumber);
		});

		tableArrayMapping(LocalVariableTable, Java1_0_2, OnlyCode, DescriptorLocalVariable[]::new, (javaClassFileReader) ->
		{
			final char startProgramCount = javaClassFileReader.readBigEndianUnsigned16BitInteger("local variable start program counter");
			final char localVariableLength = javaClassFileReader.readBigEndianUnsigned16BitInteger("local variable length");
			@NotNull final FieldName localVariableName = javaClassFileReader.readFieldName("local variable name");
			@NotNull final FieldDescriptor localVariableDescriptor = javaClassFileReader.readFieldDescriptor("local variable descriptor");
			final char localVariableIndex = javaClassFileReader.readBigEndianUnsigned16BitInteger("local variable index");

			return new DescriptorLocalVariable(startProgramCount, localVariableLength, localVariableName, localVariableDescriptor, localVariableIndex);
		});

		tableArrayMapping(LocalVariableTypeTable, Java5, OnlyCode, SignatureLocalVariable[]::new, (javaClassFileReader) ->
		{
			final char startProgramCount = javaClassFileReader.readBigEndianUnsigned16BitInteger("local variable type start program counter");
			final char localVariableLength = javaClassFileReader.readBigEndianUnsigned16BitInteger("local variable type length");
			@NotNull final FieldName localVariableName = javaClassFileReader.readFieldName("local variable type name");
			@NotNull final Signature signature = parseFieldSignature(javaClassFileReader.readModifiedUtf8String("local variable type signature"));
			final char localVariableIndex = javaClassFileReader.readBigEndianUnsigned16BitInteger("local variable type index");

			return new SignatureLocalVariable(startProgramCount, localVariableLength, localVariableName, signature, localVariableIndex);
		});

		tableArrayMapping(MethodParameters, Java8, OnlyMethod, MethodParameter[]::new, (javaClassFileReader) ->
		{
			@Nullable final String parameterName = javaClassFileReader.readNullableModifiedUtf8String("method parameter name");
			final char accessFlags = javaClassFileReader.readAccessFlags(ParameterAccessFlagsValidityMask);
			final boolean isFinal = isParameterFinal(accessFlags);
			final boolean isSynthetic = isParameterSynthetic(accessFlags);
			final boolean isMandatory = isParameterMandatory(accessFlags);

			return new MethodParameter(parameterName, isFinal, isSynthetic, isMandatory);
		});

		mapping(RuntimeVisibleAnnotations, Java5, AllButCode, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> javaClassFileReader.parseAnnotations());

		mapping(RuntimeVisibleParameterAnnotations, Java5, OnlyMethod, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> javaClassFileReader.parseParameterAnnotations());

		mapping(RuntimeVisibleTypeAnnotations, Java8, All, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> javaClassFileReader.parseTypeAnnotations(targetTypesForLocation));

		mapping(RuntimeInvisibleAnnotations, Java5, AllButCode, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> javaClassFileReader.parseAnnotations());

		mapping(RuntimeInvisibleParameterAnnotations, Java5, OnlyMethod, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> javaClassFileReader.parseParameterAnnotations());

		mapping(RuntimeInvisibleTypeAnnotations, Java8, All, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> javaClassFileReader.parseTypeAnnotations(targetTypesForLocation));

		mapping(Signature, Java5, AllButCode, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> parseFieldSignature(stringAttributeValue(attributeLengthUnsigned32BitInteger, javaClassFileReader)));

		tableArrayMapping(StackMapTable, Java6, OnlyCode, StackMapFrame[]::new, new StackMapFrameTableArrayParser());

		mapping(SourceDebugExtension, Java5, OnlyType, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> javaClassFileReader.readModifiedUtf8String("debug extension", attributeLengthUnsigned32BitInteger));

		mapping(SourceFile, Java1_0_2, OnlyType, AttributeParserMappings::stringAttributeValue);

		mapping(Synthetic, Java1_1, AllButCode, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> parseFixedAttribute(attributeLengthUnsigned32BitInteger, Synthetic));
	}

	@NotNull
	public Object parseAttribute(@NotNull final String attributeName, final long attributeLengthUnsigned32BitInteger, @NotNull final ConstantPoolJavaClassFileReader javaClassFileReader) throws InvalidJavaClassFileException
	{
		final long positionBefore = javaClassFileReader.bytesReadSoFar();

		final ExceptionSupplier<Object, InvalidJavaClassFileException> ifAbsent = () ->
		{
			if (notValidForThisVersion.contains(attributeName))
			{
				throw new InvalidJavaClassFileException(format("The attribute '%1$s' is not valid for this version of Java ('%2$s')", attributeName, javaClassFileVersion));
			}

			if (notValidForThisLocation.contains(attributeName))
			{
				throw new InvalidJavaClassFileException(format("The attribute '%1$s' is not valid for this location ('%2$s')", attributeName, attributeLocation));
			}

			return parseUnknownAttribute(attributeName, attributeLengthUnsigned32BitInteger, javaClassFileReader);
		};

		final Object attributeData = useMapValueExceptionally(map, attributeName, ifAbsent, attributeParser -> attributeParser.parse(attributeLengthUnsigned32BitInteger, javaClassFileReader));

		validateReadAttributeCorrectly(attributeName, attributeLengthUnsigned32BitInteger, javaClassFileReader, positionBefore);

		return attributeData;
	}

	@FunctionalInterface
	private interface TableArrayParser<T>
	{
		@NotNull
		T parse(@NotNull final ConstantPoolJavaClassFileReader javaClassFileReader) throws InvalidJavaClassFileException;
	}

	@FunctionalInterface
	private interface TableSetParser<T>
	{
		void parse(@NotNull final ConstantPoolJavaClassFileReader javaClassFileReader, @NotNull final Set<T> set) throws InvalidJavaClassFileException;
	}

	@FunctionalInterface
	private interface AttributeParser
	{
		@NotNull
		Object parse(final long attributeLengthUnsigned32BitInteger, @NotNull final ConstantPoolJavaClassFileReader javaClassFileReader) throws InvalidJavaClassFileException;
	}

	private <T> void tableSetMapping(@NotNull @NonNls final String attributeName, @NotNull final JavaClassFileVersion introduced, @NotNull final AttributeLocation[] attributeLocations, @NotNull final TableSetParser<T> parse)
	{
		mapping(attributeName, introduced, attributeLocations, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> javaClassFileReader.parseTableAsSetWith16BitLength((InvalidJavaClassFileExceptionBiIntConsumer<Set<T>>) (set, index) -> parse.parse(javaClassFileReader, set)));
	}

	private <T> void tableArrayMapping(@NotNull @NonNls final String attributeName, @NotNull final JavaClassFileVersion introduced, @NotNull final AttributeLocation[] attributeLocations, @NotNull final IntFunction<T[]> arrayCreator, @NotNull final TableArrayParser<T> tableArrayParser)
	{
		final T[] emptyArray = arrayCreator.apply(0);
		mapping(attributeName, introduced, attributeLocations, (attributeLengthUnsigned32BitInteger, javaClassFileReader) -> javaClassFileReader.parseTableAsArrayWith16BitLength(arrayCreator, emptyArray, () -> tableArrayParser.parse(javaClassFileReader)));
	}

	private void mapping(@NotNull @NonNls final String attributeName, @SuppressWarnings("TypeMayBeWeakened") @NotNull final JavaClassFileVersion introduced, @NotNull final AttributeLocation[] attributeLocations, @NotNull final AttributeParser attributeParser)
	{
		if (introduced.compareTo(javaClassFileVersion) > 0)
		{
			addOnce(notValidForThisVersion, attributeName);
			return;
		}

		if (hasLocation(attributeLocations, attributeLocation))
		{
			putOnce(map, attributeName, attributeParser);
		}
		else
		{
			addOnce(notValidForThisLocation, attributeName);
		}
	}

	private static void validateReadAttributeCorrectly(@NotNull final String attributeName, final long attributeLengthUnsigned32BitInteger, @NotNull final JavaClassFileReader javaClassFileReader, final long positionBefore) throws InvalidJavaClassFileException
	{
		final long positionAfter = javaClassFileReader.bytesReadSoFar();

		final long bytesActuallyRead = positionAfter - positionBefore;
		if (bytesActuallyRead != attributeLengthUnsigned32BitInteger)
		{
			throw new InvalidJavaClassFileException(format("Did not read the correct number '%1$s' of bytes (actually read '%2$s') for attribute '%3$s'", attributeLengthUnsigned32BitInteger, bytesActuallyRead, attributeName));
		}
	}

	@NotNull
	private static UnknownAttributeData parseUnknownAttribute(@NotNull final String attributeName, final long attributeLengthUnsigned32BitInteger, @NotNull final JavaClassFileReader javaClassFileReader) throws InvalidJavaClassFileException
	{
		final String what = format("unknown attribute '%1$s' of length '%2$s'", attributeName, attributeLengthUnsigned32BitInteger);
		return new UnknownAttributeData(javaClassFileReader.readBytes(what, attributeLengthUnsigned32BitInteger));
	}

	@NotNull
	private static String stringAttributeValue(final long attributeLengthUnsigned32BitInteger, @NotNull final ConstantPoolJavaClassFileReader javaClassFileReader) throws InvalidJavaClassFileException
	{
		validateAttributeLength(attributeLengthUnsigned32BitInteger, "Attribute must have a length of exactly 2", 2L);
		return javaClassFileReader.readModifiedUtf8String("attribute value reference");
	}

	@NotNull
	private static Object parseFixedAttribute(final long attributeLengthUnsigned32BitInteger, @NotNull final Object singleton) throws InvalidJavaClassFileException
	{
		validateAttributeLength(attributeLengthUnsigned32BitInteger, "Attribute must have a length of exactly 0", 0L);
		return singleton;
	}

	private static void validateAttributeLength(final long attributeLengthUnsigned32BitInteger, @NotNull @NonNls final String message, final long length) throws InvalidJavaClassFileException
	{
		if (attributeLengthUnsigned32BitInteger != length)
		{
			throw new InvalidJavaClassFileException(message);
		}
	}

	private static final class StackMapFrameTableArrayParser implements TableArrayParser<StackMapFrame>
	{
		@NotNull private static final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, StackMapFrame>[] FrameTypeParsers = initialise();

		@SuppressWarnings("MagicNumber")
		@NotNull
		private static InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, StackMapFrame>[] initialise()
		{
			final short length = 256;
			@SuppressWarnings("unchecked") final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, StackMapFrame>[] frameTypeParsers = new InvalidJavaClassFileExceptionFunction[length];
			for (short index = 0; index < length; index++)
			{
				final short frameType = index;
				final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, StackMapFrame> function;

				//noinspection IfStatementWithTooManyBranches
				if (frameType >= 0 && frameType <= 63)
				{
					function = constantPoolJavaClassFileReader -> new SameStackMapFrame(frameType);
				}
				else if (frameType >= 64 && frameType <= 127)
				{
					function = constantPoolJavaClassFileReader ->
					{
						final VerificationType verificationType = verificationType(constantPoolJavaClassFileReader);
						return new SameLocals1StackItemStackMapFrame(frameType, verificationType);
					};
				}
				else if (frameType >= 128 && frameType <= 246)
				{
					function = constantPoolJavaClassFileReader ->
					{
						throw new InvalidJavaClassFileException(format("Frame type '%1$s' is undefined", frameType));
					};
				}
				else if (frameType == 247)
				{
					function = constantPoolJavaClassFileReader ->
					{
						final char offsetDelta = offsetDelta(constantPoolJavaClassFileReader);
						final VerificationType verificationType = verificationType(constantPoolJavaClassFileReader);
						return new SameLocals1StackItemExtendedStackMapFrame(offsetDelta, verificationType);
					};
				}
				else if (frameType >= 248 && frameType <= 250)
				{
					function = constantPoolJavaClassFileReader ->
					{
						final char offsetDelta = offsetDelta(constantPoolJavaClassFileReader);
						return new ChopStackMapFrame(offsetDelta);
					};
				}
				else if (frameType == 251)
				{
					function = constantPoolJavaClassFileReader ->
					{
						final char offsetDelta = offsetDelta(constantPoolJavaClassFileReader);
						return new SameExtendedStackMapFrame(offsetDelta);
					};
				}
				else if (frameType >= 252 && frameType <= 254)
				{
					final int numberOfVerifications = frameType - 251;
					function = constantPoolJavaClassFileReader ->
					{
						final char offsetDelta = offsetDelta(constantPoolJavaClassFileReader);
						final VerificationType[] verificationTypes = getVerificationTypes(constantPoolJavaClassFileReader, numberOfVerifications);
						return new AppendStackMapFrame(offsetDelta, verificationTypes);
					};
				}
				else if (frameType == 255)
				{
					function = constantPoolJavaClassFileReader ->
					{
						final char offsetDelta = offsetDelta(constantPoolJavaClassFileReader);
						final VerificationType[] locals = getVerificationTypes(constantPoolJavaClassFileReader);
						final VerificationType[] stack = getVerificationTypes(constantPoolJavaClassFileReader);
						return new FullStackMapFrame(offsetDelta, locals, stack);
					};
				}
				else
				{
					throw new IllegalStateException("Impossible to get any other frameType");
				}

				frameTypeParsers[index] = function;
			}
			return frameTypeParsers;
		}

		@NotNull
		@Override
		public StackMapFrame parse(@NotNull final ConstantPoolJavaClassFileReader javaClassFileReader) throws InvalidJavaClassFileException
		{
			final short frameType = javaClassFileReader.readUnsigned8BitInteger("stack map frame type");
			return FrameTypeParsers[frameType].apply(javaClassFileReader);
		}

		@NotNull
		private static VerificationType[] getVerificationTypes(@NotNull final ConstantPoolJavaClassFileReader javaClassFileReader) throws InvalidJavaClassFileException
		{
			final char numberOfVerifications = javaClassFileReader.readBigEndianUnsigned16BitInteger("number of verifications");
			return getVerificationTypes(javaClassFileReader, numberOfVerifications);
		}

		@NotNull
		private static VerificationType[] getVerificationTypes(@NotNull final ConstantPoolJavaClassFileReader javaClassFileReader, final int numberOfVerifications) throws InvalidJavaClassFileException
		{
			final VerificationType[] verificationTypes = new VerificationType[numberOfVerifications];
			for (int index = 0; index < numberOfVerifications; index++)
			{
				verificationTypes[index] = verificationType(javaClassFileReader);
			}
			return verificationTypes;
		}

		private static char offsetDelta(@NotNull final JavaClassFileReader javaClassFileReader) throws InvalidJavaClassFileException
		{
			return javaClassFileReader.readBigEndianUnsigned16BitInteger("offset delta");
		}

		@NotNull
		private static VerificationType verificationType(@NotNull final ConstantPoolJavaClassFileReader javaClassFileReader) throws InvalidJavaClassFileException
		{
			final short verificationTag = javaClassFileReader.readUnsigned8BitInteger("verification tag");
			switch (verificationTag)
			{
				case 0:
					return Top;

				case 1:
					return Integer;

				case 2:
					return Float;

				case 3:
					return Double;

				case 4:
					return Long;

				case 5:
					return Null;

				case 6:
					return UninitializedThis;

				case 7:
					//noinspection SpellCheckingInspection
					return new ObjectVerificationType(javaClassFileReader.readInternalTypeName("cpool"));

				case 8:
					return new UninitializedVerificationType(javaClassFileReader.readBigEndianUnsigned16BitInteger("offset"));

				default:
					throw new InvalidJavaClassFileException(format("Unknown stack map frame tag '%1$s'", verificationTag));
			}
		}
	}
}
