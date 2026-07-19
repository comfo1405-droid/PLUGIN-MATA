package com.messhy.plugin.data;

public class UserAccount {
    public String username;
    public String uuid;
    public String passwordHash;
    public String salt;
    public String registeredIp;
    public String lastIp;
    public long registeredAt;
    public long lastLogin;

    public UserAccount() {
    }

    public UserAccount(String username, String uuid, String passwordHash, String salt, String ip) {
        this.username = username;
        this.uuid = uuid;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.registeredIp = ip;
        this.lastIp = ip;
        this.registeredAt = System.currentTimeMillis();
        this.lastLogin = 0L;
    }
}
