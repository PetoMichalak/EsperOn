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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;
import eu.uk.ncl.pet5o.esper.util.SimpleTypeCaster;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static eu.uk.ncl.pet5o.esper.event.vaevent.VariantEventPropertyGetterAny.variantImplementationNotProvided;

public class VariantEventPropertyGetterAnyWCast implements EventPropertyGetterSPI {
    private final VariantPropertyGetterCache propertyGetterCache;
    private final int assignedPropertyNumber;
    private final SimpleTypeCaster caster;

    public VariantEventPropertyGetterAnyWCast(VariantPropertyGetterCache propertyGetterCache, int assignedPropertyNumber, SimpleTypeCaster caster) {
        this.propertyGetterCache = propertyGetterCache;
        this.assignedPropertyNumber = assignedPropertyNumber;
        this.caster = caster;
    }

    public Object get(EventBean eventBean) throws PropertyAccessException {
        Object value = VariantEventPropertyGetterAny.variantGet(eventBean, propertyGetterCache, assignedPropertyNumber);
        if (value == null) {
            return null;
        }
        return caster.cast(value);
    }

    private CodegenMethodNode getCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) throws PropertyAccessException {
        CodegenMember mCache = codegenClassScope.makeAddMember(VariantPropertyGetterCache.class, propertyGetterCache);
        CodegenMember mCaster = codegenClassScope.makeAddMember(SimpleTypeCaster.class, caster);
        return codegenMethodScope.makeChild(Object.class, this.getClass(), codegenClassScope).addParam(EventBean.class, "eventBean").getBlock()
                .declareVar(Object.class, "value", staticMethod(VariantEventPropertyGetterAny.class, "variantGet", ref("eventBean"), member(mCache.getMemberId()), constant(assignedPropertyNumber)))
                .methodReturn(exprDotMethod(member(mCaster.getMemberId()), "cast", ref("value")));
    }

    public boolean isExistsProperty(EventBean eventBean) {
        return VariantEventPropertyGetterAny.variantExists(eventBean, propertyGetterCache, assignedPropertyNumber);
    }

    public Object getFragment(EventBean eventBean) {
        return null;
    }

    public CodegenExpression eventBeanGetCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(getCodegen(codegenMethodScope, codegenClassScope), beanExpression);
    }

    public CodegenExpression eventBeanExistsCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        CodegenMember member = codegenClassScope.makeAddMember(VariantPropertyGetterCache.class, propertyGetterCache);
        return staticMethod(VariantEventPropertyGetterAny.class, "variantExists", beanExpression, member(member.getMemberId()), constant(assignedPropertyNumber));
    }

    public CodegenExpression eventBeanFragmentCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public CodegenExpression underlyingGetCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        throw variantImplementationNotProvided();
    }

    public CodegenExpression underlyingExistsCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        throw variantImplementationNotProvided();
    }

    public CodegenExpression underlyingFragmentCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        throw variantImplementationNotProvided();
    }
}
