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
package eu.uk.ncl.pet5o.esper.epl.expression.time;

import eu.uk.ncl.pet5o.esper.client.util.TimePeriod;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

/**
 * Expression representing a time period.
 * <p>
 * Child nodes to this expression carry the actual parts and must return a numeric value.
 */
public interface ExprTimePeriod extends ExprNode {
    public boolean hasVariable();

    public ExprTimePeriodEvalDeltaConst constEvaluator(ExprEvaluatorContext context);

    public ExprTimePeriodEvalDeltaNonConst nonconstEvaluator();

    public double evaluateAsSeconds(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context);

    public TimePeriod evaluateGetTimePeriod(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context);

    /**
     * Indicator whether the time period has a day part child expression.
     *
     * @return true for part present, false for not present
     */
    public boolean isHasDay();

    /**
     * Indicator whether the time period has a hour part child expression.
     *
     * @return true for part present, false for not present
     */
    public boolean isHasHour();

    /**
     * Indicator whether the time period has a minute part child expression.
     *
     * @return true for part present, false for not present
     */
    public boolean isHasMinute();

    /**
     * Indicator whether the time period has a second part child expression.
     *
     * @return true for part present, false for not present
     */
    public boolean isHasSecond();

    /**
     * Indicator whether the time period has a millisecond part child expression.
     *
     * @return true for part present, false for not present
     */
    public boolean isHasMillisecond();

    /**
     * Indicator whether the time period has a microsecond part child expression.
     *
     * @return true for part present, false for not present
     */
    public boolean isHasMicrosecond();

    /**
     * Indicator whether the time period has a year part child expression.
     *
     * @return true for part present, false for not present
     */
    public boolean isHasYear();

    /**
     * Indicator whether the time period has a month part child expression.
     *
     * @return true for part present, false for not present
     */
    public boolean isHasMonth();

    /**
     * Indicator whether the time period has a week part child expression.
     *
     * @return true for part present, false for not present
     */
    public boolean isHasWeek();

    public boolean isConstantResult();

    CodegenExpression evaluateGetTimePeriodCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope);
    CodegenExpression evaluateAsSecondsCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope);
}
