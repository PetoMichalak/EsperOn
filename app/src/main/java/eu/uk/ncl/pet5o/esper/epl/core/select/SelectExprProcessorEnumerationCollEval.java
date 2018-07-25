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
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class SelectExprProcessorEnumerationCollEval implements ExprEvaluator {
    private final SelectExprProcessorEnumerationCollForge forge;
    private final ExprEnumerationEval enumeration;

    public SelectExprProcessorEnumerationCollEval(SelectExprProcessorEnumerationCollForge forge, ExprEnumerationEval enumeration) {
        this.forge = forge;
        this.enumeration = enumeration;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Collection<EventBean> events = enumeration.evaluateGetROCollectionEvents(eventsPerStream, isNewData, exprEvaluatorContext);
        if (events == null) {
            return null;
        }
        return events.toArray(new com.espertech.esper.client.EventBean[events.size()]);
    }

    public static CodegenExpression codegen(SelectExprProcessorEnumerationCollForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(com.espertech.esper.client.EventBean[].class, SelectExprProcessorEnumerationCollEval.class, codegenClassScope);

        methodNode.getBlock()
                .declareVar(Collection.class, com.espertech.esper.client.EventBean.class, "events", forge.enumerationForge.evaluateGetROCollectionEventsCodegen(methodNode, exprSymbol, codegenClassScope))
                .ifRefNullReturnNull("events")
                .methodReturn(cast(com.espertech.esper.client.EventBean[].class, exprDotMethod(ref("events"), "toArray", newArrayByLength(com.espertech.esper.client.EventBean.class, exprDotMethod(ref("events"), "size")))));
        return localMethod(methodNode);
    }

}
