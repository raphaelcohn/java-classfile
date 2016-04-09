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

package com.stormmq.java.parsing.utilities;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.stormmq.functions.MapHelper.putOnce;
import static com.stormmq.functions.CollectionHelper.addOnce;

public final class PrimitiveTypeHelper
{
	private static final int NumberOfTypes = 8;
	private static final Map<Class<?>, Class<?>> boxedToUnboxedPrimitiveTypes;
	private static final Set<Class<?>> unboxedPrimitiveTypes;

	static
	{
		boxedToUnboxedPrimitiveTypes = new HashMap<>(NumberOfTypes);
		unboxedPrimitiveTypes = new HashSet<>(NumberOfTypes);

		populate(Boolean.class, Boolean.TYPE);
		populate(Byte.class, Byte.TYPE);
		populate(Short.class, Short.TYPE);
		populate(Character.class, Character.TYPE);
		populate(Integer.class, Integer.TYPE);
		populate(Long.class, Long.TYPE);
		populate(Float.class, Float.TYPE);
		populate(Double.class, Double.TYPE);
	}

	private PrimitiveTypeHelper()
	{
	}

	private static void populate(@NotNull final Class<?> boxedType, @NotNull final Class<?> primitiveType)
	{
		if (boxedType.isPrimitive())
		{
			throw new IllegalArgumentException("boxedType must not be primitive");
		}
		if (!primitiveType.isPrimitive())
		{
			throw new IllegalArgumentException("primitiveType must be primitive");
		}
		putOnce(boxedToUnboxedPrimitiveTypes, boxedType, primitiveType);
		addOnce(unboxedPrimitiveTypes, primitiveType);
	}

	public static boolean isNotBoxedPrimitiveExcludingVoid(@NotNull final Class<?> potentialBoxedType)
	{
		return !isBoxedPrimitiveExcludingVoid(potentialBoxedType);
	}

	private static boolean isBoxedPrimitiveExcludingVoid(@NotNull final Class<?> potentialBoxedType)
	{
		return boxedToUnboxedPrimitiveTypes.containsKey(potentialBoxedType);
	}

	public static boolean isUnboxedPrimitiveExcludingVoid(@NotNull final Class<?> potentialPrimitiveType)
	{
		return unboxedPrimitiveTypes.contains(potentialPrimitiveType);
	}

	public static boolean isBoxedOrUnboxedPrimitiveExcludingVoid(@NotNull final Class<?> valueType)
	{
		return boxedToUnboxedPrimitiveTypes.containsKey(valueType) || unboxedPrimitiveTypes.contains(valueType);
	}

	public static boolean isNotBoxedOrUnboxedPrimitiveExcludingVoid(@NotNull final Class<?> valueType)
	{
		return !(boxedToUnboxedPrimitiveTypes.containsKey(valueType) || unboxedPrimitiveTypes.contains(valueType));
	}
}
