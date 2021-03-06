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
package eu.uk.ncl.pet5o.esper.core.start;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.collection.Pair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.StatementSpecCompiled;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorFactoryDesc;
import eu.uk.ncl.pet5o.esper.epl.expression.subquery.ExprSubselectNode;
import eu.uk.ncl.pet5o.esper.epl.spec.FilterStreamSpecCompiled;
import eu.uk.ncl.pet5o.esper.epl.spec.StreamSpecCompiled;
import eu.uk.ncl.pet5o.esper.event.EventTypeUtility;
import eu.uk.ncl.pet5o.esper.pattern.EvalFactoryNode;
import eu.uk.ncl.pet5o.esper.pattern.EvalFilterFactoryNode;
import eu.uk.ncl.pet5o.esper.view.ViewFactoryChain;

public class EPStatementStartMethodHelperUtil {
    public static Pair<ResultSetProcessor, AggregationService> startResultSetAndAggregation(ResultSetProcessorFactoryDesc resultSetProcessorPrototype, AgentInstanceContext agentInstanceContext, boolean isSubquery, Integer subqueryNumber) {
        AggregationService aggregationService = null;
        if (resultSetProcessorPrototype.getAggregationServiceFactoryDesc() != null) {
            aggregationService = resultSetProcessorPrototype.getAggregationServiceFactoryDesc().getAggregationServiceFactory().makeService(agentInstanceContext, agentInstanceContext.getEngineImportService(), isSubquery, subqueryNumber);
        }

        OrderByProcessor orderByProcessor = null;
        if (resultSetProcessorPrototype.getOrderByProcessorFactory() != null) {
            orderByProcessor = resultSetProcessorPrototype.getOrderByProcessorFactory().instantiate(agentInstanceContext);
        }

        ResultSetProcessor resultSetProcessor = resultSetProcessorPrototype.getResultSetProcessorFactory().instantiate(orderByProcessor, aggregationService, agentInstanceContext);

        return new Pair<ResultSetProcessor, AggregationService>(resultSetProcessor, aggregationService);
    }

    /**
     * Returns a stream name assigned for each stream, generated if none was supplied.
     *
     * @param streams - stream specifications
     * @return array of stream names
     */
    @SuppressWarnings({"StringContatenationInLoop"})
    protected static String[] determineStreamNames(StreamSpecCompiled[] streams) {
        String[] streamNames = new String[streams.length];
        for (int i = 0; i < streams.length; i++) {
            // Assign a stream name for joins, if not supplied
            streamNames[i] = streams[i].getOptionalStreamName();
            if (streamNames[i] == null) {
                streamNames[i] = "stream_" + i;
            }
        }
        return streamNames;
    }

    protected static boolean[] getHasIStreamOnly(boolean[] isNamedWindow, ViewFactoryChain[] unmaterializedViewChain) {
        boolean[] result = new boolean[unmaterializedViewChain.length];
        for (int i = 0; i < unmaterializedViewChain.length; i++) {
            if (isNamedWindow[i]) {
                continue;
            }
            result[i] = unmaterializedViewChain[i].getDataWindowViewFactoryCount() == 0;
        }
        return result;
    }

    protected static boolean determineSubquerySameStream(StatementSpecCompiled statementSpec, FilterStreamSpecCompiled filterStreamSpec) {
        for (ExprSubselectNode subselect : statementSpec.getSubSelectExpressions()) {
            StreamSpecCompiled streamSpec = subselect.getStatementSpecCompiled().getStreamSpecs()[0];
            if (!(streamSpec instanceof FilterStreamSpecCompiled)) {
                continue;
            }
            FilterStreamSpecCompiled filterStream = (FilterStreamSpecCompiled) streamSpec;
            EventType typeSubselect = filterStream.getFilterSpec().getFilterForEventType();
            EventType typeFiltered = filterStreamSpec.getFilterSpec().getFilterForEventType();
            if (EventTypeUtility.isTypeOrSubTypeOf(typeSubselect, typeFiltered) || EventTypeUtility.isTypeOrSubTypeOf(typeFiltered, typeSubselect)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean isConsumingFilters(EvalFactoryNode evalNode) {
        if (evalNode instanceof EvalFilterFactoryNode) {
            return ((EvalFilterFactoryNode) evalNode).getConsumptionLevel() != null;
        }
        boolean consumption = false;
        for (EvalFactoryNode child : evalNode.getChildNodes()) {
            consumption = consumption || isConsumingFilters(child);
        }
        return consumption;
    }
}
