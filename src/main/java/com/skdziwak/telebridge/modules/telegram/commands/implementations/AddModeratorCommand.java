package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakUtils;
import com.skdziwak.telebridge.modules.telegram.commands.core.*;
import com.skdziwak.telebridge.modules.telegram.commands.data.ModeratorPermissionToken;
import com.skdziwak.telebridge.modules.tokens.TokenService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.Duration;

@Command(name = "addModerator", arguments = {"NAME"}, requiresBridge = true, permission = PermissionLevel.BRIDGE_OWNER)
public class AddModeratorCommand extends AbstractCommand {

    @Autowired
    private TeamspeakUtils teamspeakUtils;

    @Autowired
    private TS3Api ts3Api;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private TokenService tokenService;

    @Override
    public void command(CommandContext commandContext) {
        Client client = teamspeakUtils.findClientByAliasOrNickname(getArgument("NAME"))
                .orElseThrow(() -> new CommandResponseException(languageService.get(LanguageKey.COMMANDS_MESSAGE_CLIENT_NOT_FOUND)));
        ModeratorPermissionToken moderatorPermissionToken = new ModeratorPermissionToken();
        moderatorPermissionToken.setBridgeID(commandContext.bridge().getId());
        String token = tokenService.createToken(ModeratorPermissionToken.TOKEN_TYPE, moderatorPermissionToken, new Timestamp(System.currentTimeMillis() + Duration.ofHours(1).toMillis()));

        ts3Api.sendPrivateMessage(client.getId(), languageService.get(LanguageKey.COMMANDS_ADD_MODERATOR_MESSAGE, "token", token));
        commandContext.respond(languageService.get(LanguageKey.MESSAGES_OK));
    }
}
