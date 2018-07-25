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
package eu.uk.ncl.pet5o.esper.core.support;

import eu.uk.ncl.pet5o.esper.client.Configuration;
import eu.uk.ncl.pet5o.esper.client.ConfigurationEngineDefaults;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import eu.uk.ncl.pet5o.esper.core.service.*;
import eu.uk.ncl.pet5o.esper.core.service.multimatch.MultiMatchHandlerFactoryImpl;
import eu.uk.ncl.pet5o.esper.core.thread.ThreadingServiceImpl;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationFactoryFactoryDefault;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceFactoryServiceImpl;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineSettingsService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowMgmtServiceImpl;
import eu.uk.ncl.pet5o.esper.epl.spec.PluggableObjectCollection;
import eu.uk.ncl.pet5o.esper.epl.spec.PluggableObjectRegistryImpl;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableServiceImpl;
import eu.uk.ncl.pet5o.esper.event.vaevent.ValueAddEventServiceImpl;
import eu.uk.ncl.pet5o.esper.filter.FilterBooleanExpressionFactoryImpl;
import eu.uk.ncl.pet5o.esper.pattern.PatternNodeFactoryImpl;
import eu.uk.ncl.pet5o.esper.pattern.PatternObjectResolutionServiceImpl;
import eu.uk.ncl.pet5o.esper.schedule.ScheduleBucket;
import eu.uk.ncl.pet5o.esper.schedule.SchedulingService;
import eu.uk.ncl.pet5o.esper.schedule.SchedulingServiceImpl;
import eu.uk.ncl.pet5o.esper.timer.TimeSourceServiceImpl;
import eu.uk.ncl.pet5o.esper.view.ViewEnumHelper;
import eu.uk.ncl.pet5o.esper.view.ViewFactoryContext;
import eu.uk.ncl.pet5o.esper.view.ViewResolutionServiceImpl;
import eu.uk.ncl.pet5o.esper.view.ViewServicePreviousFactoryImpl;

import java.net.URI;

public class SupportStatementContextFactory {
    public static ExprEvaluatorContext makeEvaluatorContext() {
        return new ExprEvaluatorContextStatement(null, false);
    }

    public static AgentInstanceContext makeAgentInstanceContext(SchedulingService stub) {
        return new AgentInstanceContext(makeContext(stub), null, -1, null, null, null);
    }

    public static AgentInstanceContext makeAgentInstanceContext() {
        return new AgentInstanceContext(makeContext(), null, -1, null, null, null);
    }

    public static AgentInstanceViewFactoryChainContext makeAgentInstanceViewFactoryContext(SchedulingService stub) {
        AgentInstanceContext agentInstanceContext = makeAgentInstanceContext(stub);
        return new AgentInstanceViewFactoryChainContext(agentInstanceContext, false, null, null);
    }

    public static AgentInstanceViewFactoryChainContext makeAgentInstanceViewFactoryContext() {
        AgentInstanceContext agentInstanceContext = makeAgentInstanceContext();
        return new AgentInstanceViewFactoryChainContext(agentInstanceContext, false, null, null);
    }

    public static ViewFactoryContext makeViewContext() {
        StatementContext stmtContext = makeContext();
        return new ViewFactoryContext(stmtContext, 1, "somenamespacetest", "somenametest", false, -1, false);
    }

    public static StatementContext makeContext() {
        SupportSchedulingServiceImpl sched = new SupportSchedulingServiceImpl();
        return makeContext(sched);
    }

    public static StatementContext makeContext(int statementId) {
        SupportSchedulingServiceImpl sched = new SupportSchedulingServiceImpl();
        return makeContext(statementId, sched);
    }

    public static StatementContext makeContext(SchedulingService stub) {
        return makeContext(1, stub);
    }

    public static StatementContext makeContext(int statementId, SchedulingService stub) {
        Configuration config = new Configuration();
        config.getEngineDefaults().getViewResources().setAllowMultipleExpiryPolicies(true);

        TimeSourceServiceImpl timeSourceService = new TimeSourceServiceImpl();
        StatementContextEngineServices stmtEngineServices = new StatementContextEngineServices("engURI",
                SupportEventAdapterService.getService(),
                new NamedWindowMgmtServiceImpl(false, null),
                null, new TableServiceImpl(),
                new EngineSettingsService(new Configuration().getEngineDefaults(), new URI[0]),
                new ValueAddEventServiceImpl(),
                config,
                null,
                null,
                null,
                null,
                new StatementEventTypeRefImpl(), null, null, null, null, null, new ViewServicePreviousFactoryImpl(), null, new PatternNodeFactoryImpl(), new FilterBooleanExpressionFactoryImpl(), timeSourceService, SupportEngineImportServiceFactory.make(), AggregationFactoryFactoryDefault.INSTANCE, new SchedulingServiceImpl(timeSourceService), null);

        return new StatementContext(stmtEngineServices,
                stub,
                new ScheduleBucket(1),
                new EPStatementHandle(statementId, "name1", "epl1", StatementType.SELECT, "epl1", false, null, 0, false, false, new MultiMatchHandlerFactoryImpl().getDefaultHandler()),
                new ViewResolutionServiceImpl(new PluggableObjectRegistryImpl(new PluggableObjectCollection[]{ViewEnumHelper.getBuiltinViews()}), null, null),
                new PatternObjectResolutionServiceImpl(null),
                null,
                null,
                null,
                null,
                new StatementResultServiceImpl("name", null, null, new ThreadingServiceImpl(new ConfigurationEngineDefaults.Threading())), // statement result svc
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                AggregationServiceFactoryServiceImpl.DEFAULT_FACTORY,
                false,
                null, new StatementSemiAnonymousTypeRegistryImpl(), 0);
    }
}
