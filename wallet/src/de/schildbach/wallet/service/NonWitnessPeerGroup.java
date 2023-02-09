package org.apache.cordova.radiocoin.wallet.service;

import org.bitcoinj.core.AbstractBlockChain;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.VersionMessage;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Because someone thought it was fun to hardcode witness requirement even though
 * THIS SAME DAMN CLASS implements a service filter, we have to override the respective method.
 * very not wow...
 */
public class NonWitnessPeerGroup extends PeerGroup {
    public NonWitnessPeerGroup(NetworkParameters params, @Nullable AbstractBlockChain chain) {
        super(params, chain);
    }

    /**
     * In an ideal world this would be a copy/paste job,
     * but FOR SOME REASON some of this crap is private. AAAAAAAAAAHHHHHHHHHHHHHHHH
     */
    @Override
    protected Peer selectDownloadPeer(List<Peer> peers) {
        // Characteristics to select for in order of importance:
        //  - Chain height is reasonable (majority of nodes)
        //  - High enough protocol version for the features we want (but we'll settle for less)
        //  - Randomly, to try and spread the load.
        if (peers.isEmpty())
            return null;

        int mostCommonChainHeight = getMostCommonChainHeight(peers);
        // Make sure we don't select a peer if there is no consensus about block height.
        if (mostCommonChainHeight == 0)
            return null;

        // Only select peers that announce the minimum protocol and services and that we think is fully synchronized.
        List<Peer> candidates = new LinkedList<>();
        final int MINIMUM_VERSION = params.getProtocolVersionNum(NetworkParameters.ProtocolVersion.WITNESS_VERSION);
        for (Peer peer : peers) {
            final VersionMessage versionMessage = peer.getPeerVersionMessage();
            if (versionMessage.clientVersion < MINIMUM_VERSION)
                continue;
            if (!versionMessage.hasBlockChain())
                continue;
            final long peerHeight = peer.getBestHeight();
            if (peer.getPeerBlockHeightDifference() < 0)
                continue;
            //if (peerHeight < mostCommonChainHeight || peerHeight > mostCommonChainHeight + 1)
            //   continue;
            candidates.add(peer);
        }
        if (candidates.isEmpty())
            return null;

        // Random poll.
        int index = (int) (Math.random() * candidates.size());
        return candidates.get(index);
    }
}
