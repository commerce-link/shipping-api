package pl.commercelink.shipping.api;

public record Parcel(
        int width,
        int depth,
        int height,
        int weight,
        int insuranceValue,
        String description,
        String type
) {

    public boolean isComplete() {
        return width > 0 && depth > 0 && height > 0 && weight > 0 && insuranceValue > 0
                && description != null && !description.isEmpty()
                && type != null && !type.isEmpty();
    }
}
