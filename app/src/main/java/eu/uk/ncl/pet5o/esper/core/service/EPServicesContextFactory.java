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
import eu.uk.ncl.pet5o.esper.client.EPServiceProvider;

/**
 * Interface for a factory class to provide services in a services context for an engine instance.
 */
public interface EPServicesContextFactory {
    /**
     * Factory method for a new set of engine services.
     *
     * @param epServiceProvider     is the engine instance
     * @param configurationSnapshot is a snapshot of configs at the time of engine creation
     * @return services context
     */
    public EPServicesContext createServicesContext(EPServiceProvider epServiceProvider, ConfigurationInformation configurationSnapshot);
}
