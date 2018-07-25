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
package eu.uk.ncl.pet5o.esper.epl.expression.codegen;

import com.espertech.esper.codegen.base.CodegenBlock;
import com.espertech.esper.codegen.base.CodegenClassScope;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.base.CodegenMethodScope;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.event.EventPropertyGetterSPI;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static com.espertech.esper.epl.expression.codegen.CodegenLegoPropertyBeanOrUnd.AccessType.*;

/**
 * if (!(valueMap instanceof TYPE)) {
 * if (value instanceof EventBean) {
 * return getter.XXX((EventBean) value);
 * }
 * return XXXX;
 * }
 * return getter.getXXXX(value);
 */
public class CodegenLegoPropertyBeanOrUnd {
    public static CodegenMethodNode from(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope, Class expectedUnderlyingType, EventPropertyGetterSPI innerGetter, AccessType accessType, Class generator) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(accessType == AccessType.EXISTS ? boolean.class : Object.class, generator, codegenClassScope).addParam(Object.class, "value");
        CodegenBlock block = methodNode.getBlock()
                .ifNotInstanceOf("value", expectedUnderlyingType)
                .ifInstanceOf("value", com.espertech.esper.client.EventBean.class)
                .declareVarWCast(com.espertech.esper.client.EventBean.class, "bean", "value");

        if (accessType == AccessType.GET) {
            block = block.blockReturn(innerGetter.eventBeanGetCodegen(ref("bean"), codegenMethodScope, codegenClassScope));
        } else if (accessType == AccessType.EXISTS) {
            block = block.blockReturn(innerGetter.eventBeanExistsCodegen(ref("bean"), codegenMethodScope, codegenClassScope));
        } else if (accessType == AccessType.FRAGMENT) {
            block = block.blockReturn(innerGetter.eventBeanFragmentCodegen(ref("bean"), codegenMethodScope, codegenClassScope));
        } else {
            throw new UnsupportedOperationException("Invalid access type " + accessType);
        }

        block = block.blockReturn(constant(accessType == AccessType.EXISTS ? false : null));

        CodegenExpression expression;
        if (accessType == AccessType.GET) {
            expression = innerGetter.underlyingGetCodegen(cast(expectedUnderlyingType, ref("value")), codegenMethodScope, codegenClassScope);
        } else if (accessType == AccessType.EXISTS) {
            expression = innerGetter.underlyingExistsCodegen(cast(expectedUnderlyingType, ref("value")), codegenMethodScope, codegenClassScope);
        } else if (accessType == AccessType.FRAGMENT) {
            expression = innerGetter.underlyingFragmentCodegen(cast(expectedUnderlyingType, ref("value")), codegenMethodScope, codegenClassScope);
        } else {
            throw new UnsupportedOperationException("Invalid access type " + accessType);
        }
        block.methodReturn(expression);
        return methodNode;
    }

    public enum AccessType {
        GET,
        EXISTS,
        FRAGMENT
    }
}
