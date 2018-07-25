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

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementResultService;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordWMatchExprLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.metric.MetricReportingServiceSPI;
import eu.uk.ncl.pet5o.esper.epl.metric.StatementMetricHandle;
import eu.uk.ncl.pet5o.esper.epl.table.merge.TableOnMergeHelper;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;

public class TableOnMergeViewFactory implements TableOnViewFactory {
    private final TableMetadata tableMetadata;
    private final TableOnMergeHelper onMergeHelper;
    private final StatementResultService statementResultService;
    private final StatementMetricHandle metricsHandle;
    private final MetricReportingServiceSPI metricReportingService;

    public TableOnMergeViewFactory(TableMetadata tableMetadata, TableOnMergeHelper onMergeHelper, StatementResultService statementResultService, StatementMetricHandle metricsHandle, MetricReportingServiceSPI metricReportingService) {
        this.tableMetadata = tableMetadata;
        this.onMergeHelper = onMergeHelper;
        this.statementResultService = statementResultService;
        this.metricsHandle = metricsHandle;
        this.metricReportingService = metricReportingService;
    }

    public TableOnView make(SubordWMatchExprLookupStrategy lookupStrategy, TableStateInstance tableState, AgentInstanceContext agentInstanceContext, ResultSetProcessor resultSetProcessor) {
        if (onMergeHelper.getInsertUnmatched() != null) {
            return new TableOnMergeInsertUnmatched(tableState, agentInstanceContext, tableMetadata, this);
        }
        return new TableOnMergeView(lookupStrategy, tableState, agentInstanceContext, tableMetadata, this);
    }

    public TableMetadata getTableMetadata() {
        return tableMetadata;
    }

    public TableOnMergeHelper getOnMergeHelper() {
        return onMergeHelper;
    }

    public StatementResultService getStatementResultService() {
        return statementResultService;
    }

    public StatementMetricHandle getMetricsHandle() {
        return metricsHandle;
    }

    public MetricReportingServiceSPI getMetricReportingService() {
        return metricReportingService;
    }
}
