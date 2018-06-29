package app.mycity.mycity.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UsersContainer {

        @SerializedName("count")
        @Expose
        private Integer count;
        @SerializedName("items")
        @Expose
        private List<User> friends = null;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public List<User> getFriends() {
            return friends;
        }

        public void setFriends(List<User> friends) {
            this.friends = friends;
        }
}
