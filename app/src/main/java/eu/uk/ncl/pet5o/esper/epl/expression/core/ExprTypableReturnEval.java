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

/**
 * Interface for evaluators that select possible multi-valued results in a single select column,
 * such as subqueries and "new" and case+new combined.
 * <p>
 * When returning non-null results from {#getRowProperties},
 * the {@link ExprEvaluator#evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[], boolean, ExprEvaluatorContext)}
 * must return an instance of Map&lt;String, Object&gt; (HashMap is fine).
 * </p>
 * <p>
 * When returning non-null results, the
 * the evaluator must also return either Object[] results or Object[][],
 * each object-array following the same exact order as provided by the map,
 * matching the multi-row flag.
 * </p>
 */
public interface ExprTypableReturnEval extends ExprEvaluator {

    public Object[] evaluateTypableSingle(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context);

    public Object[][] evaluateTypableMulti(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context);
}
