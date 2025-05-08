package spring.project.urlShortener.models.dtos;

public class ResponseDto<T> {
    private T response;
    private String message;

    // Private constructor
    private ResponseDto(Builder<T> builder) {
        this.response = builder.response;
        this.message = builder.message;
    }

    // Getters
    public T getResponse() {
        return response;
    }

    public String getMessage() {
        return message;
    }

    // Static inner Builder class
    public static class Builder<T> {
        private T response;
        private String message;

        public Builder<T> response(T response) {
            this.response = response;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ResponseDto<T> build() {
            return new ResponseDto<>(this);
        }
    }

    // Static builder method
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
}
