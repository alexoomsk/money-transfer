package test.money.dto;

public final class Response<T> {

    private final Status status;
    private final String errorMessage;
    private final T result;


    private Response(Status status, String errorMessage, T result) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.result = result;
    }

    public String getStatus() {
        return status.name();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public T getResult() {
        return result;
    }

    public static <T> Response<T> done(T result) {
        return new Response<>(Status.DONE, null, result);
    }

    public static Response<?> error(String message) {
        return new Response<>(Status.ERROR, message, null);
    }

    public enum Status {
        ERROR, DONE
    }
}
