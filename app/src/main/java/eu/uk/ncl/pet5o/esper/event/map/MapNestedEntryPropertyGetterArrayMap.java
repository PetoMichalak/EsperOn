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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.event.BaseNestableEventUtil;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

/**
 * A getter that works on EventBean events residing within a Map as an event property.
 */
public class MapNestedEntryPropertyGetterArrayMap extends MapNestedEntryPropertyGetterBase {

    private final int index;
    private final MapEventPropertyGetter getter;

    public MapNestedEntryPropertyGetterArrayMap(String propertyMap, EventType fragmentType, EventAdapterService eventAdapterService, int index, MapEventPropertyGetter getter) {
        super(propertyMap, fragmentType, eventAdapterService);
        this.index = index;
        this.getter = getter;
    }

    public Object handleNestedValue(Object value) {
        return BaseNestableEventUtil.handleNestedValueArrayWithMap(value, index, getter);
    }

    public Object handleNestedValueFragment(Object value) {
        return BaseNestableEventUtil.handleBNNestedValueArrayWithMapFragment(value, index, getter, eventAdapterService, fragmentType);
    }

    public CodegenExpression handleNestedValueCodegen(CodegenExpression name, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return BaseNestableEventUtil.handleNestedValueArrayWithMapCode(index, getter, name, codegenMethodScope, codegenClassScope, this.getClass());
    }

    public CodegenExpression handleNestedValueFragmentCodegen(CodegenExpression name, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return BaseNestableEventUtil.handleBNNestedValueArrayWithMapFragmentCode(index, getter, name, codegenMethodScope, codegenClassScope, eventAdapterService, fragmentType, this.getClass());
    }
}
