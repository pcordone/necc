package org.necc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
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
        NodeProcessor nodeProcessor = new NodeProcessor();
        ObjectMapper mapper = new ObjectMapper();
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get(filename));
            JsonNode rootNode = mapper.readTree(jsonData);
            while(keepRunning) {
                System.out.print("Enter a selector and press enter: ");
                String selector = console.nextLine();
                if (selector.isEmpty()) {
                    break;
                }
                nodeProcessor.setSelector(selector);
                List<SelectorOutput> output = new ArrayList<>();
                nodeProcessor.processNodeFields(null, rootNode,null, output);
                ObjectMapper outputMapper = new ObjectMapper();
                ByteArrayOutputStream outputBaos = new ByteArrayOutputStream();
                outputMapper.writerWithDefaultPrettyPrinter().writeValue(outputBaos, output);
                System.out.println(outputBaos.toString());
            }

        } catch (IOException e) {
            System.err.println("Error! Error reading JSON file " + filename);
        }
    }
}
