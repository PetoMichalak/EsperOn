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
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;

public class WrapperUnderlyingPropertyGetter implements EventPropertyGetterSPI {
    private final EventPropertyGetterSPI underlyingGetter;

    public WrapperUnderlyingPropertyGetter(EventPropertyGetterSPI underlyingGetter) {
        this.underlyingGetter = underlyingGetter;
    }

    public Object get(EventBean theEvent) {
        if (!(theEvent instanceof DecoratingEventBean)) {
            throw new PropertyAccessException("Mismatched property getter to EventBean type");
        }
        DecoratingEventBean wrapperEvent = (DecoratingEventBean) theEvent;
        EventBean wrappedEvent = wrapperEvent.getUnderlyingEvent();
        if (wrappedEvent == null) {
            return null;
        }
        return underlyingGetter.get(wrappedEvent);
    }

    private CodegenMethodNode getCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return codegenMethodScope.makeChild(Object.class, this.getClass(), codegenClassScope).addParam(EventBean.class, "theEvent").getBlock()
                .declareVarWCast(DecoratingEventBean.class, "wrapperEvent", "theEvent")
                .declareVar(EventBean.class, "wrappedEvent", exprDotMethod(ref("wrapperEvent"), "getUnderlyingEvent"))
                .ifRefNullReturnNull("wrappedEvent")
                .methodReturn(underlyingGetter.eventBeanGetCodegen(ref("wrappedEvent"), codegenMethodScope, codegenClassScope));
    }

    public boolean isExistsProperty(EventBean eventBean) {
        return true; // Property exists as the property is not dynamic (unchecked)
    }

    public Object getFragment(EventBean theEvent) {
        if (!(theEvent instanceof DecoratingEventBean)) {
            throw new PropertyAccessException("Mismatched property getter to EventBean type");
        }
        DecoratingEventBean wrapperEvent = (DecoratingEventBean) theEvent;
        EventBean wrappedEvent = wrapperEvent.getUnderlyingEvent();
        if (wrappedEvent == null) {
            return null;
        }
        return underlyingGetter.getFragment(wrappedEvent);
    }

    private CodegenMethodNode getFragmentCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return codegenMethodScope.makeChild(Object.class, this.getClass(), codegenClassScope).addParam(EventBean.class, "theEvent").getBlock()
                .declareVarWCast(DecoratingEventBean.class, "wrapperEvent", "theEvent")
                .declareVar(EventBean.class, "wrappedEvent", exprDotMethod(ref("wrapperEvent"), "getUnderlyingEvent"))
                .ifRefNullReturnNull("wrappedEvent")
                .methodReturn(underlyingGetter.eventBeanFragmentCodegen(ref("wrappedEvent"), codegenMethodScope, codegenClassScope));
    }

    public CodegenExpression eventBeanGetCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(getCodegen(codegenMethodScope, codegenClassScope), beanExpression);
    }

    public CodegenExpression eventBeanExistsCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantTrue();
    }

    public CodegenExpression eventBeanFragmentCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(getFragmentCodegen(codegenMethodScope, codegenClassScope), beanExpression);
    }

    public CodegenExpression underlyingGetCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        throw implementationNotProvided();
    }

    public CodegenExpression underlyingExistsCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantTrue();
    }

    public CodegenExpression underlyingFragmentCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        throw implementationNotProvided();
    }

    private UnsupportedOperationException implementationNotProvided() {
        return new UnsupportedOperationException("Wrapper event type does not provide an implementation for underlying get");
    }
}
