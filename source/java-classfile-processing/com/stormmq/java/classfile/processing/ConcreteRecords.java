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

package com.stormmq.java.classfile.processing;

import com.stormmq.functions.ToBooleanFunction;
import com.stormmq.java.classfile.processing.typeInformationUsers.TypeInformationTriplet;
import com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;
import java.util.Map;
import java.util.function.Function;

import static com.stormmq.annotations.AnnotationHelper.doesAnnotationTargetPackages;
import static com.stormmq.annotations.AnnotationHelper.isInherited;
import static com.stormmq.annotations.AnnotationHelper.isRepeatable;
import static com.stormmq.functions.MapHelper.useMapValue;
import static com.stormmq.functions.MapHelper.useMapValueOrGetDefault;
import static com.stormmq.java.parsing.utilities.names.typeNames.referenceTypeNames.KnownReferenceTypeName.knownReferenceTypeName;

public final class ConcreteRecords implements Records
{
	@NotNull private final Map<KnownReferenceTypeName, TypeInformationTriplet> records;

	public ConcreteRecords(@NotNull final Map<KnownReferenceTypeName, TypeInformationTriplet> records)
	{
		this.records = records;
	}

	@Override
	public <R> void iterate(@NotNull final TypeInformationTripletUser<R> typeInformationTripletUser, @NotNull final Function<Records, R> usefulRecordsCreator)
	{
		final R usefulRecords = usefulRecordsCreator.apply(this);
		for (final TypeInformationTriplet typeInformationTriplet : records.values())
		{
			typeInformationTripletUser.use(usefulRecords, typeInformationTriplet);
		}
	}

	@Override
	@NotNull
	public TypeInformationTriplet retrieve(@NotNull final KnownReferenceTypeName knownReferenceTypeName)
	{
		return useMapValueOrGetDefault(records, knownReferenceTypeName, () ->
		{
			throw new NoTypeInformationKnownException(knownReferenceTypeName);
		});
	}

	@Override
	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	public boolean loopOverSelfAndSuperclasses(@NotNull final TypeInformationTriplet self, @NotNull final ToBooleanFunction<TypeInformationTriplet> user)
	{
		TypeInformationTriplet instance = self;
		do
		{
			if (user.applyAsBoolean(instance))
			{
				return true;
			}

			@Nullable final KnownReferenceTypeName superClassTypeName = instance.superClassTypeName();
			if (superClassTypeName == null)
			{
				return false;
			}

			instance = retrieve(superClassTypeName);
		}
		while (true);
	}

	@Override
	public boolean hasAnnotation(@NotNull final TypeInformationTriplet self, @NotNull final Class<? extends Annotation> annotationClass)
	{
		final Class<? extends Annotation> annotationClassToLookFor;
		final boolean doesAnnotationAlsoTargetPackages;
		final boolean isInherited;
		if (isRepeatable(annotationClass))
		{
			// container class of the annotation, see https://docs.oracle.com/javase/tutorial/java/annotations/repeating.html
			annotationClassToLookFor = annotationClass.getAnnotation(Repeatable.class).value();
			doesAnnotationAlsoTargetPackages = doesAnnotationTargetPackages(annotationClass) || doesAnnotationTargetPackages(annotationClassToLookFor);
			isInherited = isInherited(annotationClass) || isInherited(annotationClassToLookFor);
		}
		else
		{
			annotationClassToLookFor = annotationClass;
			doesAnnotationAlsoTargetPackages = doesAnnotationTargetPackages(annotationClass);
			isInherited = isInherited(annotationClass);
		}

		final KnownReferenceTypeName annotationTypeName = knownReferenceTypeName(annotationClassToLookFor.getName());

		if (doesAnnotationAlsoTargetPackages)
		{
			final KnownReferenceTypeName packageClass = self.packageClass();
			final boolean isOnPackage = useMapValue(records, packageClass, false, value -> value.hasAnnotation(annotationTypeName));
			if (isOnPackage)
			{
				return true;
			}
		}

		if (isInherited)
		{
			return hasInheritedAnnotation(self, annotationTypeName);
		}
		return self.hasAnnotation(annotationTypeName);
	}

	private boolean hasInheritedAnnotation(@NotNull final TypeInformationTriplet self, @NotNull final KnownReferenceTypeName annotationTypeName)
	{
		return loopOverSelfAndSuperclasses(self, typeInformationTriplet -> typeInformationTriplet.hasAnnotation(annotationTypeName));
	}
}
