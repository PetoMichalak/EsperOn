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
package eu.uk.ncl.pet5o.esper.epl.parse;

import eu.uk.ncl.pet5o.esper.client.ConfigurationPlugInAggregationMultiFunction;
import eu.uk.ncl.pet5o.esper.client.hook.AggregationFunctionFactory;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportException;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportUndefinedException;
import eu.uk.ncl.pet5o.esper.epl.expression.accessagg.ExprPlugInAggMultiFunctionNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprPlugInAggNode;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionDeclarationContext;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionFactory;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.util.LazyAllocatedMap;

import java.util.Locale;

public class ASTAggregationHelper {
    public static ExprNode tryResolveAsAggregation(EngineImportService engineImportService,
                                                   boolean distinct,
                                                   String functionName,
                                                   LazyAllocatedMap<ConfigurationPlugInAggregationMultiFunction, PlugInAggregationMultiFunctionFactory> plugInAggregations,
                                                   String engineURI) {
        try {
            AggregationFunctionFactory aggregationFactory = engineImportService.resolveAggregationFactory(functionName);
            return new ExprPlugInAggNode(distinct, aggregationFactory, functionName);
        } catch (EngineImportUndefinedException e) {
            // Not an aggregation function
        } catch (EngineImportException e) {
            throw new IllegalStateException("Error resolving aggregation: " + e.getMessage(), e);
        }

        // try plug-in aggregation multi-function
        ConfigurationPlugInAggregationMultiFunction config = engineImportService.resolveAggregationMultiFunction(functionName);
        if (config != null) {
            PlugInAggregationMultiFunctionFactory factory = plugInAggregations.getMap().get(config);
            if (factory == null) {
                factory = (PlugInAggregationMultiFunctionFactory) JavaClassHelper.instantiate(PlugInAggregationMultiFunctionFactory.class, config.getMultiFunctionFactoryClassName(), engineImportService.getClassForNameProvider());
                plugInAggregations.getMap().put(config, factory);
            }
            factory.addAggregationFunction(new PlugInAggregationMultiFunctionDeclarationContext(functionName.toLowerCase(Locale.ENGLISH), distinct, engineURI, config));
            return new ExprPlugInAggMultiFunctionNode(distinct, config, factory, functionName);
        }

        // try built-in expanded set of aggregation functions
        return engineImportService.resolveAggExtendedBuiltin(functionName, distinct);
    }
}
