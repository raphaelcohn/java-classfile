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

public enum LocalVariableSimpleType
{
	_boolean(ComputationalCategory._int, 1),
	_byte(ComputationalCategory._int, 1),
	_char(ComputationalCategory._int, 1),
	_short(ComputationalCategory._int, 1),
	_int(ComputationalCategory._int, 1),
	_float(ComputationalCategory._float, 1),
	reference(ComputationalCategory.reference, 1),
	returnAddress(ComputationalCategory.returnAddress, 1),
	_long(ComputationalCategory._long, 2),
	_double(ComputationalCategory._double, 2),
	;

	@NotNull private final String actualName;
	@NotNull private final ComputationalCategory computationalCategory;
	private final int category;

	LocalVariableSimpleType(@NotNull final ComputationalCategory computationalCategory, final int category)
	{
		if (category < 1 || category > 2)
		{
			throw new IllegalArgumentException("category can only be either 1 or 2");
		}

		final String name = name();

		if (name.isEmpty())
		{
			throw new IllegalStateException("name() should not be empty");
		}

		actualName = name.charAt(0) != '_' ? name : name.substring(1);
		this.computationalCategory = computationalCategory;
		this.category = category;
	}
}
