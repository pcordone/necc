package org.necc;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.List;

public class NodeProcessor {
    /**
     * Our enum of the nodes to consider for processing.
     */
    enum ConsideredNames {
        SUBVIEWS("subviews"),
        CONTENT_VIEW("contentView"),
        INPUT("input"),
        CONTROL("control");

        private final String value;

        ConsideredNames(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return getValue();
        }

        /**
         * Tests if a string is one of the considered enum values.
         * // TODO: DO we want to use apache commons lang 3 lib EnumUtils.isValidEnum?  Let's keep it simple for now.
         * @param value The string value to test.
         * @return true if the string matches an enum value, false otherwise
         */
        public static boolean isConsideredValue(String value) {
            for (ConsideredNames c : ConsideredNames.values()) {
                if (c.name().equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        }

        public static ConsideredNames fromString(String text) {
            for (ConsideredNames b : ConsideredNames.values()) {
                if (b.value.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    enum ConsideredAttributes {
        CLASS("class"),
        CLASS_NAMES("classNames"),
        IDENTIFIER("identifier");

        private final String value;

        ConsideredAttributes(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public boolean isEqualToString(String strValue) {
            return this.value.equalsIgnoreCase(strValue);
        }

        public static ConsideredAttributes consideredAttributeFromString(String value) {
            for (ConsideredAttributes c : ConsideredAttributes.values()) {
                if (c.name().equalsIgnoreCase(value)) {
                    return c;
                }
            }
            return null;
        }

        public static ConsideredAttributes consideredAttributeFromChar(char value) {
            switch(value) {
                case '.':
                    return ConsideredAttributes.CLASS_NAMES;
                case '#':
                    return ConsideredAttributes.IDENTIFIER;
                default:
                    return ConsideredAttributes.CLASS;
            }
        }
    }

    /**
     * This method processes the fields.  It will recurse for any fields that are part of ConsideredNames.  We will keep
     * track of any fields that match our selector so that when we get to the bottom of the recursion (i.e. we are at
     * the leaf JSON nodes with no sub nodes) we know whether we should keep it.
     * @param processedNode - the node under processing.
     * @param consideredAttribute - the attribute we are considering for matching.
     * @param consideredAttributeValue - the attribute value we are considering for matching.
     */
    public static void processNodeFields(String processedNodeName, JsonNode processedNode, ConsideredAttributes consideredAttribute, String consideredAttributeValue, Integer nodeNumber, List output) {
        // for object nodes, we need to check if this node matches our attribute criteria and emit JSON if so.
        assert (processedNode.isObject());
        String attributeName = consideredAttribute.getValue();
        JsonNode actualAttributeNode = processedNode.get(attributeName);
        System.out.println("Considering node name '" + processedNodeName + "'" +
                " matching attribute '" + consideredAttribute.getValue() + "'" +
                " matching value '"  + consideredAttributeValue + "'" +
                " actual value '" + (actualAttributeNode == null ? "null" : actualAttributeNode.asText()) + "'" +
                " node number '" + (nodeNumber == null ? "" : nodeNumber) + "'"
        );
        if (actualAttributeNode != null && consideredAttributeValue.equalsIgnoreCase(actualAttributeNode.asText())) {
            System.out.println("Found match.");
            output.add(new SelectorOutput(processedNodeName, nodeNumber, actualAttributeNode.asText()));
        }
        // make sure we recurse on any nested nodes for fields that we should be considering
        for (ConsideredNames c : ConsideredNames.values()) {
            JsonNode nestedNode = processedNode.get(c.getValue());
            if (nestedNode != null) {
                // if the field value is an array, we need to make sure we recurse for all elements
                if (nestedNode.isArray()) {
                    int i = 1;
                    Iterator<JsonNode> nodes = nestedNode.elements();
                    while (nodes.hasNext()) {
                        JsonNode node = nodes.next();
                        processNodeFields(c.getValue(), node, consideredAttribute, consideredAttributeValue, i++, output);
                    }
                }
                processNodeFields(c.getValue(), nestedNode, consideredAttribute, consideredAttributeValue, null, output);
            }
        }
    }
}
