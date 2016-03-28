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

package com.stormmq.java.classfile.domain.attributes.annotations;

import com.stormmq.java.classfile.domain.attributes.AttributeLocation;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.stormmq.java.classfile.domain.attributes.AttributeLocation.*;
import static com.stormmq.java.classfile.domain.attributes.annotations.TargetInfoItem.*;

public enum TargetType
{
	// Java Language Specification 8: §8.1.4, §8.1.5, §8.5, §9.5
	ATypeInTheExtendsOrImplementsClauseOfAClassDeclaration((short) 0x00, "type parameter declaration of generic class or interface", type_parameter_target, Type),


	// Java Language Specification 8: "§9.1.3, §8.5, §9.5"
	ATypeInTheExtendsClauseOfAnInterfaceDeclaration((short) 0x01, "type parameter declaration of generic method or constructor", type_parameter_target, Method),


	// Java Language Specification 8: §8.4.5, §9.4, §9.6.1
	TheReturnTypeOfAMethodIncludingTheTypeOfAnElementOfAnAnnotationType((short) 0x10, "type in extends or implements clause of class declaration (including the direct superclass or direct superinterface of an anonymous class declaration), or in extends clause of interface declaration", supertype_target, Type),


	// Java Language Specification 8: §8.4.6, §8.8.5, §9.4
	ATypeInTheThrowsClauseOfAMethodOrConstructor((short) 0x11, "type in bound of type parameter declaration of generic class or interface", type_parameter_bound_target, Type),


	// Java Language Specification 8: §8.1.2, §9.1.2, §8.4.4, §8.8.4
	ATypeInTheExtendsClauseOfATypeParameterDeclarationOfAGenericClassInterfaceMethodOrConstructor((short) 0x12, "type in bound of type parameter declaration of generic method or constructor", type_parameter_bound_target, Method),


	// Java Language Specification 8: §8.3, §9.3, §8.9.1
	TheTypeInAFieldDeclarationOfAClassOrInterfaceIncludingAnEnumConstant((short) 0x13, "type in field declaration", empty_target, Field),


	// Java Language Specification 8: §8.4.1, §8.8.1, §9.4, §15.27.1
	TheTypeInAFormalParameterDeclarationOfAMethodConstructorOrLambdaExpression((short) 0x14, "return type of method, or type of newly constructed object", empty_target, Method),


	// Java Language Specification 8: §8.4.1
	TheTypeOfTheReceiverParameterOfAMethod((short) 0x15, "receiver type of method or constructor", empty_target, Method),


	// Java Language Specification 8: §14.4, §14.14.1, §14.14.2, §14.20.3
	TheTypeInALocalVariableDeclaration((short) 0x16, "type in formal parameter declaration of method, constructor, or lambda expression", formal_parameter_target, Method),


	// Java Language Specification 8: §14.20
	TheTypeInAnExceptionParameterDeclaration((short) 0x17, "type in throws clause of method or constructor", throws_target, Method),


	// Java Language Specification 8: §8.8.7.1, §15.9, §15.12
	ATypeInTheExplicitTypeArgumentListToAnExplicitConstructorInvocationStatementOrClassInstanceCreationExpressionOrMethodInvocationExpression((short) 0x40, "type in local variable declaration", localvar_target, Code),


	// Java Language Specification 8: 1 = §15.9, 2 = §15.9.5
	InAnUnqualifiedClassInstanceCreationExpressionAsTheClassTypeToBeInstantiated1OrAsTheDirectSuperclassOrDirectSuperinterfaceOfAnAnonymousClassToBeInstantiated2((short) 0x41, "type in resource variable declaration", localvar_target, Code),


	// Java Language Specification 8: §15.10.1
	TheElementTypeInAnArrayCreationExpression((short) 0x42, "type in exception parameter declaration", catch_target, Code),


	// Java Language Specification 8: §15.20.2
	TheTypeThatFollowsTheInstanceofRelationalOperator((short) 0x43, "type in instanceof expression", offset_target, Code),

	x44((short) 0x44, "type in new expression", offset_target, Code),


	// Java Language Specification 8: §15.13
	InAMethodReferenceExpressionAsTheReferenceTypeToSearchForAMemberMethodOrAsTheClassTypeOrArrayTypeToConstructNew((short) 0x45, "type in method reference expression using ::new", offset_target, Code),


	// Java Language Specification 8: §15.13
	InAMethodReferenceExpressionAsTheReferenceTypeToSearchForAMemberMethodOrAsTheClassTypeOrArrayTypeToConstructIdentifier((short) 0x46, "type in method reference expression using ::Identifier", offset_target, Code),


	// Java Language Specification 8: §15.16
	TheTypeInTheCastOperatorOfACastExpression((short) 0x47, "type in cast expression", type_argument_target, Code),


	TypeArgumentGenericConstructorNew((short) 0x48, "type argument for generic constructor in new expression or explicit constructor invocation statement", type_argument_target, Code),


	TypeArgumentGenericMethodInvocation((short) 0x49, "type argument for generic method in method invocation expression", type_argument_target, Code),


	TypeArgumentGenericConstructorMethodReferenceNew((short) 0x4A, "type argument for generic constructor in method reference expression using ::new", type_argument_target, Code),


	TypeArgumentGenericConstructorMethodReferenceIdentifier((short) 0x4B, "type argument for generic method in method reference expression using ::Identifier", type_argument_target, Code),

	;

	private final short targetTypeTag;
	@NotNull public final TargetInfoItem targetInfoItem;
	@NotNull private final AttributeLocation attributeLocation;
	private final boolean isInExpressions;

	TargetType(final short targetTypeTag, @NotNull @NonNls final String kindOfTarget, @NotNull final TargetInfoItem targetInfoItem, @NotNull final AttributeLocation attributeLocation)
	{
		this.targetTypeTag = targetTypeTag;
		this.targetInfoItem = targetInfoItem;
		this.attributeLocation = attributeLocation;
		isInExpressions = attributeLocation == Code;
	}

	private boolean isValidForAttributeLocation(@NotNull final AttributeLocation attributeLocation)
	{
		return this.attributeLocation == attributeLocation;
	}

	@NotNull
	public static TargetType[] allValidTargetTypesForLocationIndexedByTargetTypeTag(@NotNull final AttributeLocation attributeLocation)
	{
		final int length = 256;
		final TargetType[] targetTypes = new TargetType[length];
		for (final TargetType targetType : values())
		{
			if (targetType.isValidForAttributeLocation(attributeLocation))
			{
				targetTypes[targetType.targetTypeTag] = targetType;
			}
		}
		return targetTypes;
	}
}
