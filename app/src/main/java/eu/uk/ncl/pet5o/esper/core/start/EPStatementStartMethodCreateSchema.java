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

import eu.uk.ncl.pet5o.esper.client.ConfigurationVariantStream;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.factory.StatementAgentInstanceFactoryNoAgentInstance;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.StatementSpecCompiled;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.spec.CreateSchemaDesc;
import eu.uk.ncl.pet5o.esper.epl.util.EPLValidationUtil;
import eu.uk.ncl.pet5o.esper.event.EventTypeUtility;
import eu.uk.ncl.pet5o.esper.view.ViewProcessingException;
import eu.uk.ncl.pet5o.esper.view.Viewable;
import eu.uk.ncl.pet5o.esper.view.ViewableDefaultImpl;

/**
 * Starts and provides the stop method for EPL statements.
 */
public class EPStatementStartMethodCreateSchema extends EPStatementStartMethodBase {
    public EPStatementStartMethodCreateSchema(StatementSpecCompiled statementSpec) {
        super(statementSpec);
    }

    public EPStatementStartResult startInternal(final EPServicesContext services, final StatementContext statementContext, boolean isNewStatement, boolean isRecoveringStatement, boolean isRecoveringResilient) throws ExprValidationException, ViewProcessingException {
        final CreateSchemaDesc spec = statementSpec.getCreateSchemaDesc();

        EPLValidationUtil.validateTableExists(services.getTableService(), spec.getSchemaName());
        EventType eventType = handleCreateSchema(services, statementContext, spec);

        // enter a reference
        services.getStatementEventTypeRefService().addReferences(statementContext.getStatementName(), new String[]{spec.getSchemaName()});

        final EventType allocatedEventType = eventType;
        EPStatementStopMethod stopMethod = new EPStatementStopMethod() {
            public void stop() {
                services.getStatementEventTypeRefService().removeReferencesStatement(statementContext.getStatementName());
                if (services.getStatementEventTypeRefService().getStatementNamesForType(spec.getSchemaName()).isEmpty()) {
                    services.getEventAdapterService().removeType(allocatedEventType.getName());
                    services.getFilterService().removeType(allocatedEventType);
                }
            }
        };
        Viewable viewable = new ViewableDefaultImpl(eventType);

        // assign agent instance factory (an empty op)
        statementContext.setStatementAgentInstanceFactory(new StatementAgentInstanceFactoryNoAgentInstance(viewable));

        return new EPStatementStartResult(viewable, stopMethod, null);
    }

    private EventType handleCreateSchema(EPServicesContext services, StatementContext statementContext, CreateSchemaDesc spec)
            throws ExprValidationException {

        EventType eventType;

        try {
            if (spec.getAssignedType() != CreateSchemaDesc.AssignedType.VARIANT) {
                eventType = EventTypeUtility.createNonVariantType(false, spec, statementContext.getAnnotations(), services.getConfigSnapshot(), services.getEventAdapterService(), services.getEngineImportService());
            } else {
                if (spec.getCopyFrom() != null && !spec.getCopyFrom().isEmpty()) {
                    throw new ExprValidationException("Copy-from types are not allowed with variant types");
                }

                boolean isAny = false;
                ConfigurationVariantStream config = new ConfigurationVariantStream();
                for (String typeName : spec.getTypes()) {
                    if (typeName.trim().equals("*")) {
                        isAny = true;
                        break;
                    }
                    config.addEventTypeName(typeName);
                }
                if (!isAny) {
                    config.setTypeVariance(ConfigurationVariantStream.TypeVariance.PREDEFINED);
                } else {
                    config.setTypeVariance(ConfigurationVariantStream.TypeVariance.ANY);
                }
                services.getValueAddEventService().addVariantStream(spec.getSchemaName(), config, services.getEventAdapterService(), services.getEventTypeIdGenerator());
                eventType = services.getValueAddEventService().getValueAddProcessor(spec.getSchemaName()).getValueAddEventType();
            }
        } catch (RuntimeException ex) {
            throw new ExprValidationException(ex.getMessage(), ex);
        }

        return eventType;
    }
}
