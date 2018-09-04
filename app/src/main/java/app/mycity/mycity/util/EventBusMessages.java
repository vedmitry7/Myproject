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
        private String photo780;
        private String name;

        public String getId() {
            return id;
        }

        public String getPhoto780() {
            return photo780;
        }

        public String getName() {
            return name;
        }

        public OpenPlace(String id, String photo780, String name) {
         this.id = id;
         this.photo780 = photo780;
         this.name = name;
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
}
