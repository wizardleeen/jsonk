package org.jsonk.processor;

import javax.lang.model.element.Name;
import javax.lang.model.util.Elements;
import java.util.Set;

class CommonNames {

    final Name toJson;
    final Name fromJson;
    final Name classJsonk;
    final Name classObject;
    final Name classClass;
    final Name classAdapter;
    final Name classAdapterFactory;
    final Name classDate;
    final Name classInstant;
    final Name classBoolean;
    final Name classByte;
    final Name classShort;
    final Name classInteger;
    final Name classLong;
    final Name classFloat;
    final Name classDouble;
    final Name classCharacter;
    final Name classString;
    final Name classMap;
    final Name classHashMap;
    final Name classLinkedHashMap;
    final Name classTreeMap;
    final Name classNavigableMap;
    final Name classSortedMap;
    final Name classSequencedMap;
    final Name classList;
    final Name classArrayList;
    final Name classLinkedList;
    final Name classQueue;
    final Name classDeque;
    final Name classSet;
    final Name classHashSet;
    final Name classTreeSet;
    final Name classNavigableSet;
    final Name classSortedSet;
    final Name classSequencedSet;
    final Name classCollection;
    final Name classLocalDateTime;
    final Name classLocalDate;
    final Name classLocalTime;
    final Name classOffsetDateTime;
    final Name classOffsetTime;
    final Name classZonedDateTime;
    final Name classJson;
    final Name classJsonProperty;
    final Name classJsonIgnore;
    final Name classTemporal;
    final Name typeProperty;
    final Name value;
    final Name dateTimeFormat;
    final Name includeNull;
    final Name adapter;
    final Name adapterFactory;
    final Name type;
    final Name subTypes;
    final Set<Name> builtinClassNames;

    public CommonNames(Elements elements) {
        classJsonk = elements.getName("org.jsonk.Jsonk");
        classObject = elements.getName("java.lang.Object");
        classClass = elements.getName("java.lang.Class");
        classAdapter = elements.getName("org.jsonk.Adapter");
        classAdapterFactory = elements.getName("org.jsonk.AdapterFactory");
        classDate = elements.getName("java.util.Date");
        classInstant = elements.getName("java.time.Instant");
        classBoolean = elements.getName("java.lang.Boolean");
        classByte = elements.getName("java.lang.Byte");
        classShort = elements.getName("java.lang.Short");
        classInteger = elements.getName("java.lang.Integer");
        classLong = elements.getName("java.lang.Long");
        classFloat = elements.getName("java.lang.Float");
        classDouble = elements.getName("java.lang.Double");
        classCharacter = elements.getName("java.lang.Character");
        classString = elements.getName("java.lang.String");
        classMap = elements.getName("java.util.Map");
        classHashMap = elements.getName("java.util.HashMap");
        classLinkedHashMap = elements.getName("java.util.LinkedHashMap");
        classTreeMap = elements.getName("java.util.TreeMap");
        classNavigableMap = elements.getName("java.util.NavigableMap");
        classSortedMap = elements.getName("java.util.SortedMap");
        classSequencedMap = elements.getName("java.util.SequencedMap");
        classList = elements.getName("java.util.List");
        classArrayList = elements.getName("java.util.ArrayList");
        classLinkedList = elements.getName("java.util.LinkedList");
        classQueue = elements.getName("java.util.Queue");
        classDeque = elements.getName("java.util.Deque");
        classSet = elements.getName("java.util.Set");
        classHashSet = elements.getName("java.util.HashSet");
        classTreeSet = elements.getName("java.util.TreeSet");
        classNavigableSet = elements.getName("java.util.NavigableSet");
        classSortedSet = elements.getName("java.util.SortedSet");
        classSequencedSet = elements.getName("java.util.SequencedSet");
        classCollection = elements.getName("java.util.Collection");
        classLocalDateTime = elements.getName("java.time.LocalDateTime");
        classLocalDate = elements.getName("java.time.LocalDate");
        classLocalTime = elements.getName("java.time.LocalTime");
        classOffsetDateTime = elements.getName("java.time.OffsetDateTime");
        classOffsetTime = elements.getName("java.time.OffsetTime");
        classZonedDateTime = elements.getName("java.time.ZonedDateTime");
        classJson = elements.getName("org.jsonk.Json");
        classJsonProperty = elements.getName("org.jsonk.JsonProperty");
        classJsonIgnore = elements.getName("org.jsonk.JsonIgnore");
        classTemporal = elements.getName("java.time.temporal.Temporal");

        toJson = elements.getName("toJson");
        fromJson = elements.getName("fromJson");
        typeProperty = elements.getName("typeProperty");
        value = elements.getName("value");
        includeNull = elements.getName("includeNull");
        dateTimeFormat = elements.getName("dateTimeFormat");
        adapter = elements.getName("adapter");
        adapterFactory = elements.getName("adapterFactory");
        type = elements.getName("type");
        subTypes = elements.getName("subTypes");

        builtinClassNames = Set.of(
                classBoolean,
                classByte,
                classShort,
                classInteger,
                classLong,
                classFloat,
                classDouble,
                classCharacter,
                classString,
                classMap,
                classHashMap,
                classLinkedHashMap,
                classTreeMap,
                classNavigableMap,
                classSortedMap,
                classSequencedMap,
                classList,
                classArrayList,
                classLinkedList,
                classQueue,
                classDeque,
                classSet,
                classHashSet,
                classTreeSet,
                classNavigableSet,
                classSortedSet,
                classSequencedSet,
                classCollection,
                classDate,
                classLocalDateTime,
                classLocalDate,
                classLocalTime,
                classOffsetDateTime,
                classOffsetTime,
                classZonedDateTime,
                classInstant,
                classObject
        );

    }
}