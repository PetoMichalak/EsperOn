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
package eu.uk.ncl.pet5o.esper.core.context.activator;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.core.service.ExprEvaluatorContextStatement;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.StatementSpecCompiled;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowProcessor;
import eu.uk.ncl.pet5o.esper.epl.spec.FilterStreamSpecCompiled;
import eu.uk.ncl.pet5o.esper.epl.spec.NamedWindowConsumerStreamSpec;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.filterspec.FilterSpecCompiled;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationAgent;
import eu.uk.ncl.pet5o.esper.pattern.EvalRootFactoryNode;
import eu.uk.ncl.pet5o.esper.pattern.PatternContext;
import eu.uk.ncl.pet5o.esper.view.HistoricalEventViewable;

import java.lang.annotation.Annotation;

public interface ViewableActivatorFactory {
    ViewableActivator createActivatorSimple(FilterStreamSpecCompiled filterStreamSpec);

    ViewableActivator createFilterProxy(EPServicesContext services,
                                        FilterSpecCompiled filterSpec,
                                        Annotation[] annotations,
                                        boolean subselect,
                                        InstrumentationAgent instrumentationAgentSubquery,
                                        boolean isCanIterate,
                                        Integer streamNumFromClause);

    ViewableActivator createStreamReuseView(EPServicesContext services,
                                            StatementContext statementContext,
                                            StatementSpecCompiled statementSpec,
                                            FilterStreamSpecCompiled filterStreamSpec,
                                            boolean isJoin,
                                            ExprEvaluatorContextStatement evaluatorContextStmt,
                                            boolean filterSubselectSameStream,
                                            int streamNum,
                                            boolean isCanIterateUnbound);

    ViewableActivator createPattern(PatternContext patternContext,
                                    EvalRootFactoryNode rootFactoryNode,
                                    EventType eventType,
                                    boolean consumingFilters,
                                    boolean suppressSameEventMatches,
                                    boolean discardPartialsOnMatch,
                                    boolean isCanIterateUnbound);

    ViewableActivator createNamedWindow(NamedWindowProcessor processor,
                                        NamedWindowConsumerStreamSpec streamSpec,
                                        StatementContext statementContext);

    ViewableActivator createTable(TableMetadata metadata, ExprEvaluator[] optionalTableFilters);

    ViewableActivator makeHistorical(HistoricalEventViewable historicalEventViewable);

    ViewableActivator makeSubqueryNWIndexShare();
}
