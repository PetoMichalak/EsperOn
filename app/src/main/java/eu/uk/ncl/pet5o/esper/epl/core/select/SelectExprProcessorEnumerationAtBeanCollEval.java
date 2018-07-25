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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.codegen.base.CodegenClassScope;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.base.CodegenMethodScope;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.epl.expression.core.ExprEnumerationEval;
import com.espertech.esper.epl.expression.core.ExprEvaluator;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Collection;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.and;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.instanceOf;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class SelectExprProcessorEnumerationAtBeanCollEval implements ExprEvaluator {
    private final SelectExprProcessorEnumerationAtBeanCollForge forge;
    private final ExprEnumerationEval enumEval;

    public SelectExprProcessorEnumerationAtBeanCollEval(SelectExprProcessorEnumerationAtBeanCollForge forge, ExprEnumerationEval enumEval) {
        this.forge = forge;
        this.enumEval = enumEval;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        // the protocol is EventBean[]
        Object result = enumEval.evaluateGetROCollectionEvents(eventsPerStream, isNewData, context);
        if (result != null && result instanceof Collection) {
            Collection<EventBean> events = (Collection<EventBean>) result;
            return events.toArray(new com.espertech.esper.client.EventBean[events.size()]);
        }
        return result;
    }

    public static CodegenExpression codegen(SelectExprProcessorEnumerationAtBeanCollForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(com.espertech.esper.client.EventBean[].class, SelectExprProcessorEnumerationAtBeanCollEval.class, codegenClassScope);
        methodNode.getBlock()
                .declareVar(Object.class, "result", forge.enumerationForge.evaluateGetROCollectionEventsCodegen(methodNode, exprSymbol, codegenClassScope))
                .ifCondition(and(notEqualsNull(ref("result")), instanceOf(ref("result"), Collection.class)))
                .declareVar(Collection.class, com.espertech.esper.client.EventBean.class, "events", cast(Collection.class, ref("result")))
                .blockReturn(cast(com.espertech.esper.client.EventBean[].class, exprDotMethod(ref("events"), "toArray", newArrayByLength(com.espertech.esper.client.EventBean.class, exprDotMethod(ref("events"), "size")))))
                .methodReturn(cast(com.espertech.esper.client.EventBean[].class, ref("result")));
        return localMethod(methodNode);
    }

}
