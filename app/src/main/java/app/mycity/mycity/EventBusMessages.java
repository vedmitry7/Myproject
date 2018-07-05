package app.mycity.mycity;

public class EventBusMessages {

    public static class OpenUser {
        private final String message;

        public OpenUser(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

}
