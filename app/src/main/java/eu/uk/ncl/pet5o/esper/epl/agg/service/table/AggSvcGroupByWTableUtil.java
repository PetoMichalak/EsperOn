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
package eu.uk.ncl.pet5o.esper.epl.agg.service.table;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgent;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgentCodegenSymbols;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgentForge;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceCodegenUtil;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableColumnMethodPair;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionCodegenType;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class AggSvcGroupByWTableUtil {
    public static CodegenExpression[] getMethodEnterLeave(TableColumnMethodPair[] methodPairs, CodegenMethodNode method, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        CodegenExpression[] expressions = new CodegenExpression[methodPairs.length];
        for (int i = 0; i < methodPairs.length; i++) {
            expressions[i] = computeCompositeKeyCodegen(methodPairs[i].getForges(), method, symbols, classScope);
        }
        return expressions;
    }

    public static CodegenExpression[] getAccessEnterLeave(boolean enter, AggregationAgentForge[] agentForges, AggregationAgent[] agents, CodegenMethodNode method, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        CodegenMember agentsMember = classScope.makeAddMember(AggregationAgent[].class, agents);
        CodegenExpression[] expressions = new CodegenExpression[agentForges.length];
        for (int i = 0; i < agentForges.length; i++) {
            if (agentForges[i].getPluginCodegenType() == PlugInAggregationMultiFunctionCodegenType.CODEGEN_NONE) {
                expressions[i] = exprDotMethod(arrayAtIndex(member(agentsMember.getMemberId()), constant(i)), enter ? "applyEnter" : "applyLeave", symbols.getAddEPS(method), symbols.getAddExprEvalCtx(method), symbols.getAddState(method));
            } else {
                expressions[i] = enter ? agentForges[i].applyEnterCodegen(method, symbols, classScope) : agentForges[i].applyLeaveCodegen(method, symbols, classScope);
            }
        }
        return expressions;
    }

    private static CodegenExpression computeCompositeKeyCodegen(ExprForge[] forges, CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        CodegenMethodNode method = parent.makeChild(Object.class, AggregationServiceCodegenUtil.class, classScope);
        if (forges.length == 1) {
            CodegenExpression expression = forges[0].evaluateCodegen(Object.class, method, symbols, classScope);
            method.getBlock().methodReturn(expression);
        } else {
            method.getBlock().declareVar(Object[].class, "keys", newArrayByLength(Object.class, constant(forges.length)));
            for (int i = 0; i < forges.length; i++) {
                method.getBlock().assignArrayElement("keys", constant(i), forges[i].evaluateCodegen(Object.class, method, symbols, classScope));
            }
            method.getBlock().methodReturn(ref("keys"));
        }
        return localMethod(method);
    }
}
