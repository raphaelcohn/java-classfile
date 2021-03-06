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

package com.stormmq.java.classfile.domain.attributes.annotations.typePathElements;

import com.stormmq.string.AbstractToString;
import org.jetbrains.annotations.NotNull;

public final class AnnotationIsOnATypeArgumentOfAParameterizedTypeTypePathElement extends AbstractToString implements TypePathElement
{
	@NotNull private static final AnnotationIsOnATypeArgumentOfAParameterizedTypeTypePathElement[] Cache = cacheAllCombinations();

	@NotNull
	private static AnnotationIsOnATypeArgumentOfAParameterizedTypeTypePathElement[] cacheAllCombinations()
	{
		final short length = 256;
		final AnnotationIsOnATypeArgumentOfAParameterizedTypeTypePathElement[] cache = new AnnotationIsOnATypeArgumentOfAParameterizedTypeTypePathElement[length];
		for (short index = 0; index < length; index++)
		{
			cache[index] = new AnnotationIsOnATypeArgumentOfAParameterizedTypeTypePathElement(index);
		}
		return cache;
	}

	@NotNull
	public static TypePathElement annotationIsOnATypeArgumentOfAParameterizedType(final short typePathArgumentIndex)
	{
		return Cache[typePathArgumentIndex];
	}

	public final short typeArgumentIndexUnsigned8BitInteger;

	private AnnotationIsOnATypeArgumentOfAParameterizedTypeTypePathElement(final short typeArgumentIndexUnsigned8BitInteger)
	{
		this.typeArgumentIndexUnsigned8BitInteger = typeArgumentIndexUnsigned8BitInteger;
	}

	@NotNull
	@Override
	protected Object[] fields()
	{
		return fields(typeArgumentIndexUnsigned8BitInteger);
	}
}
