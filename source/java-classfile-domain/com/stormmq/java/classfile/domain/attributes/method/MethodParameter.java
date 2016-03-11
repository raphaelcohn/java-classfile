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

package com.stormmq.java.classfile.domain.attributes.method;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MethodParameter
{
	@NotNull public static final MethodParameter[] EmptyMethodParameters = {};

	@Nullable private final String parameterName;
	private final boolean isFinal;
	private final boolean isSynthetic;
	private final boolean isMandatory;

	public MethodParameter(@Nullable final String parameterName, final boolean isFinal, final boolean isSynthetic, final boolean isMandatory)
	{
		this.parameterName = parameterName;
		this.isFinal = isFinal;
		this.isSynthetic = isSynthetic;
		this.isMandatory = isMandatory;
	}
}
