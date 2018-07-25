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
package eu.uk.ncl.pet5o.esper.core.service;

import eu.uk.ncl.pet5o.esper.client.ConfigurationInformation;
import eu.uk.ncl.pet5o.esper.core.context.mgr.ContextManagementService;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationFactoryFactory;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineSettingsService;
import eu.uk.ncl.pet5o.esper.epl.declexpr.ExprDeclaredService;
import eu.uk.ncl.pet5o.esper.epl.lookup.EventTableIndexService;
import eu.uk.ncl.pet5o.esper.epl.metric.MetricReportingServiceSPI;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowMgmtService;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableService;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableService;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.vaevent.ValueAddEventService;
import eu.uk.ncl.pet5o.esper.filterspec.FilterBooleanExpressionFactory;
import eu.uk.ncl.pet5o.esper.pattern.PatternNodeFactory;
import eu.uk.ncl.pet5o.esper.rowregex.RegexHandlerFactory;
import eu.uk.ncl.pet5o.esper.schedule.SchedulingService;
import eu.uk.ncl.pet5o.esper.timer.TimeSourceService;
import eu.uk.ncl.pet5o.esper.view.ViewService;
import eu.uk.ncl.pet5o.esper.view.ViewServicePreviousFactory;

import java.net.URI;

public final class StatementContextEngineServices {
    private final String engineURI;
    private final EventAdapterService eventAdapterService;
    private final NamedWindowMgmtService namedWindowMgmtService;
    private final VariableService variableService;
    private final TableService tableService;
    private final EngineSettingsService engineSettingsService;
    private final ValueAddEventService valueAddEventService;
    private final ConfigurationInformation configSnapshot;
    private final MetricReportingServiceSPI metricReportingService;
    private final ViewService viewService;
    private final ExceptionHandlingService exceptionHandlingService;
    private final ExpressionResultCacheService expressionResultCacheService;
    private final StatementEventTypeRef statementEventTypeRef;
    private final TableExprEvaluatorContext tableExprEvaluatorContext;
    private final EngineLevelExtensionServicesContext engineLevelExtensionServicesContext;
    private final RegexHandlerFactory regexHandlerFactory;
    private final StatementLockFactory statementLockFactory;
    private final ContextManagementService contextManagementService;
    private final ViewServicePreviousFactory viewServicePreviousFactory;
    private final EventTableIndexService eventTableIndexService;
    private final PatternNodeFactory patternNodeFactory;
    private final FilterBooleanExpressionFactory filterBooleanExpressionFactory;
    private final TimeSourceService timeSourceService;
    private final EngineImportService engineImportService;
    private final AggregationFactoryFactory aggregationFactoryFactory;
    private final SchedulingService schedulingService;
    private final ExprDeclaredService exprDeclaredService;

