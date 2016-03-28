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

package com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags;

import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.AccessFlags.computeFlagsInverseMask;
import static com.stormmq.java.classfile.parser.javaClassFileParsers.accessFlags.AccessFlags.hasFlagSet;

public final class ParameterAccessFlags
{
	private static final char ACC_FINAL = 0x0010; // Indicates that the formal parameter was declared final.
	private static final char ACC_SYNTHETIC = 0x1000; // Indicates that the formal parameter was not explicitly or implicitly declared in source code, according to the specification of the language in which the source code was written (JLS §13.1). (The formal parameter is an implementation artifact of the compiler which produced this class file.)
	private static final char ACC_MANDATORY = 0x8000; // Indicates that the formal parameter was implicitly declared in source code, according to the specification of the language in which the source code was written (JLS §13.1). (The formal parameter is mandated by a language specification, so all compilers for the language must emit it.)

	public static final int ParameterAccessFlagsValidityMask = computeFlagsInverseMask(ACC_FINAL, ACC_SYNTHETIC, ACC_MANDATORY);

	public static boolean isParameterFinal(final char accessFlags)
	{
		return hasFlagSet(accessFlags, ACC_FINAL);
	}

	public static boolean isParameterSynthetic(final char accessFlags)
	{
		return hasFlagSet(accessFlags, ACC_SYNTHETIC);
	}

	public static boolean isParameterMandatory(final char accessFlags)
	{
		return hasFlagSet(accessFlags, ACC_MANDATORY);
	}

	private ParameterAccessFlags()
	{
	}
}
