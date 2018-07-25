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
package eu.uk.ncl.pet5o.esper.epl.core.select;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEnumerationEval;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;

import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsIdentity;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class SelectExprProcessorEnumerationCollEvalFirstRow implements ExprEvaluator {
    private final SelectExprProcessorEnumerationCollForge forge;
    private final ExprEnumerationEval enumeration;

    public SelectExprProcessorEnumerationCollEvalFirstRow(SelectExprProcessorEnumerationCollForge forge, ExprEnumerationEval enumeration) {
        this.forge = forge;
        this.enumeration = enumeration;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Collection<EventBean> events = enumeration.evaluateGetROCollectionEvents(eventsPerStream, isNewData, exprEvaluatorContext);
        if (events == null || events.size() == 0) {
            return null;
        }
        return EventBeanUtility.getNonemptyFirstEvent(events);
    }

    public static CodegenExpression codegen(SelectExprProcessorEnumerationCollForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, SelectExprProcessorEnumerationCollEval.class, codegenClassScope);

        methodNode.getBlock()
                .declareVar(Collection.class, eu.uk.ncl.pet5o.esper.client.EventBean.class, "events", forge.enumerationForge.evaluateGetROCollectionEventsCodegen(methodNode, exprSymbol, codegenClassScope))
                .ifRefNullReturnNull("events")
                .ifCondition(equalsIdentity(exprDotMethod(ref("events"), "size"), constant(0)))
                .blockReturn(constantNull())
                .methodReturn(staticMethod(EventBeanUtility.class, "getNonemptyFirstEvent", ref("events")));
        return localMethod(methodNode);
    }
}
