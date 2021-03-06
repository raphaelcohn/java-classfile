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

package com.stormmq.java.classfile.domain.names;

import org.jetbrains.annotations.*;

import static com.stormmq.string.StringConstants.InstanceInitializerMethodName;
import static com.stormmq.string.StringConstants.StaticInitializerMethodName;

public final class MethodName
{
	@NotNull public static final MethodName InstanceInitializer = new MethodName(InstanceInitializerMethodName);
	@NotNull public static final MethodName StaticInstanceInitializer = new MethodName(StaticInitializerMethodName);

	@NotNull private final String validatedMethodName;

	public MethodName(@NotNull @NonNls final String validatedMethodName)
	{
		this.validatedMethodName = validatedMethodName;
	}

	@Override
	@NotNull
	public String toString()
	{
		return validatedMethodName;
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(@Nullable final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final MethodName that = (MethodName) o;

		if (!validatedMethodName.equals(that.validatedMethodName))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return validatedMethodName.hashCode();
	}
}
