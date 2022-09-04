package com.skdziwak.telebridge.jpa.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class TeamspeakBridge {
    @Id
    @GeneratedValue
    private Long id;
    private String host;
    private String login;
    private String password;
    private Integer port;
    private Integer queryPort;
    private Long chatId;
    @ManyToOne
    private TelegramUser owner;
    @ManyToMany
    private List<TelegramUser> moderators;

    public List<TelegramUser> getModerators() {
        return moderators;
    }

    public void setModerators(List<TelegramUser> moderators) {
        this.moderators = moderators;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getQueryPort() {
        return queryPort;
    }

    public void setQueryPort(Integer queryPort) {
        this.queryPort = queryPort;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public TelegramUser getOwner() {
        return owner;
    }

    public void setOwner(TelegramUser owner) {
        this.owner = owner;
    }
}
