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

package com.stormmq.java.classfile.parser.javaClassFileParsers.attributesParsers;

import com.stormmq.java.classfile.domain.attributes.UnknownAttributeData;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.InvalidJavaClassFileException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.exceptions.JavaClassFileContainsDataTooLongToReadException;
import com.stormmq.java.classfile.parser.javaClassFileParsers.constantPool.ConstantPoolJavaClassFileReader;
import com.stormmq.java.classfile.parser.javaClassFileParsers.functions.InvalidExceptionBiIntConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.stormmq.java.classfile.parser.javaClassFileParsers.attributesParsers.Attributes.LineNumberTable;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.attributesParsers.Attributes.LocalVariableTable;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.attributesParsers.Attributes.LocalVariableTypeTable;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Locale.ENGLISH;

public final class AttributesParser
{
	@NotNull private final AttributeParserMappings attributeParserMappings;

	public AttributesParser(@NotNull final AttributeParserMappings attributeParserMappings)
	{
		this.attributeParserMappings = attributeParserMappings;
	}

	@NotNull private static final Set<String> AttributesWhichCanOccurMoreThanOnce = new HashSet<>(asList(new String[]
	{
		LineNumberTable,
		LocalVariableTable,
		LocalVariableTypeTable
	}));

	@NotNull
	public Attributes parseAttributes(@NotNull final ConstantPoolJavaClassFileReader javaClassFileReader) throws InvalidJavaClassFileException, JavaClassFileContainsDataTooLongToReadException
	{
		final Map<String, List<UnknownAttributeData>> unknownAttributes = new HashMap<>(0);
		final Map<String, Object> attributes = javaClassFileReader.parseTableAsMapWith16BitLength(new InvalidExceptionBiIntConsumer<Map<String, Object>>()
		{
			@Override
			public void accept(@NotNull final Map<String, Object> table, final int index) throws InvalidJavaClassFileException, JavaClassFileContainsDataTooLongToReadException
			{
				final String attributeName = javaClassFileReader.readModifiedUtf8String("attribute name reference");
				final long attributeLength = javaClassFileReader.readBigEndianUnsigned32BitInteger("attribute length");

				@NotNull final Object attributeData = attributeParserMappings.parseAttribute(attributeName, attributeLength, javaClassFileReader);

				if (attributeData instanceof UnknownAttributeData)
				{
					@Nullable List<UnknownAttributeData> entries = unknownAttributes.get(attributeName);
					if (entries == null)
					{
						entries = new ArrayList<>(1);
						unknownAttributes.put(attributeName, entries);
					}
					entries.add((UnknownAttributeData) attributeData);
				}
				else
				{
					@Nullable final Object alreadyEncountered = table.get(attributeName);
					final Object attributeDataToStore;
					if (AttributesWhichCanOccurMoreThanOnce.contains(attributeName))
					{
						final List<Object> canOccurMoreThanOnceList = alreadyEncountered == null ? new ArrayList<>(4) : (List<Object>) alreadyEncountered;
						canOccurMoreThanOnceList.add(attributeData);
						attributeDataToStore = canOccurMoreThanOnceList;
					}
					else
					{
						if (alreadyEncountered != null)
						{
							throw new InvalidJavaClassFileException(format(ENGLISH, "The attribute '%1$s' is only allowed to occur once", attributeName));
						}
						attributeDataToStore = attributeData;
					}
					table.put(attributeName, attributeDataToStore);
				}
			}
		});

		final Map<String, List<UnknownAttributeData>> optimisationToReduceMemoryUsage = unknownAttributes.isEmpty() ? emptyMap() : unknownAttributes;
		return new Attributes(new HashMap<>(attributes), optimisationToReduceMemoryUsage);
	}
}


