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

package com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constantParsers;

import com.stormmq.java.classfile.domain.JavaClassFileVersion;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPool;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolIndex;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.methodHandleConstants.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.miscellaneous.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.numbers.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.NameAndTypeReferenceIndexConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.Constant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolJavaClassFileReader;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.doubles.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.singles.*;
import com.stormmq.string.InvalidUtf16StringException;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

@FunctionalInterface
public interface ConstantParser
{
	@SuppressWarnings("MagicNumber")
	@NotNull
	static ConstantParser[] constantParsers(@NotNull final JavaClassFileVersion javaClassFileVersion)
	{
		final boolean isJava7OrLater = javaClassFileVersion.isJava7OrLater();
		final boolean isLessThanJava8 = javaClassFileVersion.isLessThanJava8();

		final int length = 256;

		final ConstantParser[] constantParsers = new ConstantParser[length];

		for(int index = 0; index < length; index++)
		{
			//noinspection NumericCastThatLosesPrecision
			constantParsers[index] = new UnsupportedConstantParser((short) index);
		}

		constantParsers[1]  = (constantPoolIndex, javaClassFileReader, constantPool) ->
		{
			try
			{
				return new ModifiedUtf8StringConstant(javaClassFileReader.readModifiedUtf8StringWithPrefixedBigEndianUnsigned16BitLength("Modified UTF-8 constant"));
			}
			catch (final InvalidUtf16StringException e)
			{
				throw new InvalidJavaClassFileException("Invalid Modified UTF-8 String", e);
			}
		};
		constantParsers[3]  = (constantPoolIndex, javaClassFileReader, constantPool) -> new IntegerConstant(javaClassFileReader.readBigEndianSigned32BitInteger("Signed 32-bit integer constant"));
		constantParsers[4]  = (constantPoolIndex, javaClassFileReader, constantPool) -> new FloatConstant(javaClassFileReader.readBigEndianFloat("IEEE 754 32-bit floating point constant"));
		constantParsers[5]  = (constantPoolIndex, javaClassFileReader, constantPool) -> new LongConstant(javaClassFileReader.readBigEndianSigned64BitInteger("Signed 64-bit integer constant"));
		constantParsers[6]  = (constantPoolIndex, javaClassFileReader, constantPool) -> new DoubleConstant(javaClassFileReader.readBigEndianRawDouble("IEE 754 64-bit floating point constant"));
		constantParsers[7]  = (constantPoolIndex, javaClassFileReader, constantPool) -> new TypeReferenceIndexConstant(constantPool, javaClassFileReader.readClassReferenceIndex(constantPoolIndex));
		constantParsers[8]  = (constantPoolIndex, javaClassFileReader, constantPool) -> new StringReferenceIndexConstant(constantPool, javaClassFileReader.readStringReferenceIndex(constantPoolIndex));
		constantParsers[9]  = (constantPoolIndex, javaClassFileReader, constantPool) -> new FieldReferenceIndexConstant(constantPool, javaClassFileReader.readClassReferenceIndex(constantPoolIndex), javaClassFileReader.readNameAndTypeDescriptorReferenceIndex(constantPoolIndex));
		constantParsers[10] = (constantPoolIndex, javaClassFileReader, constantPool) -> new ClassMethodReferenceIndexConstant(constantPool, javaClassFileReader.readClassReferenceIndex(constantPoolIndex), javaClassFileReader.readNameAndTypeDescriptorReferenceIndex(constantPoolIndex));
		constantParsers[11] = (constantPoolIndex, javaClassFileReader, constantPool) -> new InterfaceMethodReferenceIndexConstant(constantPool, javaClassFileReader.readClassReferenceIndex(constantPoolIndex), javaClassFileReader.readNameAndTypeDescriptorReferenceIndex(constantPoolIndex));
		constantParsers[12] = (constantPoolIndex, javaClassFileReader, constantPool) -> new NameAndTypeReferenceIndexConstant(constantPool, javaClassFileReader.readNameReferenceIndex(constantPoolIndex), javaClassFileReader.readTypeDescriptorReferenceIndex(constantPoolIndex));
		if (isJava7OrLater)
		{
			constantParsers[15] = (constantPoolIndex, javaClassFileReader, constantPool) ->
			{
				final short referenceKind = javaClassFileReader.readUnsigned8BitInteger("reference kind");
				final ConstantPoolIndex referenceIndex = javaClassFileReader.readMethodHandleReferenceIndex(constantPoolIndex);

				switch(referenceKind)
				{
					case 1:
						return new GetInstanceFieldMethodHandleConstant(constantPool, referenceIndex);

					case 2:
						return new GetStaticFieldMethodHandleConstant(constantPool, referenceIndex);

					case 3:
						return new PutInstanceFieldMethodHandleConstant(constantPool, referenceIndex);

					case 4:
						return new PutStaticFieldMethodHandleConstant(constantPool, referenceIndex);

					case 5:
						return new InvokeVirtualMethodHandleConstant(constantPool, referenceIndex);

					case 6:
						return new InvokeStaticMethodHandleConstant(constantPool, referenceIndex, isLessThanJava8);

					case 7:
						return new InvokeSpecialMethodHandleConstant(constantPool, referenceIndex, isLessThanJava8);

					case 8:
						return new NewInvokeSpecialMethodHandleConstant(constantPool, referenceIndex);

					case 9:
						return new InvokeInterfaceMethodHandleConstant(constantPool, referenceIndex);

					default:
						throw new InvalidJavaClassFileException(format(ENGLISH, "A reference kind must be between 1 to 9 inclusive, not '%1$s'", referenceKind));
				}
			};
			constantParsers[16] = (constantPoolIndex, javaClassFileReader, constantPool) -> new MethodTypeReferenceIndexConstant(constantPool, javaClassFileReader.readStringReferenceIndex(constantPoolIndex));
			constantParsers[18] = (constantPoolIndex, javaClassFileReader, constantPool) -> new InvokeDynamicIndexConstant(constantPool, javaClassFileReader.readBigEndianUnsigned16BitInteger("bootstrap method index"), javaClassFileReader.readNameAndTypeDescriptorReferenceIndex(constantPoolIndex));
		}

		return constantParsers;
	}

	@NotNull
	Constant parse(@NotNull final ConstantPoolIndex constantPoolIndex, @NotNull final ConstantPoolJavaClassFileReader javaClassFileReader, @NotNull final ConstantPool constantPool) throws InvalidJavaClassFileException;
}
