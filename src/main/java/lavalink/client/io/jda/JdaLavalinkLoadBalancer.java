package lavalink.client.io.jda;

import edu.umd.cs.findbugs.annotations.NonNull;
import lavalink.client.io.Lavalink;
import lavalink.client.io.LavalinkLoadBalancer;
import lavalink.client.io.LavalinkSocket;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.util.List;
import java.util.stream.Collectors;

public class JdaLavalinkLoadBalancer extends LavalinkLoadBalancer {

    public JdaLavalinkLoadBalancer(Lavalink lavalink) {
        super(lavalink);
    }

    @NonNull
    @Override
    public LavalinkSocket determineBestSocket(long guildId) {
        LavalinkSocket leastPenalty = null;
        int record = Integer.MAX_VALUE;

        JdaLavalink link = (JdaLavalink) lavalink;
        JDA jda = link.getJdaFromSnowflake(Long.toUnsignedString(guildId));
        Guild guild = jda.getGuildById(guildId);
        String guildRegion = guild.getRegionRaw();

        @SuppressWarnings("unchecked")
        List<LavalinkSocket> nodes = lavalink.getNodes();

        List<LavalinkSocket> filteredNodes = nodes.stream()
                .filter(LavalinkSocket::isAvailable)
                .filter((socket) ->
                        socket.getRegion().getJDARegions().stream()
                                .anyMatch((r) ->
                                        r.equalsIgnoreCase(guildRegion)
                                )
                ).collect(Collectors.toList());

        if (!filteredNodes.isEmpty()) {
            nodes = filteredNodes;
        }

        for (LavalinkSocket socket : nodes) {
            int total = getPenalties(socket, guildId, penaltyProviders).getTotal();

            if (total < record) {
                leastPenalty = socket;
                record = total;
            }
        }

        if (leastPenalty == null || !leastPenalty.isAvailable())
            throw new IllegalStateException("No available nodes!");

        return leastPenalty;
    }
}