    public StatementContextEngineServices(String engineURI, EventAdapterService eventAdapterService, NamedWindowMgmtService namedWindowMgmtService, VariableService variableService, TableService tableService, EngineSettingsService engineSettingsService, ValueAddEventService valueAddEventService, ConfigurationInformation configSnapshot, MetricReportingServiceSPI metricReportingService, ViewService viewService, ExceptionHandlingService exceptionHandlingService, ExpressionResultCacheService expressionResultCacheService, StatementEventTypeRef statementEventTypeRef, TableExprEvaluatorContext tableExprEvaluatorContext, EngineLevelExtensionServicesContext engineLevelExtensionServicesContext, RegexHandlerFactory regexHandlerFactory, StatementLockFactory statementLockFactory, ContextManagementService contextManagementService, ViewServicePreviousFactory viewServicePreviousFactory, EventTableIndexService eventTableIndexService, PatternNodeFactory patternNodeFactory, FilterBooleanExpressionFactory filterBooleanExpressionFactory, TimeSourceService timeSourceService, EngineImportService engineImportService, AggregationFactoryFactory aggregationFactoryFactory, SchedulingService schedulingService, ExprDeclaredService exprDeclaredService) {
        this.engineURI = engineURI;
        this.eventAdapterService = eventAdapterService;
        this.namedWindowMgmtService = namedWindowMgmtService;
        this.variableService = variableService;
        this.tableService = tableService;
        this.engineSettingsService = engineSettingsService;
        this.valueAddEventService = valueAddEventService;
        this.configSnapshot = configSnapshot;
        this.metricReportingService = metricReportingService;
        this.viewService = viewService;
        this.exceptionHandlingService = exceptionHandlingService;
        this.expressionResultCacheService = expressionResultCacheService;
        this.statementEventTypeRef = statementEventTypeRef;
        this.tableExprEvaluatorContext = tableExprEvaluatorContext;
        this.engineLevelExtensionServicesContext = engineLevelExtensionServicesContext;
        this.regexHandlerFactory = regexHandlerFactory;
        this.statementLockFactory = statementLockFactory;
        this.contextManagementService = contextManagementService;
        this.viewServicePreviousFactory = viewServicePreviousFactory;
        this.eventTableIndexService = eventTableIndexService;
        this.patternNodeFactory = patternNodeFactory;
        this.filterBooleanExpressionFactory = filterBooleanExpressionFactory;
        this.timeSourceService = timeSourceService;
        this.engineImportService = engineImportService;
        this.aggregationFactoryFactory = aggregationFactoryFactory;
        this.schedulingService = schedulingService;
        this.exprDeclaredService = exprDeclaredService;
    }

    public String getEngineURI() {
        return engineURI;
    }

    public EventAdapterService getEventAdapterService() {
        return eventAdapterService;
    }

    public NamedWindowMgmtService getNamedWindowMgmtService() {
        return namedWindowMgmtService;
    }

    public VariableService getVariableService() {
        return variableService;
    }

    public URI[] getPlugInTypeResolutionURIs() {
        return engineSettingsService.getPlugInEventTypeResolutionURIs();
    }

    public ValueAddEventService getValueAddEventService() {
        return valueAddEventService;
    }

    public ConfigurationInformation getConfigSnapshot() {
        return configSnapshot;
    }

    public MetricReportingServiceSPI getMetricReportingService() {
        return metricReportingService;
    }

    public ViewService getViewService() {
        return viewService;
    }

    public ExceptionHandlingService getExceptionHandlingService() {
        return exceptionHandlingService;
    }

    public ExpressionResultCacheService getExpressionResultCacheService() {
        return expressionResultCacheService;
    }

    public StatementEventTypeRef getStatementEventTypeRef() {
        return statementEventTypeRef;
    }

    public TableService getTableService() {
        return tableService;
    }

    public TableExprEvaluatorContext getTableExprEvaluatorContext() {
        return tableExprEvaluatorContext;
    }

    public EngineLevelExtensionServicesContext getEngineLevelExtensionServicesContext() {
        return engineLevelExtensionServicesContext;
    }

    public RegexHandlerFactory getRegexHandlerFactory() {
        return regexHandlerFactory;
    }

    public StatementLockFactory getStatementLockFactory() {
        return statementLockFactory;
    }

    public ContextManagementService getContextManagementService() {
        return contextManagementService;
    }

    public ViewServicePreviousFactory getViewServicePreviousFactory() {
        return viewServicePreviousFactory;
    }

    public EventTableIndexService getEventTableIndexService() {
        return eventTableIndexService;
    }

    public PatternNodeFactory getPatternNodeFactory() {
        return patternNodeFactory;
    }

    public FilterBooleanExpressionFactory getFilterBooleanExpressionFactory() {
        return filterBooleanExpressionFactory;
    }

    public EngineSettingsService getEngineSettingsService() {
        return engineSettingsService;
    }

    public TimeSourceService getTimeSourceService() {
        return timeSourceService;
    }

    public EngineImportService getEngineImportService() {
        return engineImportService;
    }

    public AggregationFactoryFactory getAggregationFactoryFactory() {
        return aggregationFactoryFactory;
    }

    public SchedulingService getSchedulingService() {
        return schedulingService;
    }

    public ExprDeclaredService getExprDeclaredService() {
        return exprDeclaredService;
    }
}
