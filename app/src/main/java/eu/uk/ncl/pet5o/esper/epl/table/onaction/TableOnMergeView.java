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
package eu.uk.ncl.pet5o.esper.epl.table.onaction;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordWMatchExprLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.spec.OnTriggerType;
import eu.uk.ncl.pet5o.esper.epl.table.merge.TableOnMergeMatch;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import java.util.List;

public class TableOnMergeView extends TableOnViewBase {
    private final TableOnMergeViewFactory parent;

    public TableOnMergeView(SubordWMatchExprLookupStrategy lookupStrategy, TableStateInstance rootView, ExprEvaluatorContext exprEvaluatorContext, TableMetadata metadata, TableOnMergeViewFactory parent) {
        super(lookupStrategy, rootView, exprEvaluatorContext, metadata, parent.getOnMergeHelper().isRequiresWriteLock());
        this.parent = parent;
    }

    public void handleMatching(eu.uk.ncl.pet5o.esper.client.EventBean[] triggerEvents, eu.uk.ncl.pet5o.esper.client.EventBean[] matchingEvents) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qInfraOnAction(OnTriggerType.ON_MERGE, triggerEvents, matchingEvents);
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[3]; // first:table, second: trigger, third:before-update (optional)

        boolean postResultsToListeners = parent.getStatementResultService().isMakeNatural() || parent.getStatementResultService().isMakeSynthetic();
        TableOnMergeViewChangeHandler changeHandlerRemoved = null;
        TableOnMergeViewChangeHandler changeHandlerAdded = null;
        if (postResultsToListeners) {
            changeHandlerRemoved = new TableOnMergeViewChangeHandler(parent.getTableMetadata());
            changeHandlerAdded = new TableOnMergeViewChangeHandler(parent.getTableMetadata());
        }

        if ((matchingEvents == null) || (matchingEvents.length == 0)) {

            List<TableOnMergeMatch> unmatched = parent.getOnMergeHelper().getUnmatched();

            for (eu.uk.ncl.pet5o.esper.client.EventBean triggerEvent : triggerEvents) {
                eventsPerStream[1] = triggerEvent;
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().qInfraMergeWhenThens(false, triggerEvent, unmatched.size());
                }

                int count = -1;
                for (TableOnMergeMatch action : unmatched) {
                    count++;

                    if (InstrumentationHelper.ENABLED) {
                        InstrumentationHelper.get().qInfraMergeWhenThenItem(false, count);
                    }
                    if (!action.isApplies(eventsPerStream, super.getExprEvaluatorContext())) {
                        if (InstrumentationHelper.ENABLED) {
                            InstrumentationHelper.get().aInfraMergeWhenThenItem(false, false);
                        }
                        continue;
                    }
                    action.apply(null, eventsPerStream, tableStateInstance, changeHandlerAdded, changeHandlerRemoved, super.getExprEvaluatorContext());
                    if (InstrumentationHelper.ENABLED) {
                        InstrumentationHelper.get().aInfraMergeWhenThenItem(false, true);
                    }
                    break;  // apply no other actions
                }
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aInfraMergeWhenThens(false);
                }
            }
        } else {

            List<TableOnMergeMatch> matched = parent.getOnMergeHelper().getMatched();

            for (eu.uk.ncl.pet5o.esper.client.EventBean triggerEvent : triggerEvents) {
                eventsPerStream[1] = triggerEvent;
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().qInfraMergeWhenThens(true, triggerEvent, matched.size());
                }

                for (eu.uk.ncl.pet5o.esper.client.EventBean matchingEvent : matchingEvents) {
                    eventsPerStream[0] = matchingEvent;

                    int count = -1;
                    for (TableOnMergeMatch action : matched) {
                        count++;

                        if (InstrumentationHelper.ENABLED) {
                            InstrumentationHelper.get().qInfraMergeWhenThenItem(true, count);
                        }
                        if (!action.isApplies(eventsPerStream, super.getExprEvaluatorContext())) {
                            if (InstrumentationHelper.ENABLED) {
                                InstrumentationHelper.get().aInfraMergeWhenThenItem(true, false);
                            }
                            continue;
                        }
                        action.apply(matchingEvent, eventsPerStream, tableStateInstance, changeHandlerAdded, changeHandlerRemoved, super.getExprEvaluatorContext());
                        if (InstrumentationHelper.ENABLED) {
                            InstrumentationHelper.get().aInfraMergeWhenThenItem(true, true);
                        }
                        break;  // apply no other actions
                    }
                }
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aInfraMergeWhenThens(true);
                }
            }
        }

        // The on-delete listeners receive the events deleted, but only if there is interest
        if (postResultsToListeners) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] postedNew = changeHandlerAdded.getEvents();
            eu.uk.ncl.pet5o.esper.client.EventBean[] postedOld = changeHandlerRemoved.getEvents();
            if (postedNew != null || postedOld != null) {
                updateChildren(postedNew, postedOld);
            }
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aInfraOnAction();
        }
    }
}
