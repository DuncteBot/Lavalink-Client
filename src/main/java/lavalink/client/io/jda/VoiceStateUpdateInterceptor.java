package lavalink.client.io.jda;

import lavalink.client.io.Link;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.handle.VoiceStateUpdateHandler;
import org.json.JSONObject;

public class VoiceStateUpdateInterceptor extends VoiceStateUpdateHandler {

    private final JdaLavalink lavalink;

    VoiceStateUpdateInterceptor(JdaLavalink lavalink, JDAImpl jda) {
        super(jda);
        this.lavalink = lavalink;
    }

    @Override
    protected Long handleInternally(JSONObject content) {
        final Long guildId = content.has("guild_id") ? content.getLong("guild_id") : null;
        if (guildId != null && getJDA().getGuildSetupController().isLocked(guildId))
            return guildId;
        if (guildId == null)
            return super.handleInternally(content);

        final long userId = content.getLong("user_id");
        final Long channelId = !content.isNull("channel_id") ? content.getLong("channel_id") : null;
        Guild guild = getJDA().getGuildById(guildId);
        if (guild == null) return super.handleInternally(content);

        Member member = guild.getMemberById(userId);
        if (member == null) return super.handleInternally(content);

        // We only need special handling if our own state is modified
        if (!member.equals(guild.getSelfMember())) return super.handleInternally(content);

        VoiceChannel channel = channelId != null ? guild.getVoiceChannelById(channelId) : null;
        JdaLink link = lavalink.getLink(guildId.toString());

        if (channelId == null) {
            // Null channel means disconnected
            if (link.getState() != Link.State.DESTROYED) {
                link.onDisconnected();
            }
        } else if (channel != null) {
            link.setChannel(channel.getId()); // Change expected channel
        }

        if (link.getState() == Link.State.CONNECTED) {
            getJDA().getClient().updateAudioConnection(guildId, channel);
        }

        return super.handleInternally(content);
    }
}
