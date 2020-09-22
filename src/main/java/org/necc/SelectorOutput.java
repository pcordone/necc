package org.necc;

public class SelectorOutput {
    private String name;
    private Integer nodeNumber;
    private String matchAttributeValue;

    public SelectorOutput(String name, Integer nodeNumber, String matchAttributeValue) {
        this.name = name;
        this.nodeNumber = nodeNumber;
        this.matchAttributeValue = matchAttributeValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(Integer nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public String getMatchAttributeValue() {
        return matchAttributeValue;
    }

    public void setMatchAttributeValue(String matchAttributeValue) {
        this.matchAttributeValue = matchAttributeValue;
    }
}
