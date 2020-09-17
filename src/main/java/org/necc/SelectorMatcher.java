package org.necc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.jvm.hotspot.code.ConstantDoubleValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 */
public class SelectorMatcher {

    private JsonNode node;

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

    public void static processNodes() {

    }

    /**
     * This method processes the fiels of a node by checking if it is a node with one of the names we are considering.
     * It will call processArrayNodes if it encounters a field that is an array of nodes.
     * @param nodeBeingProcessed - the node under processing.
     * @param consideredAttribute - the attribute we are considering for matching.
     * @param consideredAttributeValue - the attribute value we are considering for matching.
     */
    public static void processNodeFields(JsonNode nodeBeingProcessed, ConsideredAttributes consideredAttribute, String consideredAttributeValue) {
        Iterator<Map.Entry<String, JsonNode>> nodes = nodeBeingProcessed.fields();
        while (nodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = nodes.next();
            String name = entry.getKey();
            if (ConsideredNames.isConsideredValue(name)) {
                JsonNode node = entry.getValue();
                if (node.isArray()) {
                    processArrayNode(node, consideredAttribute, consideredAttributeValue);
                } else {
                    // we are at the leaf nodes so we can check if the attribute matches
                    JsonNode attributeValue = node.get(consideredAttribute.getValue().toString());
                    if (consideredAttributeValue.equalsIgnoreCase(attributeValue.asText())) {
                        // we have found a node with an attribute value we are looking for.
                        System.out.println("found " + node);
                    }
                }
            }
        }
    }

    /**
     * This method processes an array of nodes by calling processNode for each node in the array.
     * @param arrayNode
     * @param consideredAttribute - The attribute we are selecting for
     * @param consideredAttributeValue - The attribute value we are selecting for
     */
    public static void processArrayNode(JsonNode arrayNode, ConsideredAttributes consideredAttribute, String consideredAttributeValue) {
        Iterator<JsonNode> nodes = arrayNode.elements();
        while(nodes.hasNext()) {
            JsonNode node = nodes.next();
            processNodeFields(node, consideredAttribute, consideredAttributeValue);
        }
    }

    /**
     * Our main program.
     * TODO: We could do this as a spring boot console app, but let's keep it simple for now.
     * @param args Our filename to process is the only argument.
     */
    public static void main(String args[]) {
        if (args.length != 1) {
            System.err.println("Error!  SelectorMatcher must be passed a filename in JSON format to parse!");
            System.exit(-1);
        }
        String filename = args[0];

        Scanner console = new Scanner(System.in);
        System.out.println("Enter a view attribute value to display and press enter.  Choose the attribute type by specifying a prefix.  Just enter will exit.");
        System.out.println("  no prefix - selects classname attribute e.g. class=\"StackView\"");
        System.out.println("  . - selects classNames attribute value e.g. classNames contains e.g. \".container\"");
        System.out.println("  # - selects identifier attribute value e.g. identifier=\"videoMode\"");
        boolean keepRunning = true;
        while(keepRunning) {
            System.out.print("Enter a selector and press enter: ");
            String selector = console.nextLine();
            if (selector.isEmpty()) {
                break;
            }
            char selectorFirstChar = selector.charAt(0);
            ConsideredAttributes consideredAttribute = ConsideredAttributes.consideredAttributeFromChar(selectorFirstChar);
            if (consideredAttribute != ConsideredAttributes.CLASS) {
                selector = selector.substring(1, selector.length());
            }
            try {
                byte[] jsonData = Files.readAllBytes(Paths.get(filename));
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonData);
                // let's make no assumptions about the root node and process all fields
                System.out.println("Searching for attribute " + consideredAttribute.getValue() + "  with value " + selector);
                processNodeFields(rootNode, consideredAttribute, selector);
            } catch (IOException e) {
                System.err.println("Error! Error reading JSON file " + filename);
            }
        }
    }
}
