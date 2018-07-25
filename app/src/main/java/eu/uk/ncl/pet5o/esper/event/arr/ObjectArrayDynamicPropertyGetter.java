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
package eu.uk.ncl.pet5o.esper.event.arr;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.PropertyAccessException;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

/**
 * Getter for a dynamic property (syntax field.inner?), using vanilla reflection.
 */
public class ObjectArrayDynamicPropertyGetter implements ObjectArrayEventPropertyGetter {
    private final String propertyName;

    public ObjectArrayDynamicPropertyGetter(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getObjectArray(Object[] array) throws PropertyAccessException {
        return null;
    }

    public boolean isObjectArrayExistsProperty(Object[] array) {
        return false;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param eventBean bean
     * @param propertyName props
     * @return value
     * @throws PropertyAccessException exception
     */
    public static Object getOADynamicProp(EventBean eventBean, String propertyName) throws PropertyAccessException {
        ObjectArrayEventType objectArrayEventType = (ObjectArrayEventType) eventBean.getEventType();
        Integer index = objectArrayEventType.getPropertiesIndexes().get(propertyName);
        if (index == null) {
            return null;
        }
        Object[] theEvent = (Object[]) eventBean.getUnderlying();
        return theEvent[index];
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param eventBean bean
     * @param propertyName name
     * @return flag
     */
    public static boolean isExistsOADynamicProp(EventBean eventBean, String propertyName) {
        ObjectArrayEventType objectArrayEventType = (ObjectArrayEventType) eventBean.getEventType();
        Integer index = objectArrayEventType.getPropertiesIndexes().get(propertyName);
        return index != null;
    }

    public Object get(EventBean eventBean) throws PropertyAccessException {
        return getOADynamicProp(eventBean, propertyName);
    }

    public boolean isExistsProperty(EventBean eventBean) {
        return isExistsOADynamicProp(eventBean, propertyName);
    }

    public Object getFragment(EventBean eventBean) throws PropertyAccessException {
        return null;
    }

    public CodegenExpression eventBeanGetCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return staticMethod(this.getClass(), "getOADynamicProp", beanExpression, constant(propertyName));
    }

    public CodegenExpression eventBeanExistsCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return staticMethod(this.getClass(), "isExistsOADynamicProp", beanExpression, constant(propertyName));
    }

    public CodegenExpression eventBeanFragmentCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public CodegenExpression underlyingGetCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public CodegenExpression underlyingExistsCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantFalse();
    }

    public CodegenExpression underlyingFragmentCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantNull();
    }
}
