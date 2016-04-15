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

import com.stormmq.functions.SizedIterator;
import com.stormmq.java.classfile.domain.information.*;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import com.stormmq.string.AbstractToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class TypeInformationTriplet extends AbstractToString implements TypeInformation
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

	@NotNull
	@Override
	protected Object[] fields()
	{
		return fields(typeInformation, relativeFilePath, relativeRootFolderPath);
	}

	@Override
	public boolean hasAnnotation(@NotNull final KnownReferenceTypeName annotationTypeName)
	{
		return typeInformation.hasAnnotation(annotationTypeName);
	}

	@Override
	public int numberOfStaticFields()
	{
		return typeInformation.numberOfStaticFields();
	}

	@Override
	public void forEachStaticField(@NotNull final Consumer<FieldInformation> action)
	{
		typeInformation.forEachStaticField(action);
	}

	@Override
	public int numberOfInstanceFields()
	{
		return typeInformation.numberOfInstanceFields();
	}

	@Override
	public void forEachInstanceField(@NotNull final Consumer<FieldInformation> action)
	{
		typeInformation.forEachInstanceField(action);
	}

	@NotNull
	@Override
	public SizedIterator<FieldInformation> instanceFieldsSizedIterator()
	{
		return typeInformation.instanceFieldsSizedIterator();
	}

	@Override
	public int numberOfStaticMethods()
	{
		return typeInformation.numberOfStaticMethods();
	}

	@Override
	public void forEachStaticMethod(@NotNull final Consumer<MethodInformation> action)
	{
		typeInformation.forEachStaticMethod(action);
	}

	@Override
	public int numberOfInstanceMethods()
	{
		return typeInformation.numberOfInstanceMethods();
	}

	@Override
	public void forEachInstanceMethod(@NotNull final Consumer<MethodInformation> action)
	{
		typeInformation.forEachInstanceMethod(action);
	}

	@Override
	@NotNull
	public KnownReferenceTypeName thisClassTypeName()
	{
		return typeInformation.thisClassTypeName();
	}

	@Nullable
	@Override
	public KnownReferenceTypeName superClassTypeName()
	{
		return typeInformation.superClassTypeName();
	}

	@NotNull
	@Override
	public KnownReferenceTypeName packageClass()
	{
		return typeInformation.packageClass();
	}
}
