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

package com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool;

import com.stormmq.functions.PutOnceViolationException;
import com.stormmq.java.classfile.domain.*;
import com.stormmq.java.classfile.domain.attributes.annotations.*;
import com.stormmq.java.classfile.domain.attributes.annotations.targetInformations.*;
import com.stormmq.java.classfile.domain.attributes.annotations.typePathElements.TypePathElement;
import com.stormmq.java.classfile.domain.attributes.code.constants.BootstrapMethodArgument;
import com.stormmq.java.classfile.domain.attributes.code.constants.RuntimeConstantPool;
import com.stormmq.java.classfile.domain.descriptors.FieldDescriptor;
import com.stormmq.java.classfile.domain.descriptors.MethodDescriptor;
import com.stormmq.java.classfile.domain.fieldConstants.FieldConstant;
import com.stormmq.java.classfile.domain.names.FieldName;
import com.stormmq.java.classfile.domain.names.MethodName;
import com.stormmq.java.classfile.parser.JavaClassFileReader;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.NameAndTypeReferenceIndexConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.functions.*;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.IntFunction;

import static com.stormmq.functions.MapHelper.getGuarded;
import static com.stormmq.functions.MapHelper.putOnce;
import static com.stormmq.java.classfile.domain.attributes.annotations.AnnotationValue.EmptyAnnotationValues;
import static com.stormmq.java.classfile.domain.attributes.annotations.AnnotationValue.EmptyParameterAnnotations;
import static com.stormmq.java.classfile.domain.attributes.annotations.TargetInfoItem.*;
import static com.stormmq.java.classfile.domain.attributes.annotations.TypeAnnotation.EmptyTypeAnnotations;
import static com.stormmq.java.classfile.domain.attributes.annotations.targetInformations.CatchTargetInformation.catches;
import static com.stormmq.java.classfile.domain.attributes.annotations.targetInformations.ExtendsInterfaceTypeTargetInformation.extendsInterface;
import static com.stormmq.java.classfile.domain.attributes.annotations.targetInformations.FormalParameterTargetInformation.formalParameterTargetInformation;
import static com.stormmq.java.classfile.domain.attributes.annotations.targetInformations.LocalVariableTargetInformation.EmptyLocalVariableTargetInformation;
import static com.stormmq.java.classfile.domain.attributes.annotations.targetInformations.OffsetTargetInformation.offset;
import static com.stormmq.java.classfile.domain.attributes.annotations.targetInformations.SuperTypeTargetInformation.SuperType;
import static com.stormmq.java.classfile.domain.attributes.annotations.targetInformations.EmptyTargetInformation.EmptyTarget;
import static com.stormmq.java.classfile.domain.attributes.annotations.targetInformations.ThrowsTypeTargetInformation.throwsType;
import static com.stormmq.java.classfile.domain.attributes.annotations.targetInformations.TypeParameterTargetInformation.typeParameter;
import static com.stormmq.java.classfile.domain.attributes.annotations.typePathElements.AnnotationIsOnATypeArgumentOfAParameterizedTypeTypePathElement.annotationIsOnATypeArgumentOfAParameterizedType;
import static com.stormmq.java.classfile.domain.attributes.annotations.typePathElements.FixedTypePathElement.AnnotationIsDeeperInANestedType;
import static com.stormmq.java.classfile.domain.attributes.annotations.typePathElements.FixedTypePathElement.AnnotationIsDeeperInAnArrayType;
import static com.stormmq.java.classfile.domain.attributes.annotations.typePathElements.FixedTypePathElement.AnnotationIsOnTheBoundOfAWildcardTypeArgumentOfAParameterizedType;
import static com.stormmq.java.classfile.domain.attributes.annotations.typePathElements.TypePathElement.*;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ClassLikeTypeDescriptorParser.processClassLikeDescriptor;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex.referenceIndexToConstantPoolIndex;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.FieldDescriptorParser.parseFieldDescriptor;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.MethodDescriptorParser.parseMethodDescriptor;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.NameAndTypeReferenceIndexConstant.parseFieldName;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.NameAndTypeReferenceIndexConstant.parseMethodName;
import static com.stormmq.string.Formatting.format;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

