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
package eu.uk.ncl.pet5o.esper.util;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;

import java.util.Arrays;

import static eu.uk.ncl.pet5o.esper.util.TypeWidenerFactory.codegenWidenArrayAsListMayNull;

/**
 * Type widner that coerces from String to char if required.
 */
public class TypeWidenerObjectArrayToCollectionCoercer implements TypeWidener {
    public Object widen(Object input) {
        return input == null ? null : Arrays.asList((Object[]) input);
    }

    public CodegenExpression widenCodegen(CodegenExpression expression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return codegenWidenArrayAsListMayNull(expression, Object[].class, codegenMethodScope, TypeWidenerObjectArrayToCollectionCoercer.class, codegenClassScope);
    }
}
