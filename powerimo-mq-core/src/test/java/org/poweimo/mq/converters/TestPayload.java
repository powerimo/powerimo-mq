package org.poweimo.mq.converters;

// Helper class for testing
public class TestPayload {
    private String name;
    private int value;

    public TestPayload() {
    }

    public TestPayload(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestPayload)) return false;
        TestPayload that = (TestPayload) o;
        return value == that.value && name.equals(that.name);
    }
}
