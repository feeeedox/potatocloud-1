package net.potatocloud.api.property;

public class Property<T> {

    /**
     * The name of the property.
     */
    private final String name;

    /**
     * The default value of the property.
     */
    private final T defaultValue;

    /**
     * The value of the property.
     */
    private T value;

    public Property(String name, T defaultValue, T value) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public T defaultValue() {
        return defaultValue;
    }

    /**
     * Gets the current value of the property. Returns the default value if the current value is {@code null}
     *
     * @return the property value
     */
    public T value() {
        return value != null ? value : defaultValue;
    }

    public void value(T value) {
        this.value = value;
    }

    /**
     * Sets the value of the property using an object.
     *
     * @param value the value to set
     */
    @SuppressWarnings("unchecked")
    public void valueObject(Object value) {
        this.value = (T) value;
    }

    public static Property<String> ofString(String name, String defaultValue) {
        return new Property<>(name, defaultValue, defaultValue);
    }

    public static Property<Integer> ofInteger(String name, int defaultValue) {
        return new Property<>(name, defaultValue, defaultValue);
    }

    public static Property<Boolean> ofBoolean(String name, boolean defaultValue) {
        return new Property<>(name, defaultValue, defaultValue);
    }

    public static Property<Float> ofFloat(String name, float defaultValue) {
        return new Property<>(name, defaultValue, defaultValue);
    }

    public static Property<Double> ofDouble(String name, double defaultValue) {
        return new Property<>(name, defaultValue, defaultValue);
    }

    public static <T> Property<T> of(String name, T defaultValue, T value) {
        return new Property<>(name, defaultValue, value);
    }
}
