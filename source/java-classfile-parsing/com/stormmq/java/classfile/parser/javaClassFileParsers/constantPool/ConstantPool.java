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

import com.stormmq.java.classfile.domain.BootstrapMethodArgument;
import com.stormmq.java.classfile.domain.MethodHandle;
import com.stormmq.java.classfile.domain.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.miscellaneous.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.numbers.*;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.NameAndTypeReferenceIndexConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.constants.referenceIndexConstants.singles.TypeReferenceIndexConstant;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import org.jetbrains.annotations.NotNull;

public final class ConstantPool
{

	@NotNull private final Constant[] constants;
	private final int constantPoolCount;

	public ConstantPool(final char constantPoolCount)
	{
		if (constantPoolCount == 0)
		{
			throw new IllegalArgumentException("Constant pool count can not be zero");
		}
		constants = new Constant[constantPoolCount];
		this.constantPoolCount = constantPoolCount;
	}

	// returns how much to skip
	public char add(@NotNull final ConstantPoolIndex constantPoolIndex, @NotNull final Constant constant) throws InvalidJavaClassFileException
	{
		constantPoolIndex.set(constants, constant);

		if (constant.doesConstantOccupyDoubleWidthSlot())
		{
			final ConstantPoolIndex incremented = constantPoolIndex.incrementForDoubleWidthConstantPoolItem(constantPoolCount);
			incremented.set(constants, PhantomConstant.Phantom);
			return 2;
		}
		return 1;
	}

	public void validateReferenceIndices() throws InvalidJavaClassFileException
	{
		for (int index = 1; index < constantPoolCount; index++)
		{
			final Constant constant = constants[index];
			constant.validateReferenceIndices();
		}
	}

	public char constantPoolCount()
	{
		return (char) constantPoolCount;
	}

	@NotNull
	public ModifiedUtf8StringConstant validateReferenceIndexIsModifiedUtf8String(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		final Constant constant = retrieve(referenceIndex);
		if (!(constant instanceof ModifiedUtf8StringConstant))
		{
			throw new InvalidJavaClassFileException("Referenced value is not a modified UTF-8 string");
		}
		return (ModifiedUtf8StringConstant) constant;
	}

	@NotNull
	public TypeReferenceIndexConstant validateReferenceIndexIsClass(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		final Constant constant = retrieve(referenceIndex);
		if (!(constant instanceof TypeReferenceIndexConstant))
		{
			throw new InvalidJavaClassFileException("internal type name reference is not a class");
		}
		return (TypeReferenceIndexConstant) constant;
	}

	@NotNull
	public NameAndTypeReferenceIndexConstant validateReferenceIndexIsNameAndType(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		final Constant constant = retrieve(referenceIndex);
		if (!(constant instanceof NameAndTypeReferenceIndexConstant))
		{
			throw new InvalidJavaClassFileException("internal type name reference is not a name and type");
		}
		return (NameAndTypeReferenceIndexConstant) constant;
	}

	@NotNull
	private MethodHandle validateReferenceIndexIsMethodHandle(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		final Constant constant = retrieve(referenceIndex);
		if (!(constant instanceof MethodHandle))
		{
			throw new InvalidJavaClassFileException("method handle reference is not a method handle");
		}
		return (MethodHandle) constant;
	}

	@NotNull
	public FieldConstant validateReferenceIndexIsFieldConstant(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		final Constant constant = retrieve(referenceIndex);
		if (!(constant instanceof FieldConstant))
		{
			throw new InvalidJavaClassFileException("internal type name reference is not a field constant");
		}
		return (FieldConstant) constant;
	}

	@NotNull
	private BootstrapMethodArgument validateReferenceIndexIsBootstrapMethodArgument(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		final Constant constant = retrieve(referenceIndex);
		if (!(constant instanceof BootstrapMethodArgument))
		{
			throw new InvalidJavaClassFileException("bootstrap method argument reference is not a boostrap method argument");
		}
		return (BootstrapMethodArgument) constant;
	}

	@NotNull
	public IntegerConstant validateReferenceIndexIsInteger(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		final Constant constant = retrieve(referenceIndex);
		if (!(constant instanceof IntegerConstant))
		{
			throw new InvalidJavaClassFileException("integer reference is not an integer");
		}
		return (IntegerConstant) constant;
	}

	@NotNull
	public FloatConstant validateReferenceIndexIsFloat(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		final Constant constant = retrieve(referenceIndex);
		if (!(constant instanceof FloatConstant))
		{
			throw new InvalidJavaClassFileException("float reference is not a float");
		}
		return (FloatConstant) constant;
	}

	@NotNull
	public LongConstant validateReferenceIndexIsLong(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		final Constant constant = retrieve(referenceIndex);
		if (!(constant instanceof LongConstant))
		{
			throw new InvalidJavaClassFileException("long reference is not a long");
		}
		return (LongConstant) constant;
	}

	@NotNull
	public DoubleConstant validateReferenceIndexIsDouble(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		final Constant constant = retrieve(referenceIndex);
		if (!(constant instanceof DoubleConstant))
		{
			throw new InvalidJavaClassFileException("double reference is not a Double");
		}
		return (DoubleConstant) constant;
	}

	@NotNull
	public Constant retrieve(@NotNull final ConstantPoolIndex constantPoolIndex)
	{
		final Constant retrieve = constantPoolIndex.retrieve(constants);
		assert retrieve != null;
		return retrieve;
	}

	@NotNull
	public String retrieveModifiedUtf8String(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		return validateReferenceIndexIsModifiedUtf8String(referenceIndex).value();
	}

	@NotNull
	public InternalTypeName retrieveInternalTypeName(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		return validateReferenceIndexIsClass(referenceIndex).internalTypeName();
	}

	@NotNull
	public NameAndTypeReferenceIndexConstant retrieveNameAndTypeReference(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		return validateReferenceIndexIsNameAndType(referenceIndex);
	}

	@NotNull
	public MethodHandle retrieveMethodHandle(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		return validateReferenceIndexIsMethodHandle(referenceIndex);
	}

	@NotNull
	public Object retrieveFieldConstant(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		return validateReferenceIndexIsFieldConstant(referenceIndex).value();
	}

	@NotNull
	public BootstrapMethodArgument retrieveBootstrapMethodArgument(@NotNull final ConstantPoolIndex referenceIndex) throws InvalidJavaClassFileException
	{
		return validateReferenceIndexIsBootstrapMethodArgument(referenceIndex);
	}

	public int retrieveInteger(@NotNull final ConstantPoolIndex constantPoolIndex) throws InvalidJavaClassFileException
	{
		return validateReferenceIndexIsInteger(constantPoolIndex).value();
	}

	public float retrieveFloat(@NotNull final ConstantPoolIndex constantPoolIndex) throws InvalidJavaClassFileException
	{
		return validateReferenceIndexIsFloat(constantPoolIndex).value();
	}

	public long retrieveLong(@NotNull final ConstantPoolIndex constantPoolIndex) throws InvalidJavaClassFileException
	{
		return validateReferenceIndexIsLong(constantPoolIndex).value();
	}

	@NotNull
	public RawDouble retrieveDouble(@NotNull final ConstantPoolIndex constantPoolIndex) throws InvalidJavaClassFileException
	{
		return validateReferenceIndexIsDouble(constantPoolIndex).value();
	}
}
