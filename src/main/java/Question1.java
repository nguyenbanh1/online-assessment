import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


/**
 *  The count words will run file in resources when run.
 *  You can change fileName in code.
 *
 *  For example: file text.txt in resource
 *
 *
 *
 */
public class Question1 {

    private static final int THREAD_COUNT = 20;

    public static void main(String[] args) throws IOException {

        var start = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        //TODO: Change your large file to test
        String fileName = "./src/main/resources/test.txt";

        try {
            countWords(executor, fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        executor.shutdown();
        System.out.printf("\nEnd process with time %s ms", System.currentTimeMillis() - start);
    }

    private static void countWords(ExecutorService executorService, String fileName) throws Exception {
        List<Future<Map<String, Long>>> wordsToCount = new ArrayList<>();

        RandomAccessFile file = new RandomAccessFile(fileName, "r");
        FileChannel channel = file.getChannel();
        AtomicLong start = new AtomicLong(0);
        long size = 1024 * 1024 * 5; //5M
        do {
            AtomicLong end = new AtomicLong(start.get() + size);
            end.set(moveToNextLine(file, end.get()));
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, start.get(), end.get() - start.get());

            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            start.set(end.get() + 1);
            String[] content = new String(bytes, StandardCharsets.UTF_8).split("\n");

            var f = executorService.submit(() -> {
                try {
                    return processBuffer(content);
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            });
            wordsToCount.add(f);
        } while (start.get() + size < file.length());

        var listResult = wordsToCount.stream().map(item -> {
            try {
                return item.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        var result = mergeMaps(listResult).entrySet().stream().map(entry -> entry.getKey() + " " + entry.getValue()).collect(Collectors.joining(", "));
        System.out.print("Output: " + result);

    }

    private static long moveToNextLine(RandomAccessFile file, long end) throws Exception {
        if (end >= file.length()) {
            return file.length();
        }
        file.seek(end);
        int ch;
        while ((ch = file.read()) != -1) {
            if (ch == '\n') { // Move to the next space or newline
                break;
            }
        }
        return file.getFilePointer();
    }

    public static Map<String, Long> mergeMaps(List<Map<String, Long>> listOfMaps) {
        Map<String, Long> mergedMap = new HashMap<>();
        for (Map<String, Long> map : listOfMaps) {
            for (Map.Entry<String, Long> entry : map.entrySet()) {
                mergedMap.merge(entry.getKey(), entry.getValue(), Long::sum);
            }
        }
        return mergedMap;
    }


    private static Map<String, Long> processBuffer(String[] content) {
        var words2Count = new HashMap<String, Long>();
        for (String line : content) {
            if (!line.isEmpty()) {
                var words = line.split(" ");
                for (var w : words) {
                    if (words2Count.containsKey(w)) {
                        words2Count.put(w, words2Count.get(w) + 1);
                    } else {
                        words2Count.put(w, 1L);
                    }
                }
            }
        }
        return words2Count;
    }
}
