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
package eu.uk.ncl.pet5o.esper.event.map;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.PropertyAccessException;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.event.BaseNestableEventUtil;

import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.castUnderlying;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

/**
 * Getter for a dynamic mappeds property for maps.
 */
public class MapMappedPropertyGetter implements MapEventPropertyGetter, MapEventPropertyGetterAndMapped {
    private final String key;
    private final String fieldName;

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param map         map
     * @param fieldName   field
     * @param providedKey key
     * @return value
     * @throws PropertyAccessException exception
     */
    public static Object getMapMappedValue(Map<String, Object> map, String fieldName, String providedKey) throws PropertyAccessException {
        Object value = map.get(fieldName);
        return BaseNestableEventUtil.getMappedPropertyValue(value, providedKey);
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param map       map
     * @param fieldName field
     * @param key       key
     * @return value
     * @throws PropertyAccessException exception
     */
    public static boolean isMapExistsProperty(Map<String, Object> map, String fieldName, String key) {
        Object value = map.get(fieldName);
        return BaseNestableEventUtil.getMappedPropertyExists(value, key);
    }

    /**
     * Ctor.
     *
     * @param fieldName property name
     * @param key       get the element at
     */
    public MapMappedPropertyGetter(String fieldName, String key) {
        this.key = key;
        this.fieldName = fieldName;
    }

    public Object getMap(Map<String, Object> map) throws PropertyAccessException {
        return getMapMappedValue(map, fieldName, key);
    }

    public boolean isMapExistsProperty(Map<String, Object> map) {
        return isMapExistsProperty(map, fieldName, key);
    }

    public Object get(eu.uk.ncl.pet5o.esper.client.EventBean eventBean, String mapKey) throws PropertyAccessException {
        Map<String, Object> data = BaseNestableEventUtil.checkedCastUnderlyingMap(eventBean);
        return getMapMappedValue(data, fieldName, mapKey);
    }

    public Object get(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) throws PropertyAccessException {
        Map<String, Object> data = BaseNestableEventUtil.checkedCastUnderlyingMap(eventBean);
        return getMap(data);
    }

    public boolean isExistsProperty(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) {
        Map<String, Object> data = BaseNestableEventUtil.checkedCastUnderlyingMap(eventBean);
        return isMapExistsProperty(data);
    }

    public Object getFragment(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) {
        return null;
    }

    public CodegenExpression eventBeanGetCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingGetCodegen(castUnderlying(Map.class, beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression eventBeanExistsCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingExistsCodegen(castUnderlying(Map.class, beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression eventBeanFragmentCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public CodegenExpression underlyingGetCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return staticMethod(this.getClass(), "getMapMappedValue", underlyingExpression, constant(fieldName), constant(key));
    }

    public CodegenExpression underlyingExistsCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return staticMethod(this.getClass(), "isMapExistsProperty", underlyingExpression, constant(fieldName), constant(key));
    }

    public CodegenExpression underlyingFragmentCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public CodegenExpression eventBeanGetMappedCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope, CodegenExpression beanExpression, CodegenExpression key) {
        return staticMethod(this.getClass(), "getMapMappedValue", castUnderlying(Map.class, beanExpression), constant(fieldName), key);
    }
}
