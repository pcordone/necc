package org.necc;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NodeProcessorTest {
    static String JSON_FILE =
            "                           {\n" +
                    "                    \"subviews\": [\n" +
                    "                      {\n" +
                    "                        \"class\": \"Input\",\n" +
                    "                        \"label\": {\n" +
                    "                          \"text\": {\n" +
                    "                            \"text\": \"Video mode\"\n" +
                    "                          }\n" +
                    "                        },\n" +
                    "                        \"control\": {\n" +
                    "                          \"class\": \"VideoModeSelect\",\n" +
                    "                          \"identifier\": \"videoMode\"\n" +
                    "                        }\n" +
                    "                      },\n" +
                    "                      {\n" +
                    "                        \"class\": \"Input\",\n" +
                    "                        \"label\": {\n" +
                    "                          \"text\": {\n" +
                    "                            \"text\": \"High DPI (4K)\"\n" +
                    "                          }\n" +
                    "                        },\n" +
                    "                        \"control\": {\n" +
                    "                          \"class\": \"CvarCheckbox\",\n" +
                    "                          \"var\": \"r_allow_high_dpi\"\n" +
                    "                        }\n" +
                    "                      },\n" +
                    "                      {\n" +
                    "                        \"class\": \"Input\",\n" +
                    "                        \"label\": {\n" +
                    "                          \"text\": {\n" +
                    "                            \"text\": \"Window mode\"\n" +
                    "                          }\n" +
                    "                        },\n" +
                    "                        \"control\": {\n" +
                    "                          \"class\": \"CvarSelect\",\n" +
                    "                          \"identifier\": \"windowMode\",\n" +
                    "                          \"var\": \"r_fullscreen\"\n" +
                    "                        }\n" +
                    "                      },\n" +
                    "                      {\n" +
                    "                        \"class\": \"Input\",\n" +
                    "                        \"label\": {\n" +
                    "                          \"text\": {\n" +
                    "                            \"text\": \"Vertical sync\"\n" +
                    "                          }\n" +
                    "                        },\n" +
                    "                        \"control\": {\n" +
                    "                          \"class\": \"CvarSelect\",\n" +
                    "                          \"identifier\": \"verticalSync\",\n" +
                    "                          \"var\": \"r_swap_interval\"\n" +
                    "                        }\n" +
                    "                      },\n" +
                    "                      {\n" +
                    "                        \"class\": \"Input\",\n" +
                    "                        \"label\": {\n" +
                    "                          \"text\": {\n" +
                    "                            \"text\": \"Frame limiter\"\n" +
                    "                          }\n" +
                    "                        },\n" +
                    "                        \"control\": {\n" +
                    "                          \"class\": \"CvarSlider\",\n" +
                    "                          \"min\": 0,\n" +
                    "                          \"max\": 250,\n" +
                    "                          \"step\": 5,\n" +
                    "                          \"var\": \"cl_max_fps\"\n" +
                    "                        }\n" +
                    "                      }\n" +
                    "                    ]\n" +
                    "                  }\n";
    @Test
    void testCompoundSelectorMatchClassOnly() {
        String JSON = "  {\n" +
                "            \"class\": \"Input\",\n" +
                "            \"identifier\": \"videoMode\",\n" +
                "            \"classNames\": [\n" +
                "                    \"column\",\n" +
                "                    \"container\"\n" +
                "            ]\n" +
                "        }";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(JSON);
            String selectorStr = "Input";
            NodeProcessor.CompoundSelector compoundSelector = new NodeProcessor.CompoundSelector(selectorStr);
            assertTrue(compoundSelector.selectorMatches(rootNode));
        } catch(IOException e) {
            fail("test threw exception " + e);
        }
    }

    @Test
    void testCompoundSelectorMatch() {
        String JSON = "  {\n" +
                "            \"class\": \"Input\",\n" +
                "            \"identifier\": \"videoMode\",\n" +
                "            \"classNames\": [\n" +
                "                    \"column\",\n" +
                "                    \"container\"\n" +
                "            ]\n" +
                "        }";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(JSON);
            String selectorStr = "Input#videoMode.column";
            NodeProcessor.CompoundSelector compoundSelector = new NodeProcessor.CompoundSelector(selectorStr);
            assertTrue(compoundSelector.selectorMatches(rootNode));
        } catch(IOException e) {
            fail("test threw exception " + e);
        }
    }

    @Test
    void testCompoundSelectorNoMatchOnArrayAttribute() {
        String JSON = "  {\n" +
                "            \"class\": \"Input\",\n" +
                "            \"identifier\": \"videoMode\",\n" +
                "            \"classNames\": [\n" +
                "                    \"column\",\n" +
                "                    \"container\"\n" +
                "            ]\n" +
                "        }";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(JSON);
            String selectorStr = "Input#videoMode.nomatch";
            NodeProcessor.CompoundSelector compoundSelector = new NodeProcessor.CompoundSelector(selectorStr);
            assertFalse(compoundSelector.selectorMatches(rootNode));
        } catch(IOException e) {
            fail("test threw exception " + e);
        }
    }

    @Test
    void testCompoundSelectorNoMatchOnClass() {
        String JSON = "  {\n" +
                "            \"class\": \"Input\",\n" +
                "            \"identifier\": \"videoMode\",\n" +
                "            \"classNames\": [\n" +
                "                    \"column\",\n" +
                "                    \"container\"\n" +
                "            ]\n" +
                "        }";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(JSON);
            String selectorStr = "Nomatch#videoMode.column";
            NodeProcessor.CompoundSelector compoundSelector = new NodeProcessor.CompoundSelector(selectorStr);
            assertFalse(compoundSelector.selectorMatches(rootNode));
        } catch(IOException e) {
            fail("test threw exception " + e);
        }
    }

    @Test
    void testProcessSingleObjectNode() {
        String JSON =
                " {\n" +
                        "  \"class\": \"Input\",\n" +
                        "  \"label\": {\n" +
                        "    \"text\": {\n" +
                        "      \"text\": \"Video mode\"\n" +
                        "        }\n" +
                        "      },\n" +
                        "  \"control\": {\n" +
                        "    \"class\": \"VideoModeSelect\",\n" +
                        "    \"identifier\": \"videoMode\"\n" +
                        "    }\n" +
                        "}";
        // let's make no assumptions about the root node and process all fields
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(JSON);
            List<SelectorOutput> output = new ArrayList<>();
            NodeProcessor processor = new NodeProcessor();
            processor.setSelector("Input");
            processor.processNodeFields("subview", rootNode, null, output);
            String expected = "[ {\n" +
                    "  \"name\" : \"subview\",\n" +
                    "  \"nodeNumber\" : null\n" +
                    "} ]";
            ObjectMapper outputMapper = new ObjectMapper();
            ByteArrayOutputStream outputBaos = new ByteArrayOutputStream();
            outputMapper.writerWithDefaultPrettyPrinter().writeValue(outputBaos, output);
            assertEquals(expected, outputBaos.toString());
        } catch(IOException e) {
            fail("test threw exception " + e);
        }
    }

    @Test
    void testProcessNestedArrayNodesAsField() {
        String expected = "[ {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 1\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 2\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 3\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 4\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 5\n" +
                "} ]";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(JSON_FILE);
            List<SelectorOutput> output = new ArrayList<>();
            NodeProcessor processor = new NodeProcessor();
            processor.setSelector("Input");
            processor.processNodeFields(null, rootNode, null, output);
            ObjectMapper outputMapper = new ObjectMapper();
            ByteArrayOutputStream outputBaos = new ByteArrayOutputStream();
            outputMapper.writerWithDefaultPrettyPrinter().writeValue(outputBaos, output);
            assertEquals(expected, outputBaos.toString());
        } catch(IOException e) {
            fail("test threw exception " + e);
        }
    }

    @Test
    void testProcessInputFile() {
        String JSON = "{\n" +
                "  \"identifier\": \"System\",\n" +
                "  \"subviews\": [\n" +
                "    {\n" +
                "      \"class\": \"StackView\",\n" +
                "      \"classNames\": [\n" +
                "        \"container\"\n" +
                "      ],\n" +
                "      \"subviews\": [\n" +
                "        {\n" +
                "          \"class\": \"StackView\",\n" +
                "          \"classNames\": [\n" +
                "            \"columns\",\n" +
                "            \"container\"\n" +
                "          ],\n" +
                "          \"subviews\": [\n" +
                "            {\n" +
                "              \"class\": \"StackView\",\n" +
                "              \"classNames\": [\n" +
                "                \"column\",\n" +
                "                \"container\"\n" +
                "              ],\n" +
                "              \"subviews\": [\n" +
                "                {\n" +
                "                  \"class\": \"Box\",\n" +
                "                  \"label\": {\n" +
                "                    \"text\": {\n" +
                "                      \"text\": \"Display\"\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"contentView\": {\n" +
                "                    \"subviews\": [\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Video mode\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"VideoModeSelect\",\n" +
                "                          \"identifier\": \"videoMode\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"High DPI (4K)\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarCheckbox\",\n" +
                "                          \"var\": \"r_allow_high_dpi\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Window mode\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSelect\",\n" +
                "                          \"identifier\": \"windowMode\",\n" +
                "                          \"var\": \"r_fullscreen\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Vertical sync\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSelect\",\n" +
                "                          \"identifier\": \"verticalSync\",\n" +
                "                          \"var\": \"r_swap_interval\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Frame limiter\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0,\n" +
                "                          \"max\": 250,\n" +
                "                          \"step\": 5,\n" +
                "                          \"var\": \"cl_max_fps\"\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                },\n" +
                "                {\n" +
                "                  \"class\": \"Box\",\n" +
                "                  \"label\": {\n" +
                "                    \"text\": {\n" +
                "                      \"text\": \"Rendering\"\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"contentView\": {\n" +
                "                    \"subviews\": [\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Texture mode\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSelect\",\n" +
                "                          \"identifier\": \"textureMode\",\n" +
                "                          \"expectsStringValue\": true,\n" +
                "                          \"var\": \"r_texture_mode\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Anisotropy\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSelect\",\n" +
                "                          \"identifier\": \"anisotropy\",\n" +
                "                          \"var\": \"r_anisotropy\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Multisample\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSelect\",\n" +
                "                          \"identifier\": \"multisample\",\n" +
                "                          \"var\": \"r_multisample\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Supersample\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSelect\",\n" +
                "                          \"identifier\": \"supersample\",\n" +
                "                          \"var\": \"r_supersample\"\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"class\": \"StackView\",\n" +
                "              \"classNames\": [\n" +
                "                \"column\",\n" +
                "                \"container\"\n" +
                "              ],\n" +
                "              \"subviews\": [\n" +
                "                {\n" +
                "                  \"class\": \"Box\",\n" +
                "                  \"label\": {\n" +
                "                    \"text\": {\n" +
                "                      \"text\": \"Picture\"\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"contentView\": {\n" +
                "                    \"subviews\": [\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Brightness\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0.1,\n" +
                "                          \"max\": 2,\n" +
                "                          \"var\": \"r_brightness\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Contrast\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0.1,\n" +
                "                          \"max\": 2,\n" +
                "                          \"var\": \"r_contrast\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Gamma\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0.1,\n" +
                "                          \"max\": 2,\n" +
                "                          \"var\": \"r_gamma\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Modulate\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 1,\n" +
                "                          \"max\": 5,\n" +
                "                          \"var\": \"r_modulate\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Bumpmapping\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0.1,\n" +
                "                          \"max\": 2,\n" +
                "                          \"var\": \"r_bumpmap\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Hardness\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0.1,\n" +
                "                          \"max\": 2,\n" +
                "                          \"var\": \"r_hardness\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Specular\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0.1,\n" +
                "                          \"max\": 2,\n" +
                "                          \"var\": \"r_specular\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Parallax\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0.1,\n" +
                "                          \"max\": 2,\n" +
                "                          \"var\": \"r_parallax\"\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"class\": \"StackView\",\n" +
                "              \"classNames\": [\n" +
                "                \"column\",\n" +
                "                \"container\"\n" +
                "              ],\n" +
                "              \"subviews\": [\n" +
                "                {\n" +
                "                  \"class\": \"Box\",\n" +
                "                  \"label\": {\n" +
                "                    \"text\": {\n" +
                "                      \"text\": \"Sound\"\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"contentView\": {\n" +
                "                    \"subviews\": [\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Master\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0,\n" +
                "                          \"max\": 1,\n" +
                "                          \"var\": \"s_volume\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Effects\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0,\n" +
                "                          \"max\": 1,\n" +
                "                          \"var\": \"s_effects_volume\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Ambient\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0,\n" +
                "                          \"max\": 1,\n" +
                "                          \"var\": \"s_ambient_volume\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Music\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSlider\",\n" +
                "                          \"min\": 0,\n" +
                "                          \"max\": 1,\n" +
                "                          \"var\": \"s_music_volume\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Reverse stereo\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarCheckbox\",\n" +
                "                          \"var\": \"s_reverse\"\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                },\n" +
                "                {\n" +
                "                  \"class\": \"Box\",\n" +
                "                  \"label\": {\n" +
                "                    \"text\": {\n" +
                "                      \"text\": \"Network\"\n" +
                "                    }\n" +
                "                  },\n" +
                "                  \"contentView\": {\n" +
                "                    \"subviews\": [\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Connection speed\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarSelect\",\n" +
                "                          \"identifier\": \"rate\",\n" +
                "                          \"var\": \"rate\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Download maps\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarCheckbox\",\n" +
                "                          \"var\": \"cl_download_maps\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Download models\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarCheckbox\",\n" +
                "                          \"var\": \"cl_download_models\"\n" +
                "                        }\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"class\": \"Input\",\n" +
                "                        \"label\": {\n" +
                "                          \"text\": {\n" +
                "                            \"text\": \"Network graph\"\n" +
                "                          }\n" +
                "                        },\n" +
                "                        \"control\": {\n" +
                "                          \"class\": \"CvarCheckbox\",\n" +
                "                          \"var\": \"cl_draw_net_graph\"\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"class\": \"StackView\",\n" +
                "          \"classNames\": [\n" +
                "            \"accessoryView\",\n" +
                "            \"container\"\n" +
                "          ],\n" +
                "          \"subviews\": [\n" +
                "            {\n" +
                "              \"class\": \"Button\",\n" +
                "              \"identifier\": \"apply\",\n" +
                "              \"title\": {\n" +
                "                \"text\": \"Apply\"\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        String expected = "[ {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 1\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 2\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 3\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 4\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 5\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 1\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 2\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 3\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 4\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 1\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 2\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 3\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 4\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 5\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 6\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 7\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 8\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 1\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 2\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 3\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 4\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 5\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 1\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 2\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 3\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 4\n" +
                "} ]";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(JSON);
            List<SelectorOutput> output = new ArrayList<>();
            NodeProcessor processor = new NodeProcessor();
            processor.setSelector("Input");
            processor.processNodeFields(null, rootNode, null, output);
            ObjectMapper outputMapper = new ObjectMapper();
            ByteArrayOutputStream outputBaos = new ByteArrayOutputStream();
            outputMapper.writerWithDefaultPrettyPrinter().writeValue(outputBaos, output);
            assertEquals(expected, outputBaos.toString());
        } catch(IOException e) {
            fail("test threw exception " + e);
        }
    }

    @Test
    void testProcessNestedArrayNodesAsFieldCompoundSelector() {
        String expected = "[ {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 1\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 2\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 3\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 4\n" +
                "}, {\n" +
                "  \"name\" : \"subviews\",\n" +
                "  \"nodeNumber\" : 5\n" +
                "} ]";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(JSON_FILE);
            List<SelectorOutput> output = new ArrayList<>();
            NodeProcessor processor = new NodeProcessor();
            processor.setSelector("Input");
            processor.processNodeFields(null, rootNode, null, output);
            ObjectMapper outputMapper = new ObjectMapper();
            ByteArrayOutputStream outputBaos = new ByteArrayOutputStream();
            outputMapper.writerWithDefaultPrettyPrinter().writeValue(outputBaos, output);
            assertEquals(expected, outputBaos.toString());        } catch(IOException e) {
            fail("test threw exception " + e);
        }
    }

    @Test
    void testProcessInputFileClassInputVideoMode() {
        String expected = "[ ]";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(JSON_FILE);
            List<SelectorOutput> output = new ArrayList<>();
            NodeProcessor processor = new NodeProcessor();
            processor.setSelector("Input#videoMode");
            processor.processNodeFields(null, rootNode, null, output);
            ObjectMapper outputMapper = new ObjectMapper();
            ByteArrayOutputStream outputBaos = new ByteArrayOutputStream();
            outputMapper.writerWithDefaultPrettyPrinter().writeValue(outputBaos, output);
            assertEquals(expected, outputBaos.toString());        } catch(IOException e) {
            fail("test threw exception " + e);
        }
    }

    @Test
    void testProcessInputFileClassInputIdentifierVideoMode() {
        String expected = "[ ]";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(JSON_FILE);
            List<SelectorOutput> output = new ArrayList<>();
            NodeProcessor processor = new NodeProcessor();
            processor.setSelector("Input#videoMode");
            processor.processNodeFields(null, rootNode, null, output);
            ObjectMapper outputMapper = new ObjectMapper();
            ByteArrayOutputStream outputBaos = new ByteArrayOutputStream();
            outputMapper.writerWithDefaultPrettyPrinter().writeValue(outputBaos, output);
            assertEquals(expected, outputBaos.toString());        } catch(IOException e) {
            fail("test threw exception " + e);
        }
    }

    @Test
    void testProcessInputFileClassVideoModeSelectIdentifierVideoMode() {
        String expected = "[ {\n" +
                "  \"name\" : \"control\",\n" +
                "  \"nodeNumber\" : null\n" +
                "} ]";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(JSON_FILE);
            List<SelectorOutput> output = new ArrayList<>();
            NodeProcessor processor = new NodeProcessor();
            processor.setSelector("VideoModeSelect#videoMode");

            processor.processNodeFields(null, rootNode, null, output);
            ObjectMapper outputMapper = new ObjectMapper();
            ByteArrayOutputStream outputBaos = new ByteArrayOutputStream();
            outputMapper.writerWithDefaultPrettyPrinter().writeValue(outputBaos, output);
            assertEquals(expected, outputBaos.toString());        } catch(IOException e) {
            fail("test threw exception " + e);
        }
    }
}
