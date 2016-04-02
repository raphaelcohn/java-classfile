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

package com.stormmq.java.classfile.processing.typeInformationUsers;

import com.stormmq.java.classfile.domain.information.*;
import com.stormmq.java.classfile.domain.uniqueness.FieldUniqueness;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.BiConsumer;

public final class TypeInformationTriplet implements TypeInformation
{
	@NotNull private final TypeInformation typeInformation;
	@NotNull public final String relativeFilePath;
	@NotNull public final Path relativeRootFolderPath;

	public TypeInformationTriplet(@NotNull final TypeInformation typeInformation, @NotNull final String relativeFilePath, @NotNull final Path relativeRootFolderPath)
	{
		this.typeInformation = typeInformation;
		this.relativeFilePath = relativeFilePath;
		this.relativeRootFolderPath = relativeRootFolderPath;
	}

	@Override
	public boolean hasAnnotation(@NotNull final KnownReferenceTypeName annotationTypeName)
	{
		return typeInformation.hasAnnotation(annotationTypeName);
	}

	@Override
	public int numberOfStaticAndInstanceFields()
	{
		return typeInformation.numberOfStaticAndInstanceFields();
	}

	@Override
	public void forEachStaticFieldInReverseOrder(@NotNull final BiConsumer<FieldUniqueness, FieldInformation> action)
	{
		typeInformation.forEachStaticFieldInReverseOrder(action);
	}

	@Override
	public void forEachInstanceFieldInReverseOrder(@NotNull final BiConsumer<FieldUniqueness, FieldInformation> action)
	{
		typeInformation.forEachInstanceFieldInReverseOrder(action);
	}

	@Override
	@NotNull
	public KnownReferenceTypeName thisClassTypeName()
	{
		return typeInformation.thisClassTypeName();
	}

	@NotNull
	@Override
	public KnownReferenceTypeName superClassTypeName()
	{
		return typeInformation.superClassTypeName();
	}
}
