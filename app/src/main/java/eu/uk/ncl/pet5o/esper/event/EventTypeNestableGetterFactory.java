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
package eu.uk.ncl.pet5o.esper.event;

import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.event.bean.BeanEventPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.bean.BeanEventType;
import eu.uk.ncl.pet5o.esper.event.map.MapEventPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.property.IndexedProperty;
import eu.uk.ncl.pet5o.esper.event.property.MappedProperty;
import eu.uk.ncl.pet5o.esper.event.property.Property;

import java.util.Map;

public interface EventTypeNestableGetterFactory {

    public EventPropertyGetterSPI getPropertyProvidedGetter(Map<String, Object> nestableTypes, String propertyName, Property prop, EventAdapterService eventAdapterService);

    public EventPropertyGetterMappedSPI getPropertyProvidedGetterMap(Map<String, Object> nestableTypes, String mappedPropertyName, MappedProperty mappedProperty, EventAdapterService eventAdapterService);

    public EventPropertyGetterIndexedSPI getPropertyProvidedGetterIndexed(Map<String, Object> nestableTypes, String indexedPropertyName, IndexedProperty indexedProperty, EventAdapterService eventAdapterService);

    public EventPropertyGetterSPI getGetterProperty(String name, BeanEventType nativeFragmentType, EventAdapterService eventAdapterService);

    public EventPropertyGetterSPI getGetterEventBean(String name, Class underlyingType);

    public EventPropertyGetterSPI getGetterEventBeanArray(String name, EventType eventType);

    public EventPropertyGetterSPI getGetterBeanNested(String name, EventType eventType, EventAdapterService eventAdapterService);

    public EventPropertyGetterSPI getGetterBeanNestedArray(String name, EventType eventType, EventAdapterService eventAdapterService);

    public EventPropertyGetterSPI getGetterIndexedEventBean(String propertyNameAtomic, int index);

    public EventPropertyGetterSPI getGetterIndexedUnderlyingArray(String propertyNameAtomic, int index, EventAdapterService eventAdapterService, EventType innerType);

    public EventPropertyGetterSPI getGetterIndexedPOJO(String propertyNameAtomic, int index, EventAdapterService eventAdapterService, Class componentType);

    public EventPropertyGetterSPI getGetterMappedProperty(String propertyNameAtomic, String key);

    public EventPropertyGetterSPI getGetterNestedEntryBeanArray(String propertyNameAtomic, int index, EventPropertyGetter getter, EventType innerType, EventAdapterService eventAdapterService);

    public EventPropertyGetterSPI getGetterIndexedEntryEventBeanArrayElement(String propertyNameAtomic, int index, EventPropertyGetterSPI nestedGetter);

    public EventPropertyGetterSPI getGetterIndexedEntryPOJO(String propertyNameAtomic, int index, BeanEventPropertyGetter nestedGetter, EventAdapterService eventAdapterService, Class propertyTypeGetter);

    public EventPropertyGetterSPI getGetterNestedMapProp(String propertyName, MapEventPropertyGetter getterNestedMap);

    public EventPropertyGetterSPI getGetterNestedPOJOProp(String propertyName, BeanEventPropertyGetter nestedGetter, EventAdapterService eventAdapterService, Class nestedReturnType, Class nestedComponentType);

    public EventPropertyGetterSPI getGetterNestedEventBean(String propertyName, EventPropertyGetterSPI nestedGetter);

    public EventPropertyGetterSPI getGetterNestedPropertyProvidedGetterDynamic(Map<String, Object> nestableTypes, String propertyName, EventPropertyGetter nestedGetter, EventAdapterService eventAdapterService);

    public EventPropertyGetterSPI getGetterNestedEntryBean(String propertyName, EventPropertyGetter innerGetter, EventType innerType, EventAdapterService eventAdapterService);

}
