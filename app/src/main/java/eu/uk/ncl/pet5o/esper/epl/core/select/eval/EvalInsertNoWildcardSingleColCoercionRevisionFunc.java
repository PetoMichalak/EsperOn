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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.vaevent.ValueAddEventProcessor;
import eu.uk.ncl.pet5o.esper.util.TriFunction;

import java.util.Collections;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class EvalInsertNoWildcardSingleColCoercionRevisionFunc extends EvalBaseFirstProp implements SelectExprProcessor {

    private final ValueAddEventProcessor vaeProcessor;
    private final EventType vaeInnerEventType;
    private final TriFunction<EventAdapterService, Object, EventType, eu.uk.ncl.pet5o.esper.client.EventBean> func;

    public EvalInsertNoWildcardSingleColCoercionRevisionFunc(SelectExprForgeContext selectExprForgeContext, EventType resultEventType, ValueAddEventProcessor vaeProcessor, EventType vaeInnerEventType, TriFunction<EventAdapterService, Object, EventType, eu.uk.ncl.pet5o.esper.client.EventBean> func) {
        super(selectExprForgeContext, resultEventType);
        this.vaeProcessor = vaeProcessor;
        this.vaeInnerEventType = vaeInnerEventType;
        this.func = func;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean processFirstCol(Object result) {
        eu.uk.ncl.pet5o.esper.client.EventBean wrappedEvent = func.apply(super.getEventAdapterService(), result, super.getResultEventType());
        return vaeProcessor.getValueAddEventBean(super.getEventAdapterService().adapterForTypedWrapper(wrappedEvent, Collections.emptyMap(), vaeInnerEventType));
    }

    protected CodegenExpression processFirstColCodegen(Class evaluationType, CodegenExpression expression, CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        CodegenMember memberProcessor = codegenClassScope.makeAddMember(ValueAddEventProcessor.class, vaeProcessor);
        CodegenMember memberType = codegenClassScope.makeAddMember(EventType.class, vaeInnerEventType);
        CodegenMember memberFunc = codegenClassScope.makeAddMember(TriFunction.class, func);
        CodegenExpression wrappedEvent = exprDotMethod(CodegenExpressionBuilder.member(memberFunc.getMemberId()), "apply", CodegenExpressionBuilder.member(memberEventAdapterService.getMemberId()), expression, CodegenExpressionBuilder.member(memberResultEventType.getMemberId()));
        CodegenExpression adapter = exprDotMethod(CodegenExpressionBuilder.member(memberEventAdapterService.getMemberId()), "adapterForTypedWrapper", wrappedEvent, staticMethod(Collections.class, "emptyMap"), CodegenExpressionBuilder.member(memberType.getMemberId()));
        return exprDotMethod(CodegenExpressionBuilder.member(memberProcessor.getMemberId()), "getValueAddEventBean", adapter);
    }
}
