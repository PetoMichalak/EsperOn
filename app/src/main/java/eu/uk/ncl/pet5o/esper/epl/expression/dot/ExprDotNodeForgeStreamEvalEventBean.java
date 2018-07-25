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
package eu.uk.ncl.pet5o.esper.epl.expression.dot;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprDotNodeForgeStreamEvalEventBean implements ExprEvaluator {
    private final ExprDotNodeForgeStream forge;
    private final ExprDotEval[] evaluators;

    public ExprDotNodeForgeStreamEvalEventBean(ExprDotNodeForgeStream forge, ExprDotEval[] evaluators) {
        this.forge = forge;
        this.evaluators = evaluators;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprStreamEventMethod(forge.getForgeRenderable());
        }

        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventsPerStream[forge.getStreamNumber()];
        if (theEvent == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprStreamEventMethod(null);
            }
            return null;
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprDotChain(EPTypeHelper.singleEvent(theEvent.getEventType()), theEvent, evaluators);
        }
        Object inner = ExprDotNodeUtility.evaluateChain(forge.getEvaluators(), evaluators, theEvent, eventsPerStream, isNewData, exprEvaluatorContext);
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprDotChain();
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprStreamEventMethod(inner);
        }
        return inner;
    }

    public static CodegenExpression codegen(ExprDotNodeForgeStream forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(forge.getEvaluationType(), ExprDotNodeForgeStreamEvalEventBean.class, codegenClassScope);
        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);

        methodNode.getBlock()
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "event", arrayAtIndex(refEPS, constant(forge.getStreamNumber())))
                .ifRefNullReturnNull("event")
                .methodReturn(ExprDotNodeUtility.evaluateChainCodegen(methodNode, exprSymbol, codegenClassScope, ref("event"), eu.uk.ncl.pet5o.esper.client.EventBean.class, forge.getEvaluators(), null));
        return localMethod(methodNode);
    }
}
