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
package eu.uk.ncl.pet5o.esper.epl.core.select.eval;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.event.WrapperEventType;

import java.util.Collections;

public class EvalInsertNoWildcardSingleColCoercionAvroWrap extends EvalBaseFirstPropFromWrap implements SelectExprProcessor {

    public EvalInsertNoWildcardSingleColCoercionAvroWrap(SelectExprForgeContext selectExprForgeContext, WrapperEventType wrapper) {
        super(selectExprForgeContext, wrapper);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean processFirstCol(Object result) {
        eu.uk.ncl.pet5o.esper.client.EventBean wrappedEvent = super.getEventAdapterService().adapterForTypedAvro(result, wrapper.getUnderlyingEventType());
        return super.getEventAdapterService().adapterForTypedWrapper(wrappedEvent, Collections.emptyMap(), wrapper);
    }

    protected CodegenExpression processFirstColCodegen(Class evaluationType, CodegenExpression expression, CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return EvalInsertNoWildcardSingleColCoercionMapWrap.processFirstColCodegen(expression, memberEventAdapterService, codegenClassScope, wrapper, "adapterForTypedAvro", Object.class);
    }
}
