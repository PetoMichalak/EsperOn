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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForgeComplexityEnum;
import eu.uk.ncl.pet5o.esper.epl.join.plan.FilterExprAnalyzerAffector;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

public class ExprDotNodeForgeStream extends ExprDotNodeForge {
    private final ExprDotNodeImpl parent;
    private final FilterExprAnalyzerAffector filterExprAnalyzerAffector;
    private final int streamNumber;
    private final EventType eventType;
    private final ExprDotForge[] evaluators;
    private final boolean method;
    private final Class evaluationType;

    public ExprDotNodeForgeStream(ExprDotNodeImpl parent, FilterExprAnalyzerAffector filterExprAnalyzerAffector, int streamNumber, EventType eventType, ExprDotForge[] evaluators, boolean method) {
        this.parent = parent;
        this.filterExprAnalyzerAffector = filterExprAnalyzerAffector;
        this.streamNumber = streamNumber;
        this.eventType = eventType;
        this.evaluators = evaluators;
        this.method = method;

        ExprDotForge last = evaluators[evaluators.length - 1];
        if (!method) {
            evaluationType = JavaClassHelper.getBoxedType(EPTypeHelper.getClassSingleValued(last.getTypeInfo()));
        } else {
            evaluationType = JavaClassHelper.getBoxedType(EPTypeHelper.getNormalizedClass(last.getTypeInfo()));
        }
    }

    public ExprEvaluator getExprEvaluator() {
        if (!method) {
            return new ExprDotNodeForgeStreamEvalEventBean(this, ExprDotNodeUtility.getEvaluators(evaluators));
        }
        return new ExprDotNodeForgeStreamEvalMethod(this, ExprDotNodeUtility.getEvaluators(evaluators));
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (!method) {
            return ExprDotNodeForgeStreamEvalEventBean.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
        }
        return ExprDotNodeForgeStreamEvalMethod.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public Class getEvaluationType() {
        return evaluationType;
    }

    public int getStreamNumber() {
        return streamNumber;
    }

    public boolean isReturnsConstantResult() {
        return false;
    }

    public FilterExprAnalyzerAffector getFilterExprAnalyzerAffector() {
        return filterExprAnalyzerAffector;
    }

    public Integer getStreamNumReferenced() {
        return streamNumber;
    }

    public String getRootPropertyName() {
        return null;
    }

    public ExprDotNodeImpl getForgeRenderable() {
        return parent;
    }

    public ExprDotForge[] getEvaluators() {
        return evaluators;
    }

    public EventType getEventType() {
        return eventType;
    }
}
