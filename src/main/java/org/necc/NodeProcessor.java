package org.necc;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public static class CompoundSelector {
        private HashMap<ConsideredAttributes, String> selectors = new HashMap<>();

        /**
         * Let's kill the private constructor since this class is immutable.
         */
        private CompoundSelector() {
        }

        /**
         * Parses the string and builds our map of compound selectors and values.
         * @param selectorStr The selector string the user entered that will be parsed.
         */
        public CompoundSelector(String selectorStr) {
            String fields[] = selectorStr.split("(?=[.#])");
            for (String field: fields) {
                char selectorFirstChar = field.charAt(0);
                ConsideredAttributes consideredAttribute = ConsideredAttributes.consideredAttributeFromChar(selectorFirstChar);
                String selectorValue = field;
                if (consideredAttribute != ConsideredAttributes.CLASS) {
                    selectorValue = field.substring(1);
                }
                selectors.put(consideredAttribute, selectorValue);
            }
        }

        public boolean selectorMatches(JsonNode node) {
            for (Map.Entry<ConsideredAttributes, String> entry : selectors.entrySet()) {
                ConsideredAttributes attributeName = entry.getKey();
                String attributeValue = entry.getValue();
                JsonNode actualAttributeNode = node.get(attributeName.getValue());
                if (actualAttributeNode == null) {
                    return false;
                }
                else if (actualAttributeNode.isArray()) {
                    boolean oneMatched = false;
                    for (Iterator<JsonNode> it = actualAttributeNode.iterator(); it.hasNext(); ) {
                        JsonNode arrayValue = it.next();
                        if (attributeValue.equals(arrayValue.asText())) {
                            oneMatched = true;
                            break;
                        }
                    }
                    if (!oneMatched) {
                        return false;
                    }
                } else if (!attributeValue.equalsIgnoreCase(actualAttributeNode.asText())) {
                    return false;
                }
            }
            return true;
        }
    }

    private CompoundSelector compoundSelector;

    public NodeProcessor() {
    }

    public void setSelector(String selector) {
        compoundSelector = new CompoundSelector(selector);
    }

    /**
     * This method processes the fields.  It will recurse for any fields that are part of ConsideredNames.  We will keep
     * track of any fields that match our selector so that when we get to the bottom of the recursion (i.e. we are at
     * the leaf JSON nodes with no sub nodes) we know whether we should keep it.
     * @param processedNode - the node under processing.
     */
    public void processNodeFields(String processedNodeName, JsonNode processedNode, Integer nodeNumber, List output) {
        // for object nodes, we need to check if this node matches our attribute criteria and emit JSON if so.
        assert (processedNode.isObject());
        if (compoundSelector.selectorMatches(processedNode) ) {
            output.add(new SelectorOutput(processedNodeName, nodeNumber));
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
                        processNodeFields(c.getValue(), node, i++, output);
                    }
                }
                processNodeFields(c.getValue(), nestedNode, null, output);
            }
        }
    }
}
