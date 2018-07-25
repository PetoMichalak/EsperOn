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
package eu.uk.ncl.pet5o.esper.epl.table.merge;

import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprNodeCompiler;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.epl.table.onaction.TableOnMergeViewChangeHandler;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import java.util.List;

public class TableOnMergeMatch {
    private ExprEvaluator optionalCond;
    private List<TableOnMergeAction> actions;

    public TableOnMergeMatch(ExprNode optionalCond, List<TableOnMergeAction> actions, EngineImportService engineImportService, String statementName) {
        this.optionalCond = optionalCond != null ? ExprNodeCompiler.allocateEvaluator(optionalCond.getForge(), engineImportService, this.getClass(), false, statementName) : null;
        this.actions = actions;
    }

    public boolean isApplies(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        if (optionalCond == null) {
            return true;
        }

        Object result = optionalCond.evaluate(eventsPerStream, true, context);
        return result != null && (Boolean) result;
    }

    public void apply(eu.uk.ncl.pet5o.esper.client.EventBean matchingEvent,
                      eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream,
                      TableStateInstance stateInstance,
                      TableOnMergeViewChangeHandler changeHandlerAdded,
                      TableOnMergeViewChangeHandler changeHandlerRemoved,
                      ExprEvaluatorContext context) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qInfraMergeWhenThenActions(actions.size());
        }

        int count = -1;
        for (TableOnMergeAction action : actions) {
            count++;

            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().qInfraMergeWhenThenActionItem(count, action.getName());
                boolean applies = action.isApplies(eventsPerStream, context);
                if (applies) {
                    action.apply(matchingEvent, eventsPerStream, stateInstance, changeHandlerAdded, changeHandlerRemoved, context);
                }
                InstrumentationHelper.get().aInfraMergeWhenThenActionItem(applies);
                continue;
            }

            if (action.isApplies(eventsPerStream, context)) {
                action.apply(matchingEvent, eventsPerStream, stateInstance, changeHandlerAdded, changeHandlerRemoved, context);
            }
        }
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aInfraMergeWhenThenActions();
        }
    }
}
