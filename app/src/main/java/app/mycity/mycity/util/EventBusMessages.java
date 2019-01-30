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
        private String postId;
        public OpenPlacePhoto(String placeId, String postId) {
            this.placeId = placeId;
            this.postId = postId;
        }

        public String getPostId() {
            return postId;
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
        private final String type;

        public OpenComments(String postId, String ownerId, String type) {
            this.postId = postId;
            this.ownerId = ownerId;
            this.type = type;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public String getPostId() {
            return postId;
        }

        public String getType() {
            return type;
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

    public static class NewChatMessage{
        app.mycity.mycity.api.model.Message message;
        int out;


        public NewChatMessage(app.mycity.mycity.api.model.Message message, int our) {
          this.message = message;
          this.out = our;
        }

        public app.mycity.mycity.api.model.Message getMessage() {
            return message;
        }

        public int getOut() {
            return out;
        }
    }

    public static class MessageWasRead{
        long messageId;

        public MessageWasRead(long messageId) {
            this.messageId = messageId;
        }

        public long getMessageId() {
            return messageId;
        }
    }

    public static class UpdateDialog {
        private String message;
        private String id;

        public UpdateDialog(String id, String message) {
            this.message = message;
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public String getId() {
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
        int position;
        public SortPlaces(int adapterPosition) {
            position = adapterPosition;
        }

        public int getPosition() {
            return position;
        }
    }

    public static class DeleteChatMessage {
        long id;

        public DeleteChatMessage(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }
    }

    public static class DeleteDialog {
        String id;
        public DeleteDialog(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }
    }

    public static class OpenNotifications {
    }

    public static class SortPeople {
        int position;
        public SortPeople(int adapterPosition) {
            position = adapterPosition;
        }

        public int getPosition() {
            return position;
        }
    }

    public static class OpenFeed {
    }

    public static class OpenChronics {
    }

    public static class OpenPlaces {
    }

    public static class OpenPeople {
    }

    public static class OpenProfile {
    }

    public static class OpenMenu {
    }

    public static class OpenChat {
    }

    public static class OpenEvents {
    }

    public static class OpenCheckins {
    }

    public static class UpdateCoordinates {
    }

    public static class LocationStop {
    }

    public static class LocationResume {
    }

    public static class PublicationComplete {
    }

    public static class MainSettings {
    }

    public static class PublicationError {
    }

    public static class ClickOnSliderImage {
    }

    public static class OpenPlaceContent {
        String postId;

        public OpenPlaceContent(String postId) {
            this.postId = postId;
        }

        public String getPostId() {
            return postId;
        }
    }

    public static class OpenCheckinProfileContent {
        private String postId;
        private String storageKey;

        public OpenCheckinProfileContent(String postId, String storageKey) {
            this.postId = postId;
            this.storageKey = storageKey;
        }

        public String getStorageKey() {
            return storageKey;
        }

        public String getPostId() {
            return postId;
        }
    }

    public static class OpenEventContent {

        private String eventId;
        private String ownerId;
        private String placeName;

        public OpenEventContent(String eventId, String ownerId, String placeName) {
            this.eventId = eventId;
            this.ownerId = ownerId;
            this.placeName = placeName;
        }

        public String getEventId() {
            return eventId;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public String getPlaceName() {
            return placeName;
        }
    }

    public static class OpenActionContent {

        private String eventId;
        private String ownerId;
        private String placeName;

        public OpenActionContent(String eventId, String ownerId, String placeName) {
            this.eventId = eventId;
            this.ownerId = ownerId;
            this.placeName = placeName;
        }

        public String getEventId() {
            return eventId;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public String getPlaceName() {
            return placeName;
        }
    }

    public static class DialogUpdate{
        String dialogId;
        String messageText;
        int unreadCount;
        int time;

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public DialogUpdate(String dialogId, String messageText, int unreadCount, int time) {
            this.dialogId = dialogId;
            this.messageText = messageText;
            this.unreadCount = unreadCount;
            this.time = time;
        }

        public String getDialogId() {
            return dialogId;
        }

        public void setDialogId(String dialogId) {
            this.dialogId = dialogId;
        }

        public String getMessageText() {
            return messageText;
        }

        public void setMessageText(String messageText) {
            this.messageText = messageText;
        }

        public int getUnreadCount() {
            return unreadCount;
        }

        public void setUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
        }
    }

    public static class UnreadCountUpdate{

    }
}
