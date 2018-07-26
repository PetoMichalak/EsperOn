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
package eu.uk.ncl.pet5o.esper.epl.datetime.eval;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;

import java.util.Date;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;

public class DatetimeLongCoercerDate implements DatetimeLongCoercer {
    public long coerce(Object date) {
        return ((Date) date).getTime();
    }

    public CodegenExpression codegen(CodegenExpression value, Class valueType, CodegenClassScope codegenClassScope) {
        if (valueType != Date.class) {
            throw new IllegalStateException("Expected a Date type");
        }
        return exprDotMethod(value, "getTime");
    }
}
