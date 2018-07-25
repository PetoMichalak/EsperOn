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
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterAndMapped;
import eu.uk.ncl.pet5o.esper.event.vaevent.PropertyUtility;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.castUnderlying;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.event.bean.KeyedMapMethodPropertyGetter.getBeanPropInternalCodegen;

/**
 * Getter for a key property identified by a given key value of a map, using the CGLIB fast method.
 */
public class KeyedMapFastPropertyGetter extends BaseNativePropertyGetter implements BeanEventPropertyGetter, EventPropertyGetterAndMapped {
    private final FastMethod fastMethod;
    private final Object key;

    /**
     * Constructor.
     *
     * @param method              the underlying method
     * @param fastMethod          is the method to use to retrieve a value from the object.
     * @param key                 is the key to supply as parameter to the mapped property getter
     * @param eventAdapterService factory for event beans and event types
     */
    public KeyedMapFastPropertyGetter(Method method, FastMethod fastMethod, Object key, EventAdapterService eventAdapterService) {
        super(eventAdapterService, JavaClassHelper.getGenericReturnTypeMap(method, false), null);
        this.key = key;
        this.fastMethod = fastMethod;
    }

    public boolean isBeanExistsProperty(Object object) {
        return true; // Property exists as the property is not dynamic (unchecked)
    }

    public Object getBeanProp(Object object) throws PropertyAccessException {
        return getBeanPropInternal(object, key);
    }

    public Object get(eu.uk.ncl.pet5o.esper.client.EventBean eventBean, String mapKey) throws PropertyAccessException {
        return getBeanPropInternal(eventBean.getUnderlying(), mapKey);
    }

    public Object getBeanPropInternal(Object object, Object key) throws PropertyAccessException {
        try {
            Object result = fastMethod.invoke(object, null);
            if (!(result instanceof Map)) {
                return null;
            }
            Map resultMap = (Map) result;
            return resultMap.get(key);
        } catch (ClassCastException e) {
            throw PropertyUtility.getMismatchException(fastMethod.getJavaMethod(), object, e);
        } catch (InvocationTargetException e) {
            throw PropertyUtility.getInvocationTargetException(fastMethod.getJavaMethod(), e);
        }
    }

    public final Object get(eu.uk.ncl.pet5o.esper.client.EventBean obj) throws PropertyAccessException {
        Object underlying = obj.getUnderlying();
        return getBeanProp(underlying);
    }

    public String toString() {
        return "KeyedMapFastPropertyGetter " +
                " fastMethod=" + fastMethod.toString() +
                " key=" + key;
    }

    public boolean isExistsProperty(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) {
        return true; // Property exists as the property is not dynamic (unchecked)
    }

    public Class getBeanPropType() {
        return JavaClassHelper.getGenericReturnTypeMap(fastMethod.getJavaMethod(), false);
    }

    public Class getTargetType() {
        return fastMethod.getDeclaringClass();
    }

    public CodegenExpression eventBeanGetCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingGetCodegen(castUnderlying(getTargetType(), beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression eventBeanExistsCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantTrue();
    }

    public CodegenExpression underlyingGetCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(getBeanPropInternalCodegen(codegenMethodScope, getBeanPropType(), getTargetType(), fastMethod.getJavaMethod(), codegenClassScope), underlyingExpression, constant(key));
    }

    public CodegenExpression underlyingExistsCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantTrue();
    }

    public CodegenExpression eventBeanGetMappedCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope, CodegenExpression beanExpression, CodegenExpression key) {
        return localMethod(getBeanPropInternalCodegen(codegenMethodScope, getBeanPropType(), getTargetType(), fastMethod.getJavaMethod(), codegenClassScope), castUnderlying(getTargetType(), beanExpression), key);
    }
}
