package app.mycity.mycity.util;

import app.mycity.mycity.api.model.Album;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;

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

    public static class OpenPhotoReportContent {
        private final String albumName;
        private long albumDate;
        private String placeId;
        private String albumId;
        private int position;

        public OpenPhotoReportContent (String placeId, String albumId, String albumName, Long albumDate, int position) {
            this.placeId = placeId;
            this.albumId = albumId;
            this.albumName = albumName;
            this.albumDate = albumDate;
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
        public String getAlbumId() {
            return albumId;
        }
        public String getAlbumName() {
            return albumName;
        }
        public long getAlbumDate() {
            return albumDate;
        }
        public String getPlaceId() {
            return placeId;
        }
    }

    public static class OpenPhotoReport{
        private Album album;

        public Album getAlbum() {
            return album;
        }

        public OpenPhotoReport(Album album) {
            this.album = album;
        }
    }

    public static class OpenPlacePhoto {
        private String placeId;
        public OpenPlacePhoto(String placeId) {
            this.placeId = placeId;
        }

        public String getPlaceId() {
            return placeId;
        }
    }

    public static class OpenPlacePhoto2 {
        private final String placeId;
        private Post post;
        private Group group;
        private Profile profile;

        public Post getPost() {
            return post;
        }

        public Profile getProfile() {
            return profile;
        }

        public Group getGroup() {

            return group;
        }

        public OpenPlacePhoto2(String placeId, Post post, Group group, Profile profile) {
            this.placeId = placeId;
            this.post = post;
            this.group = group;
            this.profile = profile;
        }

        public String getPlaceId() {
            return placeId;
        }
    }


    public static class OpenPlaceSubscribers{
        private final String groupId;

        public OpenPlaceSubscribers(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupId() {
            return groupId;
        }
    }


    public static class OpenUsersInPlace{
        private final String groupId;

        public OpenUsersInPlace(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupId() {
            return groupId;
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

    //newer used should delete
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
        String albumId;
        int adapterPosition;

        public LoadAlbum(String albumId, int adapterPosition) {
            this.albumId = albumId;
            this.adapterPosition = adapterPosition;
        }

        public String getAlbumId() {
            return albumId;
        }

        public void setAlbumId(String albumId) {
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

    public static class ShowImage {
        int position;

        public int getPosition() {
            return position;
        }

        public ShowImage(int position) {
            this.position = position;
        }
    }

    public static class PhotoReportPhotoClick {
        int position;

        public int getPosition() {
            return position;
        }

        public PhotoReportPhotoClick(int position) {
            this.position = position;
        }
    }

    public static class OpenUserPlace {
        private final String userId;

        public OpenUserPlace(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }
    }

    public static class UpdateSocketConnection {
    }

    public static class SortPlaces {
        public SortPlaces(int adapterPosition) {
        }
    }
}
