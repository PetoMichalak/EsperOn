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
import eu.uk.ncl.pet5o.esper.event.BaseNestableEventUtil;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.castUnderlying;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

/**
 * Getter for a dynamic indexed property for maps.
 */
public class ObjectArrayIndexedPropertyGetter implements ObjectArrayEventPropertyGetter {
    private final int propertyIndex;
    private final int index;

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param array array
     * @param propertyIndex prop index
     * @param index index
     * @return value
     * @throws PropertyAccessException exception
     */
    public static Object getObjectArrayIndexValue(Object[] array, int propertyIndex, int index) throws PropertyAccessException {
        Object value = array[propertyIndex];
        return BaseNestableEventUtil.getBNArrayValueAtIndexWithNullCheck(value, index);
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param array array
     * @param propertyIndex prop index
     * @param index index
     * @return value
     * @throws PropertyAccessException exception
     */
    public static boolean isObjectArrayExistsProperty(Object[] array, int propertyIndex, int index) throws PropertyAccessException {
        Object value = array[propertyIndex];
        return BaseNestableEventUtil.isExistsIndexedValue(value, index);
    }

    /**
     * Ctor.
     *
     * @param propertyIndex property index
     * @param index         index to get the element at
     */
    public ObjectArrayIndexedPropertyGetter(int propertyIndex, int index) {
        this.propertyIndex = propertyIndex;
        this.index = index;
    }

    public Object getObjectArray(Object[] array) throws PropertyAccessException {
        return getObjectArrayIndexValue(array, propertyIndex, index);
    }

    public boolean isObjectArrayExistsProperty(Object[] array) {
        return isObjectArrayExistsProperty(array, propertyIndex, index);
    }

    public Object get(EventBean eventBean) throws PropertyAccessException {
        return getObjectArray(BaseNestableEventUtil.checkedCastUnderlyingObjectArray(eventBean));
    }

    public boolean isExistsProperty(EventBean eventBean) {
        return isObjectArrayExistsProperty(BaseNestableEventUtil.checkedCastUnderlyingObjectArray(eventBean));
    }

    public Object getFragment(EventBean eventBean) {
        return null;
    }

    public CodegenExpression eventBeanGetCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingGetCodegen(castUnderlying(Object[].class, beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression eventBeanExistsCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingExistsCodegen(castUnderlying(Object[].class, beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression eventBeanFragmentCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public CodegenExpression underlyingGetCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return staticMethod(this.getClass(), "getObjectArrayIndexValue", underlyingExpression, constant(propertyIndex), constant(index));
    }

    public CodegenExpression underlyingExistsCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return staticMethod(this.getClass(), "isObjectArrayExistsProperty", underlyingExpression, constant(propertyIndex), constant(index));
    }

    public CodegenExpression underlyingFragmentCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantNull();
    }
}
