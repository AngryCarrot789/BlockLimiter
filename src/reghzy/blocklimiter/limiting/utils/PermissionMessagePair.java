package reghzy.blocklimiter.limiting.utils;

public class PermissionMessagePair {
    public String permission;
    public String denyMessage;

    public PermissionMessagePair(String permission, String denyMessage) {
        this.permission = permission;
        this.denyMessage = denyMessage;
    }
}
