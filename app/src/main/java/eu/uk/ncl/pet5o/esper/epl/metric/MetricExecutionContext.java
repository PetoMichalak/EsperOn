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
package eu.uk.ncl.pet5o.esper.epl.metric;

import eu.uk.ncl.pet5o.esper.client.EPRuntime;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;

/**
 * Execution context for metrics reporting executions.
 */
public class MetricExecutionContext {
    private final EPServicesContext epServicesContext;
    private final EPRuntime runtime;
    private final StatementMetricRepository statementMetricRepository;

    /**
     * Ctor.
     *
     * @param epServicesContext         services context
     * @param runtime                   for routing events
     * @param statementMetricRepository for getting statement data
     */
    public MetricExecutionContext(EPServicesContext epServicesContext, EPRuntime runtime, StatementMetricRepository statementMetricRepository) {
        this.epServicesContext = epServicesContext;
        this.runtime = runtime;
        this.statementMetricRepository = statementMetricRepository;
    }

    /**
     * Returns services.
     *
     * @return services
     */
    public EPServicesContext getServices() {
        return epServicesContext;
    }

    /**
     * Returns runtime
     *
     * @return runtime
     */
    public EPRuntime getRuntime() {
        return runtime;
    }

    /**
     * Returns statement metric holder
     *
     * @return holder for metrics
     */
    public StatementMetricRepository getStatementMetricRepository() {
        return statementMetricRepository;
    }
}
