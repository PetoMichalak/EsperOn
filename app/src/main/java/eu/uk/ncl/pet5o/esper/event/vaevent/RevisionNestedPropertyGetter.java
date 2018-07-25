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
package eu.uk.ncl.pet5o.esper.event.vaevent;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.event.vaevent.VAERevisionEventPropertyGetterDeclaredGetVersioned.revisionImplementationNotProvided;

/**
 * A getter that works on POJO events residing within a Map as an event property.
 */
public class RevisionNestedPropertyGetter implements EventPropertyGetterSPI {
    private final EventPropertyGetterSPI revisionGetter;
    private final EventPropertyGetterSPI nestedGetter;
    private final EventAdapterService eventAdapterService;

    /**
     * Ctor.
     *
     * @param revisionGetter      getter for revision value
     * @param nestedGetter        getter to apply to revision value
     * @param eventAdapterService for handling object types
     */
    public RevisionNestedPropertyGetter(EventPropertyGetterSPI revisionGetter, EventPropertyGetterSPI nestedGetter, EventAdapterService eventAdapterService) {
        this.revisionGetter = revisionGetter;
        this.eventAdapterService = eventAdapterService;
        this.nestedGetter = nestedGetter;
    }

    public Object get(eu.uk.ncl.pet5o.esper.client.EventBean obj) {
        Object result = revisionGetter.get(obj);
        if (result == null) {
            return result;
        }

        // Object within the map
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventAdapterService.adapterForBean(result);
        return nestedGetter.get(theEvent);
    }

    private CodegenMethodNode getCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        CodegenMember mgetter = codegenClassScope.makeAddMember(EventPropertyGetter.class, nestedGetter);
        CodegenMember msvc = codegenClassScope.makeAddMember(EventAdapterService.class, eventAdapterService);
        return codegenMethodScope.makeChild(Object.class, this.getClass(), codegenClassScope).addParam(eu.uk.ncl.pet5o.esper.client.EventBean.class, "obj").getBlock()
                .declareVar(Object.class, "result", revisionGetter.eventBeanGetCodegen(ref("obj"), codegenMethodScope, codegenClassScope))
                .ifRefNullReturnNull("result")
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", exprDotMethod(member(msvc.getMemberId()), "adapterForBean", ref("result")))
                .methodReturn(exprDotMethod(member(mgetter.getMemberId()), "get", ref("theEvent")));
    }

    public boolean isExistsProperty(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) {
        return true; // Property exists as the property is not dynamic (unchecked)
    }

    public Object getFragment(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) {
        return null; // no fragments provided by revision events
    }

    public CodegenExpression eventBeanGetCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(getCodegen(codegenMethodScope, codegenClassScope), beanExpression);
    }

    public CodegenExpression eventBeanExistsCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantTrue();
    }

    public CodegenExpression eventBeanFragmentCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public CodegenExpression underlyingGetCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        throw revisionImplementationNotProvided();
    }

    public CodegenExpression underlyingExistsCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        throw revisionImplementationNotProvided();
    }

    public CodegenExpression underlyingFragmentCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        throw revisionImplementationNotProvided();
    }
}
