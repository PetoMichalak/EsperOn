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
import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.client.PropertyAccessException;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.castUnderlying;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

/**
 * Getter for one or more levels deep nested properties.
 */
public class NestedPropertyGetter extends BaseNativePropertyGetter implements BeanEventPropertyGetter {
    private final BeanEventPropertyGetter[] getterChain;

    /**
     * Ctor.
     *
     * @param getterChain         is the chain of getters to retrieve each nested property
     * @param eventAdapterService is the cache and factory for event bean types and event wrappers
     * @param finalPropertyType   type of the entry returned
     * @param finalGenericType    generic type parameter of the entry returned, if any
     */
    public NestedPropertyGetter(List<EventPropertyGetter> getterChain, EventAdapterService eventAdapterService, Class finalPropertyType, Class finalGenericType) {
        super(eventAdapterService, finalPropertyType, finalGenericType);
        this.getterChain = new BeanEventPropertyGetter[getterChain.size()];

        for (int i = 0; i < getterChain.size(); i++) {
            this.getterChain[i] = (BeanEventPropertyGetter) getterChain.get(i);
        }
    }

    public Object getBeanProp(Object value) throws PropertyAccessException {
        if (value == null) {
            return value;
        }

        for (int i = 0; i < getterChain.length; i++) {
            value = getterChain[i].getBeanProp(value);

            if (value == null) {
                return null;
            }
        }
        return value;
    }

    public boolean isBeanExistsProperty(Object value) {
        if (value == null) {
            return false;
        }

        int lastElementIndex = getterChain.length - 1;

        // walk the getter chain up to the previous-to-last element, returning its object value.
        // any null values in between mean the property does not exists
        for (int i = 0; i < getterChain.length - 1; i++) {
            value = getterChain[i].getBeanProp(value);

            if (value == null) {
                return false;
            }
        }

        return getterChain[lastElementIndex].isBeanExistsProperty(value);
    }

    public Object get(EventBean eventBean) throws PropertyAccessException {
        return getBeanProp(eventBean.getUnderlying());
    }

    public boolean isExistsProperty(EventBean eventBean) {
        return isBeanExistsProperty(eventBean.getUnderlying());
    }

    public Class getBeanPropType() {
        return getterChain[getterChain.length - 1].getBeanPropType();
    }

    public Class getTargetType() {
        return getterChain[0].getTargetType();
    }

    public CodegenExpression eventBeanGetCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingGetCodegen(castUnderlying(getTargetType(), beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression eventBeanExistsCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingExistsCodegen(castUnderlying(getTargetType(), beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression underlyingGetCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(getBeanPropCodegen(codegenMethodScope, codegenClassScope, false), underlyingExpression);
    }

    public CodegenExpression underlyingExistsCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(getBeanPropCodegen(codegenMethodScope, codegenClassScope, true), underlyingExpression);
    }

    private CodegenMethodNode getBeanPropCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope, boolean exists) {
        CodegenBlock block = codegenMethodScope.makeChild(exists ? boolean.class : JavaClassHelper.getBoxedType(getterChain[getterChain.length - 1].getBeanPropType()), this.getClass(), codegenClassScope).addParam(getterChain[0].getTargetType(), "value").getBlock();
        if (!exists) {
            block.ifRefNullReturnNull("value");
        } else {
            block.ifRefNullReturnFalse("value");
        }
        String lastName = "value";
        for (int i = 0; i < getterChain.length - 1; i++) {
            String varName = "l" + i;
            block.declareVar(getterChain[i].getBeanPropType(), varName, getterChain[i].underlyingGetCodegen(ref(lastName), codegenMethodScope, codegenClassScope));
            lastName = varName;
            if (!exists) {
                block.ifRefNullReturnNull(lastName);
            } else {
                block.ifRefNullReturnFalse(lastName);
            }
        }
        if (!exists) {
            return block.methodReturn(getterChain[getterChain.length - 1].underlyingGetCodegen(ref(lastName), codegenMethodScope, codegenClassScope));
        } else {
            return block.methodReturn(getterChain[getterChain.length - 1].underlyingExistsCodegen(ref(lastName), codegenMethodScope, codegenClassScope));
        }
    }
}
