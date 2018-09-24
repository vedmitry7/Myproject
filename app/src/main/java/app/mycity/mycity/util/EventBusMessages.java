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

    public static class OpenSubscribers{
        private final String userId;

        public OpenSubscribers(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }
    }

    public static class OpenSubscriptions{
        private final String userId;

        public OpenSubscriptions(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }
    }

    public static class LikePost {
        private final String itemId;
        private final String ownerId;
        private final int adapterPosition;

        public String getItemId() {
            return itemId;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public int getAdapterPosition() {
            return adapterPosition;
        }

        public LikePost(String itemId, String ownerId, int adapterPosition) {
            this.itemId = itemId;
            this.ownerId = ownerId;
            this.adapterPosition = adapterPosition;
        }
    }

    public static class AddVisitor {
        private final String itemId;
        private final String ownerId;
        private final int adapterPosition;

        public String getItemId() {
            return itemId;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public int getAdapterPosition() {
            return adapterPosition;
        }

        public AddVisitor(String itemId, String ownerId, int adapterPosition) {
            this.itemId = itemId;
            this.ownerId = ownerId;
            this.adapterPosition = adapterPosition;
        }
    }


    public static class LikeComment {
        private final String itemId;
        private final String ownerId;
        private final int adapterPosition;

        public String getItemId() {
            return itemId;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public int getAdapterPosition() {
            return adapterPosition;
        }

        public LikeComment(String itemId, String ownerId, int adapterPosition) {
            this.itemId = itemId;
            this.ownerId = ownerId;
            this.adapterPosition = adapterPosition;
        }
    }

    public static class OpenComments {
        private final String postId;
        private final String ownerId;

        public OpenComments(String postId, String ownerId) {
            this.postId = postId;
            this.ownerId = ownerId;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public String getPostId() {
            return postId;
        }
    }

    public static class Message {
        private final String history;

        public Message(String history) {
            this.history = history;
        }

        public String getHistory() {
            return history;
        }
    }

    public static class UpdateChat{
    }

    public static class UpdateDialog {
        private String message;
        private long id;

        public UpdateDialog(long id, String message) {
            this.message = message;
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public long getId() {
            return id;
        }
    }

    public static class OpenPlace {
        private String id;

        public String getId() {
            return id;
        }

        public OpenPlace(String id) {
         this.id = id;
        }
    }

    public static class SwichTab {
        private int tab;

        public int getPos() {
            return tab;
        }

        public SwichTab(int tab) {
            this.tab = tab;
        }
    }


    public static class LoadAlbum{
        long albumId;
        int adapterPosition;

        public LoadAlbum(long albumId, int adapterPosition) {
            this.albumId = albumId;
            this.adapterPosition = adapterPosition;
        }

        public long getAlbumId() {
            return albumId;
        }

        public void setAlbumId(long albumId) {
            this.albumId = albumId;
        }

        public int getAdapterPosition() {
            return adapterPosition;
        }

        public void setAdapterPosition(int adapterPosition) {
            this.adapterPosition = adapterPosition;
        }
    }

    public static class MakeCheckin {
    }
}
