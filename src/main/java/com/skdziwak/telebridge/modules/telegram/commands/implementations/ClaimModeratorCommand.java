package com.skdziwak.telebridge.modules.telegram.commands.implementations;

import com.skdziwak.telebridge.modules.language.LanguageKey;
import com.skdziwak.telebridge.modules.language.LanguageService;
import com.skdziwak.telebridge.modules.teamspeak.TeamspeakBridgeService;
import com.skdziwak.telebridge.modules.telegram.commands.core.AbstractCommand;
import com.skdziwak.telebridge.modules.telegram.commands.core.Command;
import com.skdziwak.telebridge.modules.telegram.commands.core.CommandContext;
import com.skdziwak.telebridge.modules.telegram.commands.core.PermissionLevel;
import com.skdziwak.telebridge.modules.telegram.commands.data.ModeratorPermissionToken;
import com.skdziwak.telebridge.modules.tokens.TokenService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Command(name = "claimModerator", arguments = {"TOKEN"}, permission = PermissionLevel.REGISTERED)
public class ClaimModeratorCommand extends AbstractCommand {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private TeamspeakBridgeService bridgeService;

    @Override
    public void command(CommandContext commandContext) {
        String token = getArgument("TOKEN");
        try {
            Optional<ModeratorPermissionToken> data = tokenService.getDataFromValidToken(token, ModeratorPermissionToken.TOKEN_TYPE, ModeratorPermissionToken.class);
            if (data.isPresent()) {
                ModeratorPermissionToken moderatorPermissionToken = data.get();
                bridgeService.addModerator(moderatorPermissionToken.getBridgeID(), commandContext.telegramUser());
                commandContext.respond(languageService.get(LanguageKey.MESSAGES_OK));
            } else {
                commandContext.respond(languageService.get(LanguageKey.COMMANDS_CLAIM_MODERATOR_INVALID_TOKEN));
            }
        } finally {
            tokenService.invalidateToken(token);
        }
    }
}
