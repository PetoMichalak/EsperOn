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
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercerFactory;

public class DatetimeLongCoercerLong implements DatetimeLongCoercer {
    public long coerce(Object date) {
        return (Long) date;
    }

    public CodegenExpression codegen(CodegenExpression value, Class valueType, CodegenClassScope codegenClassScope) {
        return SimpleNumberCoercerFactory.SimpleNumberCoercerLong.codegenLong(value, valueType);
    }
}
