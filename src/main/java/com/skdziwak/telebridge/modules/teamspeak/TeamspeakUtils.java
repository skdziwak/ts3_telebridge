package com.skdziwak.telebridge.modules.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.skdziwak.telebridge.jpa.entities.TeamspeakBridge;
import com.skdziwak.telebridge.jpa.entities.TeamspeakUser;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.telegram.commands.core.scope.CommandScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Component
@Scope(CommandScope.COMMAND_SCOPE)
public class TeamspeakUtils {
    @Autowired
    private TS3Api ts3Api;

    @Autowired
    private TeamspeakBridge teamspeakBridge;

    @Autowired
    private TeamspeakUserRepository userRepository;

    @Autowired
    private final LanguageService languageService;

    public TeamspeakUtils(TS3Api ts3Api, TeamspeakBridge teamspeakBridge, TeamspeakUserRepository userRepository, LanguageService languageService) {
        this.ts3Api = ts3Api;
        this.teamspeakBridge = teamspeakBridge;
        this.userRepository = userRepository;
        this.languageService = languageService;
    }

    public Optional<Client> findClientByAliasOrNickname(String alias) {
        List<Client> clients = ts3Api.getClients();

        Optional<TeamspeakUser> user = userRepository.findFirstByAliasAndTeamspeakBridgeId(alias, teamspeakBridge.getId());
        if (user.isPresent() && user.get().getAlias() != null) {
            String uniqueID = user.get().getUserUniqueID();
            for (Client client : clients) {
                if (client.getUniqueIdentifier().equals(uniqueID)) {
                    return Optional.of(client);
                }
            }
        } else {
            for (Client client : clients) {
                if (client.getNickname().equals(alias)) {
                    return Optional.of(client);
                }
            }

        }
        return Optional.empty();
    }

    @Transactional
    public void hideUser(String uniqueIdentifier) {
        TeamspeakUser user = userRepository.findFirstByUserUniqueIDAndTeamspeakBridgeId(uniqueIdentifier, teamspeakBridge.getId())
                .orElseGet(() -> {
                    TeamspeakUser newUser = new TeamspeakUser();
                    newUser.setUserUniqueID(uniqueIdentifier);
                    newUser.setTeamspeakBridge(teamspeakBridge);
                    return newUser;
                });
        user.setHidden(!user.getHidden());
        userRepository.save(user);
    }

    @Transactional
    public void setAlias(String uniqueIdentifier, String alias) {
        TeamspeakUser userAlias = userRepository.findFirstByUserUniqueIDAndTeamspeakBridgeId(uniqueIdentifier, teamspeakBridge.getId())
                .orElseGet(() -> {
                    TeamspeakUser newUserAlias = new TeamspeakUser();
                    newUserAlias.setUserUniqueID(uniqueIdentifier);
                    newUserAlias.setTeamspeakBridge(teamspeakBridge);
                    return newUserAlias;
                });
        userAlias.setAlias(alias);
        userRepository.save(userAlias);
    }

    public Optional<String> getClientAliasByUniqueIdentifier(String uniqueID) {
        Optional<TeamspeakUser> userOptional = userRepository.findFirstByUserUniqueIDAndTeamspeakBridgeId(uniqueID, teamspeakBridge.getId());
        return userOptional.map(TeamspeakUser::getAlias);
    }

    public String getClientDisplayedName(Client client) {
        Optional<TeamspeakUser> aliasOptional = userRepository.findFirstByUserUniqueIDAndTeamspeakBridgeId(client.getUniqueIdentifier(), teamspeakBridge.getId());
        if (aliasOptional.isPresent()) {
            String alias = aliasOptional.get().getAlias();
            return alias == null ? client.getNickname() : alias;
        } else {
            return client.getNickname();
        }
    }

    public Boolean isUserHidden(Client client) {
        return isUserHidden(client.getUniqueIdentifier());
    }

    public Boolean isUserHidden(String uniqueIdentifier) {
        Optional<TeamspeakUser> user = userRepository.findFirstByUserUniqueIDAndTeamspeakBridgeId(uniqueIdentifier, teamspeakBridge.getId());
        if (user.isPresent()) {
            return user.get().getHidden();
        }
        return false;
    }

    public String serializeClient(Client client, boolean showDetails) {
        StringBuilder builder = new StringBuilder(getClientDisplayedName(client));
        if (client.isInputMuted() | client.isOutputMuted()) {
            builder.append(" (").append(languageService.get(LanguageKey.COMMANDS_LIST_USER_MUTED)).append(")");
        }
        if (showDetails) {
            builder.append(" [").append(client.getUniqueIdentifier()).append("]");
            if (isUserHidden(client)) {
                builder.append(" HIDDEN");
            }
        }
        return builder.toString();
    }
}
