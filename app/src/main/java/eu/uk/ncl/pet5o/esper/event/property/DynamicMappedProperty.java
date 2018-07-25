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
package eu.uk.ncl.pet5o.esper.event.property;

import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayMappedPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.bean.BeanEventType;
import eu.uk.ncl.pet5o.esper.event.bean.DynamicMappedPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.map.MapEventPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.map.MapMappedPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.xml.BaseXMLEventType;
import eu.uk.ncl.pet5o.esper.event.xml.DOMMapGetter;
import eu.uk.ncl.pet5o.esper.event.xml.SchemaElementComplex;
import eu.uk.ncl.pet5o.esper.event.xml.SchemaItem;

import java.io.StringWriter;
import java.util.Map;

/**
 * Represents a dynamic mapped property of a given name.
 * <p>
 * Dynamic properties always exist, have an Object type and are resolved to a method during runtime.
 */
public class DynamicMappedProperty extends PropertyBase implements DynamicProperty, PropertyWithKey {
    private final String key;

    /**
     * Ctor.
     *
     * @param propertyName is the property name
     * @param key          is the mapped access key
     */
    public DynamicMappedProperty(String propertyName, String key) {
        super(propertyName);
        this.key = key;
    }

    public boolean isDynamic() {
        return true;
    }

    public String[] toPropertyArray() {
        return new String[]{this.getPropertyNameAtomic()};
    }

    public EventPropertyGetterSPI getGetter(BeanEventType eventType, EventAdapterService eventAdapterService) {
        return new DynamicMappedPropertyGetter(propertyNameAtomic, key, eventAdapterService);
    }

    public Class getPropertyType(BeanEventType eventType, EventAdapterService eventAdapterService) {
        return Object.class;
    }

    public GenericPropertyDesc getPropertyTypeGeneric(BeanEventType beanEventType, EventAdapterService eventAdapterService) {
        return GenericPropertyDesc.getObjectGeneric();
    }

    public Class getPropertyTypeMap(Map optionalMapPropTypes, EventAdapterService eventAdapterService) {
        return Object.class;
    }

    public MapEventPropertyGetter getGetterMap(Map optionalMapPropTypes, EventAdapterService eventAdapterService) {
        return new MapMappedPropertyGetter(this.getPropertyNameAtomic(), key);
    }

    public void toPropertyEPL(StringWriter writer) {
        writer.append(propertyNameAtomic);
        writer.append("('");
        writer.append(key);
        writer.append("')");
        writer.append('?');
    }

    public EventPropertyGetterSPI getGetterDOM(SchemaElementComplex complexProperty, EventAdapterService eventAdapterService, BaseXMLEventType eventType, String propertyExpression) {
        return new DOMMapGetter(propertyNameAtomic, key, null);
    }

    public SchemaItem getPropertyTypeSchema(SchemaElementComplex complexProperty, EventAdapterService eventAdapterService) {
        return null;  // always returns Node
    }

    public EventPropertyGetterSPI getGetterDOM() {
        return new DOMMapGetter(propertyNameAtomic, key, null);
    }

    public ObjectArrayEventPropertyGetter getGetterObjectArray(Map<String, Integer> indexPerProperty, Map<String, Object> nestableTypes, EventAdapterService eventAdapterService) {
        Integer propertyIndex = indexPerProperty.get(propertyNameAtomic);
        return propertyIndex == null ? null : new ObjectArrayMappedPropertyGetter(propertyIndex, key);
    }

    public String getKey() {
        return key;
    }
}