public final class ConstantPoolJavaClassFileReader implements JavaClassFileReader
{
	@NotNull private static final Object[] EmptyArrayValues = new Object[0];
	@NotNull private static final Map<TargetInfoItem, InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation>> TargetInfoItemsParsingMap = targetInfoItemsParsingMap();

	@NotNull
	private static Map<TargetInfoItem, InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation>> targetInfoItemsParsingMap()
	{
		final Map<TargetInfoItem, InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation>> targetInfoItemsParsingMap = new EnumMap<>(TargetInfoItem.class);

		final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation> value9 = constantPoolJavaClassFileReader -> typeParameter(constantPoolJavaClassFileReader.readUnsigned8BitInteger("type parameter target"));
		putOnce(targetInfoItemsParsingMap, type_parameter_target, value9);

		final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation> value8 = constantPoolJavaClassFileReader ->
		{
			final char index = constantPoolJavaClassFileReader.readBigEndianUnsigned16BitInteger("super type index");
			if (index == 0)
			{
				return SuperType;
			}
			return extendsInterface(index);
		};
		putOnce(targetInfoItemsParsingMap, supertype_target, value8);

		final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation> value7 = constantPoolJavaClassFileReader -> new TypeParameterBoundTargetInformation(constantPoolJavaClassFileReader.readUnsigned8BitInteger("type parameter index"), constantPoolJavaClassFileReader.readUnsigned8BitInteger("bound index"));
		putOnce(targetInfoItemsParsingMap, type_parameter_bound_target, value7);

		final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation> value6 = constantPoolJavaClassFileReader -> EmptyTarget;
		putOnce(targetInfoItemsParsingMap, empty_target, value6);

		final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation> value5 = constantPoolJavaClassFileReader -> formalParameterTargetInformation(constantPoolJavaClassFileReader.readUnsigned8BitInteger("formal parameter index"));
		putOnce(targetInfoItemsParsingMap, formal_parameter_target, value5);

		final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation> value4 = constantPoolJavaClassFileReader -> throwsType(constantPoolJavaClassFileReader.readBigEndianUnsigned16BitInteger("throws type index"));
		putOnce(targetInfoItemsParsingMap, throws_target, value4);

		final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation> value3 = constantPoolJavaClassFileReader ->
		{
			final char tableLength = constantPoolJavaClassFileReader.readBigEndianUnsigned16BitInteger("table length");

			if (tableLength == 0)
			{
				return EmptyLocalVariableTargetInformation;
			}

			final LocalVariableTargetInformationItem[] localVariableTargetInformationItems = new LocalVariableTargetInformationItem[tableLength];
			for(char index = 0; index < tableLength; index++)
			{
				final char startProgramCount = constantPoolJavaClassFileReader.readBigEndianUnsigned16BitInteger("start program count");
				final char length = constantPoolJavaClassFileReader.readBigEndianUnsigned16BitInteger("length");
				final char indexX = constantPoolJavaClassFileReader.readBigEndianUnsigned16BitInteger("index");
				localVariableTargetInformationItems[index] = new LocalVariableTargetInformationItem(startProgramCount, length, indexX);
			}

			return new LocalVariableTargetInformation(localVariableTargetInformationItems);
		};
		putOnce(targetInfoItemsParsingMap, localvar_target, value3);

		final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation> value2 = constantPoolJavaClassFileReader -> catches(constantPoolJavaClassFileReader.readBigEndianUnsigned16BitInteger("catches type index"));
		putOnce(targetInfoItemsParsingMap, catch_target, value2);

		final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation> value1 = constantPoolJavaClassFileReader -> offset(constantPoolJavaClassFileReader.readBigEndianUnsigned16BitInteger("offset type index"));
		putOnce(targetInfoItemsParsingMap, offset_target, value1);

		final InvalidJavaClassFileExceptionFunction<ConstantPoolJavaClassFileReader, TargetInformation> value = constantPoolJavaClassFileReader -> new TypeArgumentOffsetTargetInformation(constantPoolJavaClassFileReader.readBigEndianUnsigned16BitInteger("type argument offset type index"), constantPoolJavaClassFileReader.readUnsigned8BitInteger("type argument index"));
		putOnce(targetInfoItemsParsingMap, type_argument_target, value);


		for (final TargetInfoItem targetInfoItem : values())
		{
			if (!targetInfoItemsParsingMap.containsKey(targetInfoItem))
			{
				throw new IllegalStateException("A targetInfoItem enum constant does not exist as a key in this map - has the code been modified in TargetInfoItem ?");
			}
		}

		return targetInfoItemsParsingMap;
	}

