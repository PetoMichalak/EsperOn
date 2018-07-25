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
package eu.uk.ncl.pet5o.esper.epl.enummethod.eval;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenNames;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenParams;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEnumerationEval;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.ArrayList;
import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class EnumIntersectForgeEval implements EnumEval {

    private final EnumIntersectForge forge;
    private final ExprEnumerationEval evaluator;

    public EnumIntersectForgeEval(EnumIntersectForge forge, ExprEnumerationEval evaluator) {
        this.forge = forge;
        this.evaluator = evaluator;
    }

    public Object evaluateEnumMethod(EventBean[] eventsLambda, Collection enumcoll, boolean isNewData, ExprEvaluatorContext context) {
        Collection other;
        if (forge.scalar) {
            other = evaluator.evaluateGetROCollectionScalar(eventsLambda, isNewData, context);
        } else {
            other = evaluator.evaluateGetROCollectionEvents(eventsLambda, isNewData, context);
        }

        return enumIntersectForgeEvalSet(other, enumcoll, forge.scalar);
    }

    public static CodegenExpression codegen(EnumIntersectForge forge, EnumForgeCodegenParams args, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        ExprForgeCodegenSymbol scope = new ExprForgeCodegenSymbol(false, null);
        CodegenMethodNode methodNode = codegenMethodScope.makeChildWithScope(Collection.class, EnumIntersectForgeEval.class, scope, codegenClassScope).addParam(EnumForgeCodegenNames.PARAMS);

        CodegenBlock block = methodNode.getBlock();
        if (forge.scalar) {
            block.declareVar(Collection.class, "other", forge.evaluatorForge.evaluateGetROCollectionScalarCodegen(methodNode, scope, codegenClassScope));
        } else {
            block.declareVar(Collection.class, "other", forge.evaluatorForge.evaluateGetROCollectionEventsCodegen(methodNode, scope, codegenClassScope));
        }
        block.methodReturn(staticMethod(EnumIntersectForgeEval.class, "enumIntersectForgeEvalSet", ref("other"), EnumForgeCodegenNames.REF_ENUMCOLL, constant(forge.scalar)));
        return localMethod(methodNode, args.getEps(), args.getEnumcoll(), args.getIsNewData(), args.getExprCtx());
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param other    other
     * @param enumcoll coll
     * @param scalar   indicator
     * @return intersection
     */
    public static Collection enumIntersectForgeEvalSet(Collection other, Collection enumcoll, boolean scalar) {
        if (other == null || other.isEmpty() || enumcoll.isEmpty()) {
            return enumcoll;
        }

        if (scalar) {
            ArrayList<Object> result = new ArrayList<Object>(enumcoll);
            result.retainAll(other);
            return result;
        }

        Collection<EventBean> targetEvents = (Collection<EventBean>) enumcoll;
        Collection<EventBean> sourceEvents = (Collection<EventBean>) other;
        ArrayList<EventBean> result = new ArrayList<EventBean>();

        // we compare event underlying
        for (EventBean targetEvent : targetEvents) {
            if (targetEvent == null) {
                result.add(null);
                continue;
            }

            boolean found = false;
            for (EventBean sourceEvent : sourceEvents) {
                if (targetEvent == sourceEvent) {
                    found = true;
                    break;
                }
                if (sourceEvent == null) {
                    continue;
                }
                if (targetEvent.getUnderlying().equals(sourceEvent.getUnderlying())) {
                    found = true;
                    break;
                }
            }

            if (found) {
                result.add(targetEvent);
            }
        }
        return result;
    }
}
