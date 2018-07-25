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

import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.and;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.instanceOf;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class SelectExprProcessorEnumerationAtBeanCollEval implements ExprEvaluator {
    private final SelectExprProcessorEnumerationAtBeanCollForge forge;
    private final ExprEnumerationEval enumEval;

    public SelectExprProcessorEnumerationAtBeanCollEval(SelectExprProcessorEnumerationAtBeanCollForge forge, ExprEnumerationEval enumEval) {
        this.forge = forge;
        this.enumEval = enumEval;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        // the protocol is EventBean[]
        Object result = enumEval.evaluateGetROCollectionEvents(eventsPerStream, isNewData, context);
        if (result != null && result instanceof Collection) {
            Collection<EventBean> events = (Collection<EventBean>) result;
            return events.toArray(new eu.uk.ncl.pet5o.esper.client.EventBean[events.size()]);
        }
        return result;
    }

    public static CodegenExpression codegen(SelectExprProcessorEnumerationAtBeanCollForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean[].class, SelectExprProcessorEnumerationAtBeanCollEval.class, codegenClassScope);
        methodNode.getBlock()
                .declareVar(Object.class, "result", forge.enumerationForge.evaluateGetROCollectionEventsCodegen(methodNode, exprSymbol, codegenClassScope))
                .ifCondition(and(notEqualsNull(ref("result")), instanceOf(ref("result"), Collection.class)))
                .declareVar(Collection.class, eu.uk.ncl.pet5o.esper.client.EventBean.class, "events", cast(Collection.class, ref("result")))
                .blockReturn(cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("events"), "toArray", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, exprDotMethod(ref("events"), "size")))))
                .methodReturn(cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, ref("result")));
        return localMethod(methodNode);
    }

}
