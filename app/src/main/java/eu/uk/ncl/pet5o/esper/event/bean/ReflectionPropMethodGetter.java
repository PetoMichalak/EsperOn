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
package eu.uk.ncl.pet5o.esper.event.bean;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.PropertyAccessException;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.vaevent.PropertyUtility;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.castUnderlying;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;

/**
 * Property getter for methods using Java's vanilla reflection.
 */
public final class ReflectionPropMethodGetter extends BaseNativePropertyGetter implements BeanEventPropertyGetter {
    private final Method method;

    /**
     * Constructor.
     *
     * @param method              is the regular reflection method to use to obtain values for a field.
     * @param eventAdapterService factory for event beans and event types
     */
    public ReflectionPropMethodGetter(Method method, EventAdapterService eventAdapterService) {
        super(eventAdapterService, method.getReturnType(), JavaClassHelper.getGenericReturnType(method, false));
        this.method = method;
    }

    public Object getBeanProp(Object object) throws PropertyAccessException {
        try {
            return method.invoke(object, (Object[]) null);
        } catch (IllegalArgumentException e) {
            throw PropertyUtility.getIllegalArgumentException(method, e);
        } catch (IllegalAccessException e) {
            throw PropertyUtility.getIllegalAccessException(method, e);
        } catch (InvocationTargetException e) {
            throw PropertyUtility.getInvocationTargetException(method, e);
        }
    }

    public boolean isBeanExistsProperty(Object object) {
        return true;
    }

    public final Object get(EventBean obj) throws PropertyAccessException {
        Object underlying = obj.getUnderlying();
        return getBeanProp(underlying);
    }

    public String toString() {
        return "ReflectionPropMethodGetter " +
                "method=" + method.toGenericString();
    }

    public boolean isExistsProperty(EventBean eventBean) {
        return true; // Property exists as the property is not dynamic (unchecked)
    }

    public Class getBeanPropType() {
        return method.getReturnType();
    }

    public Class getTargetType() {
        return method.getDeclaringClass();
    }

    public CodegenExpression eventBeanGetCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingGetCodegen(castUnderlying(getTargetType(), beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression eventBeanExistsCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantTrue();
    }

    public CodegenExpression underlyingGetCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return exprDotMethod(underlyingExpression, method.getName());
    }

    public CodegenExpression underlyingExistsCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantTrue();
    }
}
