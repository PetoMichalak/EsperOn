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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder;
import eu.uk.ncl.pet5o.esper.event.BaseNestableEventUtil;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

/**
 * Getter for map entry.
 */
public class ObjectArrayPropertyGetterDefaultMap extends ObjectArrayPropertyGetterDefaultBase {
    public ObjectArrayPropertyGetterDefaultMap(int propertyIndex, EventType fragmentEventType, EventAdapterService eventAdapterService) {
        super(propertyIndex, fragmentEventType, eventAdapterService);
    }

    protected Object handleCreateFragment(Object value) {
        return BaseNestableEventUtil.handleBNCreateFragmentMap(value, fragmentEventType, eventAdapterService);
    }

    protected CodegenExpression handleCreateFragmentCodegen(CodegenExpression value, CodegenClassScope codegenClassScope) {
        CodegenMember mSvc = codegenClassScope.makeAddMember(EventAdapterService.class, eventAdapterService);
        CodegenMember mType = codegenClassScope.makeAddMember(EventType.class, fragmentEventType);
        return staticMethod(BaseNestableEventUtil.class, "handleBNCreateFragmentMap", value, CodegenExpressionBuilder.member(mType.getMemberId()), CodegenExpressionBuilder.member(mSvc.getMemberId()));
    }
}