	@NotNull private final JavaClassFileReader delegate;
	@NotNull private final ConstantPool constantPool;
	@NotNull private final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, String> retrieveModifiedUtf8String;
	@NotNull private final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, InternalTypeName> retrieveInternalTypeName;
	@NotNull private final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, MethodHandle> retrieveMethodHandle;
	@NotNull private final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, FieldConstant> retrieveFieldConstant;
	@NotNull private final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, BootstrapMethodArgument> retrieveBootstrapMethodArgument;
	@NotNull private final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, NameAndTypeReferenceIndexConstant> retrieveNameAndTypeReference;
	@NotNull private final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, Integer> retrieveInteger;
	@NotNull private final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, Float> retrieveFloat;
	@NotNull private final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, Long> retrieveLong;
	@NotNull private final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, RawDouble> retrieveDouble;

	public ConstantPoolJavaClassFileReader(@NotNull final JavaClassFileReader delegate, @NotNull final ConstantPool constantPool)
	{
		this.delegate = delegate;
		this.constantPool = constantPool;

		retrieveInternalTypeName = constantPool::retrieveInternalTypeName;
		retrieveModifiedUtf8String = constantPool::retrieveModifiedUtf8String;
		retrieveMethodHandle = constantPool::retrieveMethodHandle;
		retrieveFieldConstant = constantPool::retrieveFieldConstant;
		retrieveBootstrapMethodArgument = constantPool::retrieveBootstrapMethodArgument;
		retrieveNameAndTypeReference = constantPool::retrieveNameAndTypeReference;
		retrieveInteger = constantPool::retrieveInteger;
		retrieveFloat = constantPool::retrieveFloat;
		retrieveLong = constantPool::retrieveLong;
		retrieveDouble = constantPool::retrieveDouble;
	}

	@Override
	public long bytesReadSoFar()
	{
		return delegate.bytesReadSoFar();
	}

	@NotNull
	@Override
	public String readModifiedUtf8StringWithPrefixedBigEndianUnsigned16BitLength(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return delegate.readModifiedUtf8StringWithPrefixedBigEndianUnsigned16BitLength(what);
	}

	@Override
	public float readBigEndianFloat(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return delegate.readBigEndianFloat(what);
	}

