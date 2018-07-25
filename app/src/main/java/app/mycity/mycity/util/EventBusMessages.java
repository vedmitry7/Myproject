package app.mycity.mycity.util;

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
