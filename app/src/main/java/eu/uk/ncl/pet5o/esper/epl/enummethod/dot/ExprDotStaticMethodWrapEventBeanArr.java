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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;

import java.util.Arrays;
import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class ExprDotStaticMethodWrapEventBeanArr implements ExprDotStaticMethodWrap {
    private EventType type;

    public ExprDotStaticMethodWrapEventBeanArr(EventType type) {
        this.type = type;
    }

    public EPType getTypeInfo() {
        return EPTypeHelper.collectionOfEvents(type);
    }

    public Collection convertNonNull(Object result) {
        if (!result.getClass().isArray()) {
            return null;
        }
        return Arrays.asList((EventBean[]) result);
    }

    public CodegenExpression codegenConvertNonNull(CodegenExpression result, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return staticMethod(Arrays.class, "asList", result);
    }
}
