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

package com.stormmq.java.classfile.domain.attributes.code.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ComputationalCategory
{
	_int(true, Integer.class, true),
	_float(false, Float.class, true),
	_long(true, Long.class, false),
	_double(false, Double.class, false),
	reference,
	returnAddress,
	;

	private final boolean isNumeric;
	public final boolean isNotNumeric;
	public final boolean isIntegerNumber;
	public final boolean isNotIntegerNumber;
	@Nullable private final Class<? extends Number> numberClass;
	@NotNull public final String actualName;
	public final boolean isCategory1;

	ComputationalCategory()
	{
		isNumeric = false;
		isNotNumeric = true;
		isIntegerNumber = false;
		isNotIntegerNumber = true;
		numberClass = null;
		actualName = actualName();
		isCategory1 = true;
	}

	ComputationalCategory(final boolean isIntegerNumber, @NotNull final Class<? extends Number> numberClass, final boolean isCategory1)
	{
		isNumeric = true;
		isNotNumeric = false;
		this.isIntegerNumber = isIntegerNumber;
		isNotIntegerNumber = !isIntegerNumber;
		this.numberClass = numberClass;
		actualName = actualName();
		this.isCategory1 = isCategory1;
	}

	@NotNull
	private String actualName()
	{
		final String actualName;
		final String name = name();

		if (name.isEmpty())
		{
			throw new IllegalStateException("name() should not be empty");
		}

		actualName = name.charAt(0) != '_' ? name : name.substring(1);
		return actualName;
	}
}
