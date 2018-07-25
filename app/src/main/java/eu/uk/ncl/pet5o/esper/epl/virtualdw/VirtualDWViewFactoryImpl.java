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
package eu.uk.ncl.pet5o.esper.epl.virtualdw;

import eu.uk.ncl.pet5o.esper.client.EventBeanFactory;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.client.hook.VirtualDataWindow;
import eu.uk.ncl.pet5o.esper.client.hook.VirtualDataWindowContext;
import eu.uk.ncl.pet5o.esper.client.hook.VirtualDataWindowFactory;
import eu.uk.ncl.pet5o.esper.client.hook.VirtualDataWindowFactoryContext;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import eu.uk.ncl.pet5o.esper.core.service.ExprEvaluatorContextStatement;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.event.EventAdapterServiceHelper;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.view.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class VirtualDWViewFactoryImpl implements ViewFactory, DataWindowViewFactory, VirtualDWViewFactory {
    private final static Logger log = LoggerFactory.getLogger(VirtualDWViewFactoryImpl.class);

    private Serializable customConfiguration;
    private ViewFactoryContext viewFactoryContext;
    private List<ExprNode> viewParameters;
    private String namedWindowName;
    private VirtualDataWindowFactory virtualDataWindowFactory;
    private EventType parentEventType;
    private Object[] viewParameterArr;
    private ExprNode[] viewParameterExp;
    private EventBeanFactory eventBeanFactory;

    public VirtualDWViewFactoryImpl(Class first, String namedWindowName, Serializable customConfiguration) throws ViewProcessingException {
        if (!JavaClassHelper.isImplementsInterface(first, VirtualDataWindowFactory.class)) {
            throw new ViewProcessingException("Virtual data window factory class " + first.getName() + " does not implement the interface " + VirtualDataWindowFactory.class.getName());
        }
        this.customConfiguration = customConfiguration;
        this.namedWindowName = namedWindowName;
        virtualDataWindowFactory = (VirtualDataWindowFactory) JavaClassHelper.instantiate(VirtualDataWindowFactory.class, first);
    }

    public Set<String> getUniqueKeys() {
        return virtualDataWindowFactory.getUniqueKeyPropertyNames();
    }

    public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> viewParameters) throws ViewParameterException {
        this.viewFactoryContext = viewFactoryContext;
        this.viewParameters = viewParameters;
    }

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException {
        this.parentEventType = parentEventType;

        ExprNode[] validatedNodes = ViewFactorySupport.validate(viewFactoryContext.getViewName(), parentEventType, viewFactoryContext.getStatementContext(), viewParameters, true);
        viewParameterArr = new Object[validatedNodes.length];
        ExprEvaluatorContextStatement evaluatorContextStmt = new ExprEvaluatorContextStatement(viewFactoryContext.getStatementContext(), false);
        for (int i = 0; i < validatedNodes.length; i++) {
            try {
                viewParameterArr[i] = ViewFactorySupport.evaluateAssertNoProperties(viewFactoryContext.getViewName(), validatedNodes[i], i, evaluatorContextStmt);
            } catch (Exception ex) {
                // expected
            }
        }

        viewParameterExp = ViewFactorySupport.validate(viewFactoryContext.getViewName(), parentEventType, viewFactoryContext.getStatementContext(), viewParameters, true);

        // initialize
        try {
            eventBeanFactory = EventAdapterServiceHelper.getFactoryForType(parentEventType, statementContext.getEventAdapterService());
            virtualDataWindowFactory.initialize(new VirtualDataWindowFactoryContext(parentEventType, viewParameterArr, viewParameterExp, eventBeanFactory, namedWindowName, viewFactoryContext, customConfiguration));
        } catch (RuntimeException ex) {
            throw new ViewParameterException("Validation exception initializing virtual data window '" + namedWindowName + "': " + ex.getMessage(), ex);
        }
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        VirtualDataWindowOutStreamImpl outputStream = new VirtualDataWindowOutStreamImpl();
        VirtualDataWindowContext context = new VirtualDataWindowContext(agentInstanceViewFactoryContext.getAgentInstanceContext(), parentEventType, viewParameterArr, viewParameterExp, eventBeanFactory, outputStream, namedWindowName, viewFactoryContext, customConfiguration);
        VirtualDataWindow window;
        try {
            window = virtualDataWindowFactory.create(context);
        } catch (Exception ex) {
            throw new ViewProcessingException("Exception returned by virtual data window factory upon creation: " + ex.getMessage(), ex);
        }
        VirtualDWViewImpl view = new VirtualDWViewImpl(window, parentEventType, namedWindowName);
        outputStream.setView(view);
        return view;
    }

    public EventType getEventType() {
        return parentEventType;
    }

    public boolean canReuse(View view, AgentInstanceContext agentInstanceContext) {
        return false;
    }

    public void destroyNamedWindow() {
        if (virtualDataWindowFactory != null) {
            virtualDataWindowFactory.destroyAllContextPartitions();
        }
    }

    public String getViewName() {
        return "Virtual Data Window";
    }
}
