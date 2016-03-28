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

package com.stormmq.java.classfile.domain.signatures;

import com.stormmq.java.classfile.domain.descriptors.FieldDescriptor;
import com.stormmq.java.classfile.domain.descriptors.MethodDescriptor;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.*;

import java.util.Set;

@SuppressWarnings("SpellCheckingInspection")
public final class Signature
{
	/*
		Example 1
			<XX:Ljava/lang/Object;>Ljava/lang/Object;
			public class TEST<XX>

		Example 2
			<T:Ljava/lang/String;:Ljava/lang/Runnable;Y:Ljava/lang/String;:Ljava/lang/Cloneable;>(TT;)Lcom/stormmq/java/parsing/adaptors/javac/TEST$Generic<TT;>.GenericX<TY;>;
			public <T extends java.lang.String & java.lang.Runnable, Y extends java.lang.String & java.lang.Cloneable> com.stormmq.java.parsing.adaptors.javac.TEST$Generic<T>.GenericX<Y> x(T);

		Example 3
			(nothing)
			public void x();

http://findbugs.sourceforge.net/api/edu/umd/cs/findbugs/ba/SignatureConverter.html
http://attrib4j.sourceforge.net/apidocs/attrib4j/bcel/DescriptorUtil.html
https://stackoverflow.com/questions/12327162/how-to-format-internal-java-names#12327675
	*/

	@NotNull @NonNls private final String value;

	public Signature(@NonNls @NotNull final String value)
	{
		this.value = value;
	}

	public Signature validate(@NotNull final KnownReferenceTypeName thisClassTypeName, @Nullable final KnownReferenceTypeName superClassTypeName, @NotNull final Set<KnownReferenceTypeName> interfaces)
	{
		return this;
	}

	@NotNull
	public Signature validate(@NotNull final MethodDescriptor methodDescriptor)
	{
		return this;
	}

	@NotNull
	public Signature validate(@NotNull final FieldDescriptor fieldDescriptor)
	{
		return this;
	}

	@Override
	@NotNull
	public String toString()
	{
		return value;
	}
}
