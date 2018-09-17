package penetration.pk.lucidxpo.ynami.scanners;

import penetration.pk.lucidxpo.ynami.model.Port;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static penetration.pk.lucidxpo.ynami.model.State.CLOSED;
import static penetration.pk.lucidxpo.ynami.model.State.OPEN;
import static penetration.pk.lucidxpo.ynami.model.State.TIMEDOUT;

public class PortScanner {
    private final int threads;
    private final String host;
    private final int endPort;
    private final int startPort;
    private final int msTimeout;

    public PortScanner(final String host,
                       final int startPort,
                       final int endPort,
                       final int threads,
                       final int msTimeout) {
        this.host = host;
        this.threads = threads;
        this.endPort = endPort;
        this.startPort = startPort;
        this.msTimeout = msTimeout;
    }

    public List<Port> scan() throws ExecutionException, InterruptedException {
        final ExecutorService executorService = newFixedThreadPool(threads);
        final List<Future<Port>> futures = rangeClosed(startPort, endPort)
                .mapToObj(port -> scanPort(port, host, msTimeout, executorService))
                .collect(toList());
        executorService.shutdown();
        final List<Port> results = newArrayList();
        for (final Future<Port> future : futures) {
            results.add(future.get());
        }
        return results;
    }

    private static Future<Port> scanPort(final int port,
                                         final String ip,
                                         final int timeout,
                                         final ExecutorService executorService) {
        return executorService.submit(() -> {
            final Port result = new Port(port);
            try {
                final Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port), timeout);
                socket.close();
                result.setState(OPEN);
            } catch (final SocketTimeoutException ste) {
                result.setState(TIMEDOUT);
            } catch (final Exception ex) {
                result.setState(CLOSED);
            }
            return result;
        });
    }
}
