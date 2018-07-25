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
import eu.uk.ncl.pet5o.esper.client.PropertyAccessException;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.collection.MultiKeyUntyped;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.event.vaevent.VAERevisionEventPropertyGetterDeclaredGetVersioned.revisionImplementationNotProvided;

public class VAERevisionEventPropertyGetterDeclaredNKey implements EventPropertyGetterSPI {
    private final int keyPropertyNumber;

    public VAERevisionEventPropertyGetterDeclaredNKey(int keyPropertyNumber) {
        this.keyPropertyNumber = keyPropertyNumber;
    }

    public Object get(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) throws PropertyAccessException {
        RevisionEventBeanDeclared riv = (RevisionEventBeanDeclared) eventBean;
        MultiKeyUntyped key = (MultiKeyUntyped) riv.getKey();
        if (key == null) {
            return null;
        }
        return key.getKeys()[keyPropertyNumber];
    }

    private CodegenMethodNode getCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return codegenMethodScope.makeChild(Object.class, this.getClass(), codegenClassScope).addParam(eu.uk.ncl.pet5o.esper.client.EventBean.class, "eventBean").getBlock()
                .declareVar(RevisionEventBeanDeclared.class, "riv", cast(RevisionEventBeanDeclared.class, ref("eventBean")))
                .declareVar(MultiKeyUntyped.class, "key", cast(MultiKeyUntyped.class, exprDotMethod(ref("riv"), "getKey")))
                .ifRefNullReturnNull("key")
                .methodReturn(arrayAtIndex(exprDotMethod(ref("key"), "getKeys"), constant(keyPropertyNumber)));
    }

    public boolean isExistsProperty(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) {
        return true;
    }

    public Object getFragment(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) {
        return null;
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
