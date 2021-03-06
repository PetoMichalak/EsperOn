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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForgeComplexityEnum;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.join.plan.FilterExprAnalyzerAffector;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterIndexedSPI;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterMappedSPI;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

public class ExprDotNodeForgePropertyExpr extends ExprDotNodeForge {

    private final ExprDotNodeImpl parent;
    private final String statementName;
    private final String propertyName;
    private final int streamNum;
    private final ExprForge exprForge;
    private final Class propertyType;
    private final EventPropertyGetterIndexedSPI indexedGetter;
    private final EventPropertyGetterMappedSPI mappedGetter;

    protected ExprDotNodeForgePropertyExpr(ExprDotNodeImpl parent, String statementName, String propertyName, int streamNum, ExprForge exprForge, Class propertyType, EventPropertyGetterIndexedSPI indexedGetter, EventPropertyGetterMappedSPI mappedGetter) {
        this.parent = parent;
        this.statementName = statementName;
        this.propertyName = propertyName;
        this.streamNum = streamNum;
        this.exprForge = exprForge;
        this.propertyType = propertyType;
        this.indexedGetter = indexedGetter;
        this.mappedGetter = mappedGetter;
    }

    public ExprEvaluator getExprEvaluator() {
        if (indexedGetter != null) {
            return new ExprDotNodeForgePropertyExprEvalIndexed(this, exprForge.getExprEvaluator());
        }
        return new ExprDotNodeForgePropertyExprEvalMapped(this, exprForge.getExprEvaluator());
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (indexedGetter != null) {
            return ExprDotNodeForgePropertyExprEvalIndexed.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
        }
        return ExprDotNodeForgePropertyExprEvalMapped.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public Class getEvaluationType() {
        return propertyType;
    }

    public Class getType() {
        return getEvaluationType();
    }

    protected String getWarningText(String expectedType, Object received) {
        return "Statement '" + statementName + "' property " + propertyName + " parameter expression expected a value of " +
                expectedType + " but received " + received == null ? "null" : JavaClassHelper.getClassNameFullyQualPretty(received.getClass());
    }

    public int getStreamNum() {
        return streamNum;
    }

    public EventPropertyGetterIndexedSPI getIndexedGetter() {
        return indexedGetter;
    }

    public EventPropertyGetterMappedSPI getMappedGetter() {
        return mappedGetter;
    }

    public ExprDotNodeImpl getParent() {
        return parent;
    }

    public boolean isReturnsConstantResult() {
        return false;
    }

    public FilterExprAnalyzerAffector getFilterExprAnalyzerAffector() {
        return null;
    }

    public Integer getStreamNumReferenced() {
        return streamNum;
    }

    public String getRootPropertyName() {
        return null;
    }

    public ExprForge getExprForge() {
        return exprForge;
    }

    public ExprNode getForgeRenderable() {
        return parent;
    }
}
