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

package com.stormmq.java.classfile.domain.attributes.type;

import com.stormmq.java.classfile.domain.MethodHandle;
import com.stormmq.java.classfile.domain.attributes.code.constants.BootstrapMethodArgument;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class BootstrapMethod
{
	@NotNull public static final BootstrapMethod[] EmptyBootstrapMethods = {};

	@NotNull private final MethodHandle methodHandle;
	@NotNull private final BootstrapMethodArgument[] bootstrapMethodArguments;

	public BootstrapMethod(@NotNull final MethodHandle methodHandle, @NotNull final BootstrapMethodArgument... bootstrapMethodArguments)
	{
		this.methodHandle = methodHandle;
		this.bootstrapMethodArguments = bootstrapMethodArguments;
	}

	@Override
	@NotNull
	public String toString()
	{
		return format(ENGLISH, "%1$s(%2$s, %3$s)", getClass().getSimpleName(), methodHandle, Arrays.toString(bootstrapMethodArguments));
	}
}
