/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package eu.uk.ncl.pet5o.esper.epl.enummethod.dot;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;

import java.util.Collection;

public class ExprDotStaticMethodWrapArrayScalar implements ExprDotStaticMethodWrap {

    private final String methodName;
    private final Class arrayType;

    public ExprDotStaticMethodWrapArrayScalar(String methodName, Class arrayType) {
        this.methodName = methodName;
        this.arrayType = arrayType;
    }

    public EPType getTypeInfo() {
        return EPTypeHelper.collectionOfSingleValue(arrayType.getComponentType());
    }

    public Collection convertNonNull(Object result) {
        return CollectionUtil.arrayToCollectionAllowNull(result);
    }

    public CodegenExpression codegenConvertNonNull(CodegenExpression result, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return CollectionUtil.arrayToCollectionAllowNullCodegen(codegenMethodScope, arrayType, result, codegenClassScope);
    }
}
