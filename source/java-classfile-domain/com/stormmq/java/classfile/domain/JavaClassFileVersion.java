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

package com.stormmq.java.classfile.domain;

import org.jetbrains.annotations.NotNull;

public enum JavaClassFileVersion
{
	Java1_0_2((char) 45, (char) 0, (char) 2), // Not strictly true, but done to avoid a clash with Java 1.1; given the extreme rarity of Java 1.0.2 in 2016, this isn't considered an issue at this time
	Java1_1((char) 45, (char) 3),
	Java1_2((char) 46),
	Java1_3((char) 47),
	Java1_4((char) 48),
	Java5((char) 49),
	Java6((char) 50),
	Java7((char) 51),
	Java8((char) 52),
	;

	public final char majorVersionNumber;
	public final char minimumInclusiveMinorVersionNumber;
	public final char maximumInclusiveMinorVersionNumber;

	private boolean isLessThan(@NotNull final JavaClassFileVersion javaClassFileVersion)
	{
		return compareTo(javaClassFileVersion) < 0;
	}

	private boolean isGreaterThanOrEqualTo(@NotNull final JavaClassFileVersion javaClassFileVersion)
	{
		return compareTo(javaClassFileVersion) >= 0;
	}

	public boolean isLessThanJava8()
	{
		return isLessThan(Java8);
	}

	public boolean isLessThanJava5()
	{
		return isLessThan(Java5);
	}

	public boolean isJava8OrLater()
	{
		return isGreaterThanOrEqualTo(Java8);
	}

	public boolean isJava1_1OrLater()
	{
		return isGreaterThanOrEqualTo(Java1_1);
	}

	public boolean isJava7OrLater()
	{
		return isGreaterThanOrEqualTo(Java7);
	}

	JavaClassFileVersion(final char majorVersionNumber)
	{
		this(majorVersionNumber, (char) 0, (char) 0);
	}

	JavaClassFileVersion(final char majorVersionNumber, final char minimumInclusiveMinorVersionNumber)
	{
		this(majorVersionNumber, minimumInclusiveMinorVersionNumber, minimumInclusiveMinorVersionNumber);
	}

	JavaClassFileVersion(final char majorVersionNumber, final char minimumInclusiveMinorVersionNumber, final char maximumInclusiveMinorVersionNumber)
	{
		this.majorVersionNumber = majorVersionNumber;
		this.minimumInclusiveMinorVersionNumber = minimumInclusiveMinorVersionNumber;
		this.maximumInclusiveMinorVersionNumber = maximumInclusiveMinorVersionNumber;
	}
}
