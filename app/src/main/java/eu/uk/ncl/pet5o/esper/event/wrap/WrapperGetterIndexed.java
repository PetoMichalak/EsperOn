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
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterIndexedSPI;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;

public class WrapperGetterIndexed implements EventPropertyGetterIndexedSPI {
    private final EventPropertyGetterIndexedSPI undIndexed;

    public WrapperGetterIndexed(EventPropertyGetterIndexedSPI undIndexed) {
        this.undIndexed = undIndexed;
    }

    public Object get(EventBean event, int index) throws PropertyAccessException {
        if (!(event instanceof DecoratingEventBean)) {
            throw new PropertyAccessException("Mismatched property getter to EventBean type");
        }
        DecoratingEventBean wrapper = (DecoratingEventBean) event;
        EventBean wrapped = wrapper.getUnderlyingEvent();
        if (wrapped == null) {
            return null;
        }
        return undIndexed.get(wrapped, index);
    }

    public CodegenExpression eventBeanGetIndexedCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope, CodegenExpression beanExpression, CodegenExpression key) {
        CodegenMethodNode method = codegenMethodScope.makeChild(Object.class, WrapperGetterIndexed.class, codegenClassScope).addParam(EventBean.class, "event").addParam(int.class, "index").getBlock()
                .declareVar(DecoratingEventBean.class, "wrapper", cast(DecoratingEventBean.class, ref("event")))
                .declareVar(EventBean.class, "wrapped", exprDotMethod(ref("wrapper"), "getUnderlyingEvent"))
                .ifRefNullReturnNull("wrapped")
                .methodReturn(undIndexed.eventBeanGetIndexedCodegen(codegenMethodScope, codegenClassScope, ref("wrapped"), ref("index")));
        return localMethodBuild(method).pass(beanExpression).pass(key).call();
    }
}