	@Override
	public short readUnsigned8BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return delegate.readUnsigned8BitInteger(what);
	}

	@Override
	public short readBigEndianSigned16BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return delegate.readBigEndianSigned16BitInteger(what);
	}

	@Override
	public char readBigEndianUnsigned16BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return delegate.readBigEndianUnsigned16BitInteger(what);
	}

	@Override
	public int readBigEndianSigned32BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return delegate.readBigEndianSigned32BitInteger(what);
	}

	@Override
	public long readBigEndianUnsigned32BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return delegate.readBigEndianUnsigned32BitInteger(what);
	}

	@Override
	public long readBigEndianSigned64BitInteger(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return delegate.readBigEndianSigned64BitInteger(what);
	}

	@Override
	public long readBigEndianRawDouble(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return delegate.readBigEndianRawDouble(what);
	}

	@NotNull
	@Override
	public ByteBuffer readBytes(@NotNull @NonNls final String what, final long length) throws InvalidJavaClassFileException
	{
		return delegate.readBytes(what, length);
	}

	@NotNull
	@Override
	public String readModifiedUtf8String(@NotNull @NonNls final String what, final long length) throws InvalidJavaClassFileException
	{
		return delegate.readModifiedUtf8String(what, length);
	}

	public char readAccessFlags(final int validityMask) throws InvalidJavaClassFileException
	{
		final char accessFlags = readBigEndianUnsigned16BitInteger("access flags");
		if ((accessFlags & validityMask) != 0)
		{
			throw new InvalidJavaClassFileException("Access flags contain invalid flags set");
		}
		return accessFlags;
	}

	@NotNull
	public AnnotationValue[] parseAnnotations() throws InvalidJavaClassFileException
	{
		return parseTableAsArrayWith16BitLength(AnnotationValue[]::new, EmptyAnnotationValues, this::parseAnnotationValue);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	public TypeAnnotation[] parseTypeAnnotations(@NotNull final TargetType[] targetTypesForLocation) throws InvalidJavaClassFileException
	{
		return parseTableAsArrayWith16BitLength(TypeAnnotation[]::new, EmptyTypeAnnotations, () ->
		{
			final TargetType targetType = targetType(targetTypesForLocation);
			final TargetInformation targetInformation = getGuarded(TargetInfoItemsParsingMap, targetType.targetInfoItem).apply(this);
			final TypePathElement[] typePath = parseTargetPath();
			final AnnotationValue annotationValue = parseAnnotationValue();

			return new TypeAnnotation(targetType, targetInformation, typePath, annotationValue);
		});
	}

	@NotNull
	public AnnotationValue[][] parseParameterAnnotations() throws InvalidJavaClassFileException
	{
		return parseTableAsArrayWith8BitLength(AnnotationValue[][]::new, EmptyParameterAnnotations, this::parseAnnotations);
	}

	@NotNull
	private Map<MethodName, Object> parseAnnotationElementValuePairs() throws InvalidJavaClassFileException
	{
		return parseTableAsMapWith16BitLength((table, index) ->
		{
			final MethodName elementName = readMethodName("element name");
			final Object elementValue = parseAnnotationElementValue();
			try
			{
				putOnce(table, elementName, elementValue);
			}
			catch (final PutOnceViolationException e)
			{
				throw new InvalidJavaClassFileException(format("Duplicate annotation element key '%1$s'", elementName), e);
			}
		});
	}

	@NotNull
	@NonNls
	private TypePathElement[] parseTargetPath() throws InvalidJavaClassFileException
	{
		return parseTableAsArrayWith8BitLength(TypePathElement[]::new, EmptyTypePathElements, () ->
		{
			final short typePathKind = readUnsigned8BitInteger("type path kind");
			final short typePathArgumentIndex = readUnsigned8BitInteger("type path argument index");

			if (typePathArgumentIndex != 0 && typePathKind != 3)
			{
				throw new InvalidJavaClassFileException("typePathArgumentIndex must be zero unless typePathKind is 3");
			}

			switch(typePathKind)
			{
				case 0:
					return AnnotationIsDeeperInAnArrayType;

				case 1:
					return AnnotationIsDeeperInANestedType;

				case 2:
					return AnnotationIsOnTheBoundOfAWildcardTypeArgumentOfAParameterizedType;

				case 3:
					return annotationIsOnATypeArgumentOfAParameterizedType(typePathArgumentIndex);

				default:
					throw new InvalidJavaClassFileException(format("The type path kind '%1$s' is invalid", typePathKind));
			}
		});
	}

	@NotNull
	private TargetType targetType(@NotNull final TargetType[] targetTypesForLocation) throws InvalidJavaClassFileException
	{
		final short targetTypeTag = readUnsigned8BitInteger("target type");
		@Nullable final TargetType targetType = targetTypesForLocation[targetTypeTag];
		if (targetType == null)
		{
			throw new InvalidJavaClassFileException(format("No known target type for tag '0x%1$02X' at this parse location", targetTypeTag));
		}
		return targetType;
	}

	@NotNull
	public Object parseAnnotationElementValue() throws InvalidJavaClassFileException
	{
		// element_value
		final char tag = (char) readUnsigned8BitInteger("annotation default tag value");
		switch (tag)
		{
			case 'B':
				//noinspection NumericCastThatLosesPrecision
				return (byte) readInteger("annotation default byte");

			case 'C':
				return (char) readInteger("annotation default char");

			case 'D':
				return readDouble("annotation default double");

			case 'F':
				return readFloat("annotation default float");

			case 'I':
				return readInteger("annotation default int");

			case 'J':
				return readLong("annotation default long");

			case 'S':
				//noinspection NumericCastThatLosesPrecision
				return (short) readInteger("annotation default short");

			case 'Z':
				return readInteger("annotation default boolean") != 0;

			case 's':
				return readModifiedUtf8String("annotation default string");

			case 'e':
				final String rawInternalTypeNameForConstant = readModifiedUtf8String("annotation default enum internal type name");
				final InternalTypeName internalTypeNameForConstant = processClassLikeDescriptor(rawInternalTypeNameForConstant);
				final KnownReferenceTypeName enumTypeName;
				try
				{
					enumTypeName = internalTypeNameForConstant.toKnownReferenceTypeName();
				}
				catch (final InvalidInternalTypeNameException e)
				{
					throw new InvalidJavaClassFileException("Invalid Enum constant's class", e);
				}
				final FieldName enumConstantName = readFieldName("annotation default enum simple annotation default name");
				return new EnumConstantAnnotationDefaultValue(enumTypeName, enumConstantName);

			case 'c':
				final String rawInternalTypeNameForClass = readModifiedUtf8String("annotation default class");
				final InternalTypeName internalTypeNameForClass = processClassLikeDescriptor(rawInternalTypeNameForClass);
				return internalTypeNameForClass.typeName();

			case '@':
				return parseAnnotationValue();

			case '[':
				final char numberOfArrayValues = readBigEndianUnsigned16BitInteger("number of annotation default array values");
				if (numberOfArrayValues == 0)
				{
					return EmptyArrayValues;
				}
				final Object[] arrayValues = new Object[numberOfArrayValues];
				for (char index = 0; index < numberOfArrayValues; index++)
				{
					arrayValues[index] = parseAnnotationElementValue();
				}
				return arrayValues;

			default:
				throw new InvalidJavaClassFileException(format("Unknown annotation default tag '%1$s'", tag));
		}
	}

	@NotNull
	private AnnotationValue parseAnnotationValue() throws InvalidJavaClassFileException
	{
		final FieldDescriptor typeIndexFieldDescriptor = readFieldDescriptor("annotation type index");
		final Map<MethodName, Object> fieldValues = parseAnnotationElementValuePairs();

		return new AnnotationValue(typeIndexFieldDescriptor, fieldValues);
	}

	@NotNull
	public ConstantPoolIndex readClassReferenceIndex(@NotNull final ConstantPoolIndex ourConstantPoolIndex) throws InvalidJavaClassFileException
	{
		return readReferenceIndex(ourConstantPoolIndex, "Class Reference");
	}

	@NotNull
	public ConstantPoolIndex readStringReferenceIndex(@NotNull final ConstantPoolIndex ourConstantPoolIndex) throws InvalidJavaClassFileException
	{
		return readReferenceIndex(ourConstantPoolIndex, "String reference");
	}

	@NotNull
	public ConstantPoolIndex readNameReferenceIndex(@NotNull final ConstantPoolIndex ourConstantPoolIndex) throws InvalidJavaClassFileException
	{
		return readReferenceIndex(ourConstantPoolIndex, "Name Reference");
	}

	@NotNull
	public ConstantPoolIndex readTypeDescriptorReferenceIndex(@NotNull final ConstantPoolIndex ourConstantPoolIndex) throws InvalidJavaClassFileException
	{
		return readReferenceIndex(ourConstantPoolIndex, "Type Descriptor");
	}

	@NotNull
	public ConstantPoolIndex readNameAndTypeDescriptorReferenceIndex(@NotNull final ConstantPoolIndex ourConstantPoolIndex) throws InvalidJavaClassFileException
	{
		return readReferenceIndex(ourConstantPoolIndex, "Name and Type Descriptor Reference");
	}

	@NotNull
	public ConstantPoolIndex readMethodHandleReferenceIndex(@NotNull final ConstantPoolIndex ourConstantPoolIndex) throws InvalidJavaClassFileException
	{
		return readReferenceIndex(ourConstantPoolIndex, "Method Reference");
	}

	@NotNull
	private ConstantPoolIndex readReferenceIndex(@NotNull final ConstantPoolIndex ourConstantPoolIndex, @NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		final ConstantPoolIndex referenceIndex = readReferenceIndex(what);
		if (ourConstantPoolIndex.equals(referenceIndex))
		{
			throw new InvalidJavaClassFileException(format("A %1$s of '%2$s' can not point to itself", what, referenceIndex));
		}
		return referenceIndex;
	}

	@NotNull
	@NonNls
	public FieldName readFieldName(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return parseFieldName(readModifiedUtf8String(what));
	}

	@NotNull
	@NonNls
	public MethodName readMethodName(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return parseMethodName(readModifiedUtf8String(what));
	}

	@NotNull
	public FieldDescriptor readFieldDescriptor(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return parseFieldDescriptor(readModifiedUtf8String(what));
	}

	@NotNull
	public MethodDescriptor readMethodDescriptor(@NotNull @NonNls final String what, final boolean returnTypeMustBeVoid) throws InvalidJavaClassFileException
	{
		return parseMethodDescriptor(readModifiedUtf8String(what), returnTypeMustBeVoid);
	}

	@NonNls
	@NotNull
	public String readModifiedUtf8String(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		return readReferenceAndResolveConstant(what, retrieveModifiedUtf8String);
	}

	@NotNull
	public KnownReferenceTypeName readKnownReferenceTypeName(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		final InternalTypeName internalTypeName = readInternalTypeName(what);
		try
		{
			return internalTypeName.validateIsSuitableForType();
		}
		catch (final InvalidInternalTypeNameException e)
		{
			throw new InvalidJavaClassFileException("Invalid type name", e);
		}
	}

	@NotNull
	public InternalTypeName readInternalTypeName(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		return readReferenceAndResolveConstant(what, retrieveInternalTypeName);
	}

	@NotNull
	public MethodHandle readMethodHandle(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		return readReferenceAndResolveConstant(what, retrieveMethodHandle);
	}

	@NotNull
	public Object readFieldConstant(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		return readReferenceAndResolveConstant(what, retrieveFieldConstant);
	}

	@NotNull
	public BootstrapMethodArgument readBootstrapMethodArgument(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		return readReferenceAndResolveConstant(what, retrieveBootstrapMethodArgument);
	}

	private int readInteger(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		return readReferenceAndResolveConstant(what, retrieveInteger);
	}

	private float readFloat(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		return readReferenceAndResolveConstant(what, retrieveFloat);
	}

	private long readLong(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		return readReferenceAndResolveConstant(what, retrieveLong);
	}

	@NotNull
	private RawDouble readDouble(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		return readReferenceAndResolveConstant(what, retrieveDouble);
	}

	@Nullable
	public String readNullableModifiedUtf8String(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		return readPotentiallyNullReference(what, retrieveModifiedUtf8String);
	}

	@Nullable
	public KnownReferenceTypeName readNullableKnownReferenceTypeName(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		@Nullable final InternalTypeName internalTypeName = readNullableInternalTypeName(what);
		if (internalTypeName == null)
		{
			return null;
		}
		try
		{
			return internalTypeName.validateIsSuitableForType();
		}
		catch (final InvalidInternalTypeNameException e)
		{
			throw new InvalidJavaClassFileException("Not a valid KnownReferenceTypeName", e);
		}
	}

	@Nullable
	public InternalTypeName readNullableInternalTypeName(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		return readPotentiallyNullReference(what, retrieveInternalTypeName);
	}

	@Nullable
	public NameAndTypeReferenceIndexConstant readNullableNameAndType(@NonNls @NotNull final String what) throws InvalidJavaClassFileException
	{
		return readPotentiallyNullReference(what, retrieveNameAndTypeReference);
	}

	@NotNull
	private <V> V readReferenceAndResolveConstant(@NotNull @NonNls final String what, @NotNull final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, V> retrieve) throws InvalidJavaClassFileException
	{
		final ConstantPoolIndex referenceIndex = readReferenceIndex(what);
		return retrieve.apply(referenceIndex);
	}

	@NotNull
	private ConstantPoolIndex readReferenceIndex(@NotNull @NonNls final String what) throws InvalidJavaClassFileException
	{
		final char referenceIndex = readBigEndianUnsigned16BitInteger(what);
		return parseReference(what, referenceIndex);
	}

	@Nullable
	private <V> V readPotentiallyNullReference(@NotNull @NonNls final String what, @NotNull final InvalidJavaClassFileExceptionFunction<ConstantPoolIndex, V> retrieve) throws InvalidJavaClassFileException
	{
		final char referenceIndex = readBigEndianUnsigned16BitInteger(what);
		if (referenceIndex == 0)
		{
			return null;
		}
		final ConstantPoolIndex constantPoolIndex = parseReference(what, referenceIndex);
		return retrieve.apply(constantPoolIndex);
	}

	@NotNull
	private ConstantPoolIndex parseReference(@NotNull @NonNls final String what, final char referenceIndex) throws InvalidJavaClassFileException
	{
		final char constantPoolCount = constantPool.constantPoolCount();
		if (referenceIndex >= constantPoolCount)
		{
			throw new InvalidJavaClassFileException(format("A '%1$s' can not point to a value ('%2$s') outside of the constant pool (whose count is '%3$s')", what, (int) referenceIndex, (int) constantPoolCount));
		}
		return referenceIndexToConstantPoolIndex(referenceIndex, what);
	}

	@NotNull
	private <V> V[] parseTableAsArrayWith8BitLength(@NotNull final IntFunction<V[]> arrayCreator, @NotNull final V[] empty, @NotNull final InvalidJavaClassFileExceptionSupplier<V> parse) throws InvalidJavaClassFileException
	{
		return parseTableAsArray(arrayCreator, empty, parse, this::getTableLength8Bit);
	}

	@NotNull
	public <Value> Value[] parseTableAsArrayWith16BitLength(@NotNull final IntFunction<Value[]> arrayCreator, @NotNull final Value[] empty, @NotNull final InvalidJavaClassFileExceptionSupplier<Value> parse) throws InvalidJavaClassFileException
	{
		return parseTableAsArray(arrayCreator, empty, parse, this::getTableLength16Bit);
	}

	@NotNull
	public <Value> Set<Value> parseTableAsSetWith16BitLength(@NotNull final InvalidJavaClassFileExceptionBiIntConsumer<Set<Value>> parse) throws InvalidJavaClassFileException
	{
		return parseTableAsSet(parse, this::getTableLength16Bit);
	}

	@NotNull
	public <Key, Value> Map<Key, Value> parseTableAsMapWith16BitLength(@NotNull final InvalidJavaClassFileExceptionBiIntConsumer<Map<Key, Value>> parse) throws InvalidJavaClassFileException
	{
		return parseTableAsMap(parse, this::getTableLength16Bit);
	}

	@NotNull
	public RuntimeConstantPool constantPool()
	{
		return constantPool;
	}

	@NotNull
	private static <Value> Value[] parseTableAsArray(@NotNull final IntFunction<Value[]> arrayCreator, @NotNull final Value[] empty, @NotNull final InvalidJavaClassFileExceptionSupplier<Value> parse, @NotNull final InvalidJavaClassFileExceptionIntSupplier length) throws InvalidJavaClassFileException
	{
		return parseTable(arrayCreator, empty, length, (values, index) -> values[index] = parse.get());
	}

	@NotNull
	private static <Value> Set<Value> parseTableAsSet(@NotNull final InvalidJavaClassFileExceptionBiIntConsumer<Set<Value>> parse, @NotNull final InvalidJavaClassFileExceptionIntSupplier length) throws InvalidJavaClassFileException
	{
		return parseTable(LinkedHashSet::new, emptySet(), length, parse);
	}

	@NotNull
	private static <Key, Value> Map<Key, Value> parseTableAsMap(@NotNull final InvalidJavaClassFileExceptionBiIntConsumer<Map<Key, Value>> parse, @NotNull final InvalidJavaClassFileExceptionIntSupplier length) throws InvalidJavaClassFileException
	{
		return parseTable(LinkedHashMap::new, emptyMap(), length, parse);
	}

	@NotNull
	private static <Table> Table parseTable(@NotNull final IntFunction<Table> tableCreator, @NotNull final Table empty, @NotNull final InvalidJavaClassFileExceptionIntSupplier lengthParser, @NotNull final InvalidJavaClassFileExceptionBiIntConsumer<Table> tableUser) throws InvalidJavaClassFileException
	{
		final int length = lengthParser.getAsInt();

		if (length == 0)
		{
			return empty;
		}

		final Table table = tableCreator.apply(length);
		for (int index = 0; index < length; index++)
		{
			tableUser.accept(table, index);
		}

		return table;
	}

	private int getTableLength8Bit() throws InvalidJavaClassFileException
	{
		return readUnsigned8BitInteger("8-bit table length");
	}

	private int getTableLength16Bit() throws InvalidJavaClassFileException
	{
		return readBigEndianUnsigned16BitInteger("16-bit table length");
	}
}
