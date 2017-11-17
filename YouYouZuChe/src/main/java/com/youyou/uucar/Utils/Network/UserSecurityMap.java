package com.youyou.uucar.Utils.Network;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by taurusxi on 14-9-6.
 */
public class UserSecurityMap {


    public static final ConcurrentHashMap<Integer, SecurityItem> requestSecurityMap = new ConcurrentHashMap<Integer, SecurityItem>();


    public static void put(int seq, SecurityItem item) {

        if (requestSecurityMap.size() > 20) {
        }
        requestSecurityMap.put(seq, item);
    }

    public static SecurityItem get(int seq) {
        return requestSecurityMap.get(seq);
    }

    public static void remove(int seq) {
        if (requestSecurityMap.contains(seq)) {
            requestSecurityMap.remove(seq);
        }
    }

    public static class SecurityItem {
        public NetworkTask networkItem;
        public boolean isPublic;
        public byte[] b3KeyItem;
        public byte[] b2Item;
        public byte[] b3Item;
        public int userIdItem;


        public SecurityItem(final NetworkTask networkItem, boolean isPublic, byte[] b3KeyItem, byte[] b2Item, byte[] b3Item, int userIdItem) {
            this.networkItem = networkItem;
            this.isPublic = isPublic;
            this.b3KeyItem = b3KeyItem;
            this.b2Item = b2Item;
            this.b3Item = b3Item;
            this.userIdItem = userIdItem;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("isPublic:" + isPublic + "_");
            stringBuilder.append("userIdItem:" + userIdItem + "_");
            stringBuilder.append("networkItem:" + networkItem.toString() + "_");
            return stringBuilder.toString();
        }

    }


}
