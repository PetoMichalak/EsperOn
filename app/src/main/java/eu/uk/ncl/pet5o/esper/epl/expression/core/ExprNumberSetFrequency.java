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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.type.FrequencyParameter;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

/**
 * Expression for use within crontab to specify a frequency.
 */
public class ExprNumberSetFrequency extends ExprNodeBase implements ExprForge, ExprEvaluator {
    private static final Logger log = LoggerFactory.getLogger(ExprNumberSetFrequency.class);
    private transient ExprEvaluator evaluator;
    private static final long serialVersionUID = -5389069399403078192L;

    @Override
    public ExprEvaluator getExprEvaluator() {
        return this;
    }

    public Class getEvaluationType() {
        return FrequencyParameter.class;
    }

    public ExprForge getForge() {
        return this;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return this;
    }

    public void toPrecedenceFreeEPL(StringWriter writer) {
        writer.append("*/");
        this.getChildNodes()[0].toEPL(writer, ExprPrecedenceEnum.MINIMUM);
    }

    public ExprPrecedenceEnum getPrecedence() {
        return ExprPrecedenceEnum.MINIMUM;
    }

    public boolean isConstantResult() {
        return this.getChildNodes()[0].isConstantResult();
    }

    public boolean equalsNode(ExprNode node, boolean ignoreStreamPrefix) {
        if (!(node instanceof ExprNumberSetFrequency)) {
            return false;
        }
        return true;
    }

    public ExprNode validate(ExprValidationContext validationContext) throws ExprValidationException {
        ExprForge forge = this.getChildNodes()[0].getForge();
        if (!(JavaClassHelper.isNumericNonFP(forge.getEvaluationType()))) {
            throw new ExprValidationException("Frequency operator requires an integer-type parameter");
        }
        evaluator = forge.getExprEvaluator();
        return null;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object value = evaluator.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        if (value == null) {
            return handleNumberSetFreqNullValue();
        } else {
            int intValue = ((Number) value).intValue();
            return new FrequencyParameter(intValue);
        }
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        ExprForge forge = this.getChildNodes()[0].getForge();
        Class evaluationType = forge.getEvaluationType();
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(FrequencyParameter.class, ExprNumberSetFrequency.class, codegenClassScope);
        CodegenBlock block = methodNode.getBlock()
                .declareVar(evaluationType, "value", forge.evaluateCodegen(requiredType, methodNode, exprSymbol, codegenClassScope));
        if (!evaluationType.isPrimitive()) {
            block.ifRefNull("value")
                .blockReturn(staticMethod(ExprNumberSetFrequency.class, "handleNumberSetFreqNullValue"));
        }
        block.methodReturn(newInstance(FrequencyParameter.class, SimpleNumberCoercerFactory.SimpleNumberCoercerInt.codegenInt(ref("value"), evaluationType)));
        return localMethod(methodNode);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return isConstantResult() ? ExprForgeComplexityEnum.NONE : ExprForgeComplexityEnum.INTER;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @return frequence params
     */
    public static FrequencyParameter handleNumberSetFreqNullValue() {
        log.warn("Null value returned for frequency parameter");
        return new FrequencyParameter(Integer.MAX_VALUE);
    }
}
