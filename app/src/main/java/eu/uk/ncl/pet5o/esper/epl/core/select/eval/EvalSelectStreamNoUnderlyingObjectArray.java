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
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.SelectClauseStreamCompiledSpec;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;

public class EvalSelectStreamNoUnderlyingObjectArray extends EvalSelectStreamBaseObjectArray implements SelectExprProcessor {

    public EvalSelectStreamNoUnderlyingObjectArray(SelectExprForgeContext selectExprForgeContext, EventType resultEventType, List<SelectClauseStreamCompiledSpec> namedStreams, boolean usingWildcard) {
        super(selectExprForgeContext, resultEventType, namedStreams, usingWildcard);
    }

    protected eu.uk.ncl.pet5o.esper.client.EventBean processSpecific(Object[] props, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        return super.getContext().getEventAdapterService().adapterForTypedObjectArray(props, super.getResultEventType());
    }

    protected CodegenExpression processSpecificCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenExpressionRef props, CodegenClassScope codegenClassScope) {
        return exprDotMethod(CodegenExpressionBuilder.member(memberEventAdapterService.getMemberId()), "adapterForTypedObjectArray", props, CodegenExpressionBuilder.member(memberResultEventType.getMemberId()));
    }
}
