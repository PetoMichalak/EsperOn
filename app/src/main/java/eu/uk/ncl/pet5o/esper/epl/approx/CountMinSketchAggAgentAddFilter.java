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
package eu.uk.ncl.pet5o.esper.epl.approx;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgentCodegenSymbols;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationState;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class CountMinSketchAggAgentAddFilter extends CountMinSketchAggAgentAdd {

    private final ExprEvaluator filter;

    public CountMinSketchAggAgentAddFilter(ExprEvaluator stringEvaluator, ExprEvaluator filter) {
        super(stringEvaluator);
        this.filter = filter;
    }

    @Override
    public void applyEnter(EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext, AggregationState aggregationState) {
        Boolean pass = (Boolean) filter.evaluate(eventsPerStream, true, exprEvaluatorContext);
        if (pass != null && pass) {
            Object value = stringEvaluator.evaluate(eventsPerStream, true, exprEvaluatorContext);
            CountMinSketchAggState state = (CountMinSketchAggState) aggregationState;
            state.add(value);
        }
    }

    public static CodegenExpression applyEnterCodegen(CountMinSketchAggAgentAddForge forge, CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        CodegenMethodNode method = parent.makeChild(void.class, CountMinSketchAggAgentAddFilter.class, classScope);
        if (forge.optionalFilterForge != null) {
            Class evalType = forge.optionalFilterForge.getEvaluationType();
            method.getBlock().declareVar(evalType, "pass", forge.optionalFilterForge.evaluateCodegen(evalType, method, symbols, classScope));
            if (!evalType.isPrimitive()) {
                method.getBlock().ifRefNull("pass").blockReturnNoValue();
            }
            method.getBlock().ifCondition(not(ref("pass"))).blockReturnNoValue();
        }
        Class evaluationType = forge.getStringEvaluator().getEvaluationType();
        method.getBlock()
                .declareVar(JavaClassHelper.getBoxedType(evaluationType), "value", forge.getStringEvaluator().evaluateCodegen(evaluationType, method, symbols, classScope))
                .declareVar(CountMinSketchAggState.class, "countMinSketch", cast(CountMinSketchAggState.class, symbols.getAddState(method)))
                .expression(exprDotMethod(ref("countMinSketch"), "add", ref("value")));
        return localMethod(method);
    }
}
