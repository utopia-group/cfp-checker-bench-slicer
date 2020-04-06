import org.bitcoinj.core.Peer;
import org.bitcoinj.core.AbstractBlockChain;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.core.VersionMessage;
import org.bitcoinj.core.MessageSerializer;

import java.nio.ByteBuffer;

import java.net.InetAddress;

public class Harness extends Peer
{
    NetworkParameters params;
    
    private static int nd$int()
    {
        return 1;
    }

    static void assume(boolean b) {}

    public Harness(NetworkParameters params, VersionMessage ver, PeerAddress remoteAddress, AbstractBlockChain chain)
    {
        super(params, ver, remoteAddress, chain);
        this.params = params;
    }

    public void run() throws Exception
    {
        MessageSerializer serializer = params.getDefaultSerializer();
        int reps = nd$int(), sz = nd$int();

        assume(reps > 0 && sz > 0);
        for(int i = 0; i < reps; i++)
        {
            ByteBuffer b = ByteBuffer.allocate(sz);
            processMessage(serializer.deserialize(b));
        }
    }

    public static void main(String[] args) throws Exception
    {
        NetworkParameters params = MainNetParams.get();
        Harness h = new Harness(params, new VersionMessage(params, 0), new PeerAddress(params, InetAddress.getByName("")), null);
        h.run();
    }
}
