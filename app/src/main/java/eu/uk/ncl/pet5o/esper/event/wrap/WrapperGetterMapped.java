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
package eu.uk.ncl.pet5o.esper.event.wrap;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.PropertyAccessException;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.event.DecoratingEventBean;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterMappedSPI;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethodBuild;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class WrapperGetterMapped implements EventPropertyGetterMappedSPI {

    private final EventPropertyGetterMappedSPI undMapped;

    public WrapperGetterMapped(EventPropertyGetterMappedSPI undMapped) {
        this.undMapped = undMapped;
    }

    public Object get(eu.uk.ncl.pet5o.esper.client.EventBean event, String key) throws PropertyAccessException {
        if (!(event instanceof DecoratingEventBean)) {
            throw new PropertyAccessException("Mismatched property getter to EventBean type");
        }
        DecoratingEventBean wrapper = (DecoratingEventBean) event;
        eu.uk.ncl.pet5o.esper.client.EventBean wrapped = wrapper.getUnderlyingEvent();
        if (wrapped == null) {
            return null;
        }
        return undMapped.get(wrapped, key);
    }

    public CodegenExpression eventBeanGetMappedCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope, CodegenExpression beanExpression, CodegenExpression key) {
        CodegenMethodNode method = codegenMethodScope.makeChild(Object.class, WrapperGetterMapped.class, codegenClassScope).addParam(eu.uk.ncl.pet5o.esper.client.EventBean.class, "event").addParam(String.class, "key").getBlock()
                .declareVar(DecoratingEventBean.class, "wrapper", cast(DecoratingEventBean.class, ref("event")))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "wrapped", exprDotMethod(ref("wrapper"), "getUnderlyingEvent"))
                .ifRefNullReturnNull("wrapped")
                .methodReturn(undMapped.eventBeanGetMappedCodegen(codegenMethodScope, codegenClassScope, ref("wrapped"), ref("key")));
        return localMethodBuild(method).pass(beanExpression).pass(key).call();
    }
};
