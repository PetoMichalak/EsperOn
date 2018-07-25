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
package eu.uk.ncl.pet5o.esper.epl.core.poll;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.db.PollExecStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.script.ExprNodeScript;
import eu.uk.ncl.pet5o.esper.epl.script.ExprNodeScriptEvaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MethodPollingExecStrategyScript implements PollExecStrategy {

    private final static Logger log = LoggerFactory.getLogger(MethodPollingExecStrategyScript.class);

    private final ExprNodeScriptEvaluator eval;

    public MethodPollingExecStrategyScript(ExprNodeScript scriptExpression) {
        eval = scriptExpression.getScriptExprEvaluator();
    }

    public void start() {
    }

    public List<EventBean> poll(Object[] lookupValues, ExprEvaluatorContext exprEvaluatorContext) {
        Object result = eval.evaluate(lookupValues, exprEvaluatorContext);
        if (!(result instanceof eu.uk.ncl.pet5o.esper.client.EventBean[])) {
            log.warn("Script expected return type EventBean[] does not match result " + result == null ? "null" : result.getClass().getName());
            return Collections.emptyList();
        }
        return Arrays.asList((eu.uk.ncl.pet5o.esper.client.EventBean[]) result);
    }

    public void done() {
    }

    public void destroy() {
    }
}
