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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class ExprDotStaticMethodWrapIterableScalar implements ExprDotStaticMethodWrap {

    private static final Logger log = LoggerFactory.getLogger(ExprDotStaticMethodWrapArrayScalar.class);

    private final String methodName;
    private final Class componentType;

    public ExprDotStaticMethodWrapIterableScalar(String methodName, Class componentType) {
        this.methodName = methodName;
        this.componentType = componentType;
    }

    public EPType getTypeInfo() {
        return EPTypeHelper.collectionOfSingleValue(componentType);
    }

    public Collection convertNonNull(Object result) {
        if (!(result instanceof Iterable)) {
            log.warn("Expected iterable-type input from method '" + methodName + "' but received " + result.getClass());
            return null;
        }
        return CollectionUtil.iterableToCollection((Iterable) result);
    }

    public CodegenExpression codegenConvertNonNull(CodegenExpression result, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return staticMethod(CollectionUtil.class, "iterableToCollection", cast(Iterable.class, result));
    }
}
