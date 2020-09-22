package org.necc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.jvm.hotspot.code.ConstantDoubleValue;

import static org.necc.NodeProcessor.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 */
public class SelectorMatcher {
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
                StringBuffer output = new StringBuffer();
                output.append("[\n");
                processNodeFields(null, rootNode, consideredAttribute, selector, null, output);
                output.append("]");
                System.out.println(output.toString());
            } catch (IOException e) {
                System.err.println("Error! Error reading JSON file " + filename);
            }
        }
    }
}
