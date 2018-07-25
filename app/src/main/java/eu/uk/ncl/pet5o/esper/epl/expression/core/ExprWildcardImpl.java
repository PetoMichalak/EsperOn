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
package eu.uk.ncl.pet5o.esper.epl.expression.core;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.type.WildcardParameter;

import java.io.StringWriter;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.enumValue;

/**
 * Expression for use within crontab to specify a wildcard.
 */
public class ExprWildcardImpl extends ExprNodeBase implements ExprForge, ExprEvaluator, ExprWildcard {
    private static final long serialVersionUID = -6098833102154556698L;

    public ExprWildcardImpl() {
    }

    public void toPrecedenceFreeEPL(StringWriter writer) {
        writer.append("*");
    }

    public ExprPrecedenceEnum getPrecedence() {
        return ExprPrecedenceEnum.UNARY;
    }

    public ExprEvaluator getExprEvaluator() {
        return this;
    }

    public boolean isConstantResult() {
        return true;
    }

    public boolean equalsNode(ExprNode node, boolean ignoreStreamPrefix) {
        return node instanceof ExprWildcardImpl;
    }

    public ExprNode validate(ExprValidationContext validationContext) throws ExprValidationException {
        return null;
    }

    public ExprForge getForge() {
        return this;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return this;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return WildcardParameter.INSTANCE;
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return enumValue(WildcardParameter.class, "INSTANCE");
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.NONE;
    }

    public Class getEvaluationType() {
        return WildcardParameter.class;
    }
}
