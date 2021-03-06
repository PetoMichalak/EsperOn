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
package eu.uk.ncl.pet5o.esper.epl.expression.funcs;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;

public class ExprCaseNodeForge implements ExprTypableReturnForge {
    private final ExprCaseNode parent;
    private final Class resultType;
    protected final LinkedHashMap<String, Object> mapResultType;
    private final boolean isNumericResult;
    private final boolean mustCoerce;
    private final SimpleNumberCoercer coercer;
    private final List<UniformPair<ExprNode>> whenThenNodeList;
    private final ExprNode optionalCompareExprNode;
    private final ExprNode optionalElseExprNode;

    ExprCaseNodeForge(ExprCaseNode parent, Class resultType, LinkedHashMap<String, Object> mapResultType, boolean isNumericResult, boolean mustCoerce, SimpleNumberCoercer coercer, List<UniformPair<ExprNode>> whenThenNodeList, ExprNode optionalCompareExprNode, ExprNode optionalElseExprNode) {
        this.parent = parent;
        this.resultType = resultType;
        this.mapResultType = mapResultType;
        this.isNumericResult = isNumericResult;
        this.mustCoerce = mustCoerce;
        this.coercer = coercer;
        this.whenThenNodeList = whenThenNodeList;
        this.optionalCompareExprNode = optionalCompareExprNode;
        this.optionalElseExprNode = optionalElseExprNode;
    }

    public List<UniformPair<ExprNode>> getWhenThenNodeList() {
        return whenThenNodeList;
    }

    public ExprNode getOptionalCompareExprNode() {
        return optionalCompareExprNode;
    }

    public ExprNode getOptionalElseExprNode() {
        return optionalElseExprNode;
    }

    public ExprCaseNode getForgeRenderable() {
        return parent;
    }

    public Class getEvaluationType() {
        return resultType;
    }

    boolean isNumericResult() {
        return isNumericResult;
    }

    public boolean isMustCoerce() {
        return mustCoerce;
    }

    public SimpleNumberCoercer getCoercer() {
        return coercer;
    }

    public ExprEvaluator getExprEvaluator() {
        List<UniformPair<ExprEvaluator>> evals = new ArrayList<>();
        for (UniformPair<ExprNode> pair : whenThenNodeList) {
            evals.add(new UniformPair<>(pair.getFirst().getForge().getExprEvaluator(), pair.getSecond().getForge().getExprEvaluator()));
        }
        if (!parent.isCase2()) {
            return new ExprCaseNodeForgeEvalSyntax1(this, evals, optionalElseExprNode == null ? null : optionalElseExprNode.getForge().getExprEvaluator());
        } else {
            return new ExprCaseNodeForgeEvalSyntax2(this, evals, optionalCompareExprNode.getForge().getExprEvaluator(), optionalElseExprNode == null ? null : optionalElseExprNode.getForge().getExprEvaluator());
        }
    }

    public ExprTypableReturnEval getTypableReturnEvaluator() {
        return new ExprCaseNodeForgeEvalTypable(this);
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (!parent.isCase2()) {
            return ExprCaseNodeForgeEvalSyntax1.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
        } else {
            return ExprCaseNodeForgeEvalSyntax2.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
        }
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public CodegenExpression evaluateTypableSingleCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return ExprCaseNodeForgeEvalTypable.codegenTypeableSingle(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public CodegenExpression evaluateTypableMultiCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public Boolean isMultirow() {
        return mapResultType == null ? null : false;
    }

    public LinkedHashMap<String, Object> getRowProperties() throws ExprValidationException {
        return mapResultType;
    }
}
