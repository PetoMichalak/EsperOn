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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.event.BaseNestableEventUtil;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

public class ObjectArrayNestedEntryPropertyGetterArrayObjectArray extends ObjectArrayNestedEntryPropertyGetterBase {

    private final int index;
    private final ObjectArrayEventPropertyGetter getter;

    public ObjectArrayNestedEntryPropertyGetterArrayObjectArray(int propertyIndex, EventType fragmentType, EventAdapterService eventAdapterService, int index, ObjectArrayEventPropertyGetter getter) {
        super(propertyIndex, fragmentType, eventAdapterService);
        this.index = index;
        this.getter = getter;
    }

    public Object handleNestedValue(Object value) {
        return BaseNestableEventUtil.handleNestedValueArrayWithObjectArray(value, index, getter);
    }

    public Object handleNestedValueFragment(Object value) {
        return BaseNestableEventUtil.handleNestedValueArrayWithObjectArrayFragment(value, index, getter, fragmentType, eventAdapterService);
    }

    public boolean handleNestedValueExists(Object value) {
        return BaseNestableEventUtil.handleNestedValueArrayWithObjectArrayExists(value, index, getter);
    }

    public CodegenExpression handleNestedValueCodegen(CodegenExpression refName, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return BaseNestableEventUtil.handleNestedValueArrayWithObjectArrayCodegen(index, getter, refName, codegenMethodScope, codegenClassScope, this.getClass());
    }

    public CodegenExpression handleNestedValueExistsCodegen(CodegenExpression refName, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return BaseNestableEventUtil.handleNestedValueArrayWithObjectArrayExistsCodegen(index, getter, refName, codegenMethodScope, codegenClassScope, this.getClass());
    }

    public CodegenExpression handleNestedValueFragmentCodegen(CodegenExpression refName, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return BaseNestableEventUtil.handleNestedValueArrayWithObjectArrayFragmentCodegen(index, getter, refName, codegenMethodScope, codegenClassScope, this.getClass());
    }
}
