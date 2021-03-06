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
package eu.uk.ncl.pet5o.esper.dataflow.ops;

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.client.dataflow.EPDataFlowEventCollector;
import eu.uk.ncl.pet5o.esper.client.dataflow.EPDataFlowEventCollectorContext;
import eu.uk.ncl.pet5o.esper.client.dataflow.EventBusCollector;
import eu.uk.ncl.pet5o.esper.core.service.EPRuntimeEventSender;
import eu.uk.ncl.pet5o.esper.dataflow.annotations.DataFlowOpParameter;
import eu.uk.ncl.pet5o.esper.dataflow.annotations.DataFlowOperator;
import eu.uk.ncl.pet5o.esper.dataflow.interfaces.*;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.EventBeanAdapterFactory;
import org.w3c.dom.Node;

import java.util.Map;

@DataFlowOperator
public class EventBusSink implements DataFlowOpLifecycle {

    private EventAdapterService eventAdapterService;
    private EPRuntimeEventSender runtimeEventSender;

    @DataFlowOpParameter
    private EPDataFlowEventCollector collector;

    private EventBusCollector eventBusCollector;
    private EventBeanAdapterFactory[] adapterFactories;
    private ThreadLocal<EPDataFlowEventCollectorContext> collectorDataTL = new ThreadLocal<EPDataFlowEventCollectorContext>() {
        protected synchronized EPDataFlowEventCollectorContext initialValue() {
            return null;
        }
    };

    public DataFlowOpInitializeResult initialize(DataFlowOpInitializateContext context) throws Exception {
        if (!context.getOutputPorts().isEmpty()) {
            throw new IllegalArgumentException("EventBusSink operator does not provide an output stream");
        }

        EventType[] eventTypes = new EventType[context.getInputPorts().size()];
        for (int i = 0; i < eventTypes.length; i++) {
            eventTypes[i] = context.getInputPorts().get(i).getTypeDesc().getEventType();
        }
        runtimeEventSender = context.getRuntimeEventSender();
        eventAdapterService = context.getStatementContext().getEventAdapterService();

        if (collector != null) {
            eventBusCollector = new EventBusCollector() {
                public void sendEvent(Object object) throws EPException {
                    EventBean event = eventAdapterService.adapterForBean(object);
                    runtimeEventSender.processWrappedEvent(event);
                }

                public void sendEvent(Map map, String eventTypeName) throws EPException {
                    EventBean event = eventAdapterService.adapterForMap(map, eventTypeName);
                    runtimeEventSender.processWrappedEvent(event);
                }

                public void sendEvent(Object[] objectArray, String eventTypeName) throws EPException {
                    EventBean event = eventAdapterService.adapterForObjectArray(objectArray, eventTypeName);
                    runtimeEventSender.processWrappedEvent(event);
                }

                public void sendEvent(Node node) throws EPException {
                    EventBean event = eventAdapterService.adapterForDOM(node);
                    runtimeEventSender.processWrappedEvent(event);
                }
            };
        } else {
            adapterFactories = new EventBeanAdapterFactory[eventTypes.length];
            for (int i = 0; i < eventTypes.length; i++) {
                adapterFactories[i] = context.getServicesContext().getEventAdapterService().getAdapterFactoryForType(eventTypes[i]);
            }
        }
        return null;
    }

    public void onInput(int port, Object data) {
        if (eventBusCollector != null) {
            EPDataFlowEventCollectorContext holder = collectorDataTL.get();
            if (holder == null) {
                holder = new EPDataFlowEventCollectorContext(eventBusCollector, data);
                collectorDataTL.set(holder);
            } else {
                holder.setEvent(data);
            }
            collector.collect(holder);
        } else {
            if (data instanceof EventBean) {
                runtimeEventSender.processWrappedEvent((EventBean) data);
            } else {
                EventBean event = adapterFactories[port].makeAdapter(data);
                runtimeEventSender.processWrappedEvent(event);
            }
        }
    }

    public void open(DataFlowOpOpenContext openContext) {
        // no action
    }

    public void close(DataFlowOpCloseContext openContext) {
        // no action
    }
}
