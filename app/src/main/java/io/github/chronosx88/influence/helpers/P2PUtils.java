package io.github.chronosx88.influence.helpers;

import com.google.gson.Gson;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FuturePing;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import java.util.Map;

public class P2PUtils {
    private static Gson gson = new Gson();
    private static PeerDHT peerDHT = AppHelper.getPeerDHT();

    public static boolean ping(PeerAddress recipientPeerAddress) {
        // For connection opening
        for (int i = 0; i < 2; i++) {
            peerDHT
                    .peer()
                    .ping()
                    .tcpPing(true)
                    .peerAddress(recipientPeerAddress)
                    .start()
                    .awaitUninterruptibly();
        }

        FuturePing ping = peerDHT
                .peer()
                .ping()
                .tcpPing(true)
                .peerAddress(recipientPeerAddress)
                .start()
                .awaitUninterruptibly();
        return ping.isSuccess();
    }

    public static boolean put(String locationKey, String contentKey, Data data) {
        FuturePut futurePut = peerDHT
                .put(Number160.createHash(locationKey))
                .data(contentKey == null ? Number160.ZERO : Number160.createHash(contentKey), data)
                .start()
                .awaitUninterruptibly();
        return futurePut.isSuccess();
    }

    public static Map<Number640, Data> get(String locationKey) {
        FutureGet futureGet = peerDHT
                .get(Number160.createHash(locationKey))
                .all()
                .start()
                .awaitUninterruptibly();
        if(!futureGet.isEmpty()) {
            return futureGet.dataMap();
        }
        return null;
    }
}