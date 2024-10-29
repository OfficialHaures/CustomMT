package nl.inferno.customMT.Discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordMain {
    JDA jda;
    String token;

    public DiscordMain(String token) {
        this.token = token;
        startBot();
    }

    private void startBot() {
        JDABuilder builder = JDABuilder.createDefault(token);

        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(net.dv8tion.jda.api.entities.Activity.playing("CustomMT"));

        builder.enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_PRESENCES
        );
        try {
            jda = builder.build();
          jda.awaitReady();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public JDA getJDA() {
        return jda;
    }
}
