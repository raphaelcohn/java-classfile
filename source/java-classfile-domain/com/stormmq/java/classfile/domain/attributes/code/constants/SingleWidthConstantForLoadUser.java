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

package com.stormmq.java.classfile.domain.attributes.code.constants;

import com.stormmq.java.classfile.domain.InternalTypeName;
import com.stormmq.java.classfile.domain.MethodHandle;
import com.stormmq.java.classfile.domain.attributes.code.operandStackItems.constantOperandStackItems.*;
import com.stormmq.java.classfile.domain.descriptors.MethodDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface SingleWidthConstantForLoadUser<T>
{
	@SuppressWarnings("AnonymousInnerClass") @NotNull SingleWidthConstantForLoadUser<ConstantOperandStackItem> SingleWidthConstantForLoadUserInstance = new SingleWidthConstantForLoadUser<ConstantOperandStackItem>()
	{
		@NotNull
		@Override
		public ConstantOperandStackItem useInteger(final int value)
		{
			return new IntegerConstantOperandStackItem(value);
		}

		@NotNull
		@Override
		public ConstantOperandStackItem useFloat(final float value)
		{
			return new FloatConstantOperandStackItem(value);
		}

		@NotNull
		@Override
		public ConstantOperandStackItem usePotentiallyInvalidString(@NotNull @NonNls final String potentiallyInvalidValue)
		{
			return new StringReferenceConstantOperandStackItem(potentiallyInvalidValue);
		}

		@NotNull
		@Override
		public ConstantOperandStackItem useType(@NotNull final InternalTypeName value)
		{
			return new ClassReferenceConstantOperandStackItem(value);
		}

		@NotNull
		@Override
		public ConstantOperandStackItem useMethodType(@NotNull final MethodDescriptor value)
		{
			return new MethodTypeReferenceConstantOperandStackItem(value);
		}

		@NotNull
		@Override
		public ConstantOperandStackItem useMethodHandle(@NotNull final MethodHandle value)
		{
			return new MethodHandleReferenceConstantOperandStackItem(value);
		}
	};

	@NotNull
	T useInteger(final int value);

	@NotNull
	T useFloat(final float value);

	@NotNull
	T usePotentiallyInvalidString(@NotNull @NonNls final String potentiallyInvalidValue);

	@NotNull
	T useType(@NotNull final InternalTypeName value);

	@NotNull
	T useMethodType(@NotNull final MethodDescriptor value);

	@NotNull
	T useMethodHandle(@NotNull final MethodHandle value);
}
