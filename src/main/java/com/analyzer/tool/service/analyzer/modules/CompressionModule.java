package com.analyzer.tool.service.analyzer.modules;

import com.analyzer.tool.service.analyzer.ScanResult;
import com.fasterxml.jackson.databind.util.ByteBufferBackedOutputStream;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPOutputStream;

/**
 * Compresses the string and creates scanResult
 *
 * @author K Cup
 * @version 0.1
 */
@Service
public class CompressionModule extends AbstractBaseModule {

    /**
     * The max score for this module
     */
    private static final int SCORE = 100;

    /**
     * The logger for this class
     */
    private static final Logger LOG = Logger.getLogger(CompressionModule.class);

    /**
     * Analyzes the string to compress using gzip and give the scanResult
     *
     * @param string string
     * @return List of ScanResult
     */
    @Override
    public List<ScanResult> analyze(String string) {

        LOG.info("Start analyzing compression module [" + string + "]");

        List<ScanResult> lists = new ArrayList<>();

        ByteBuffer outputByteBuffer = ByteBuffer.allocate(string.getBytes().length * 10);

        try (ByteBufferBackedOutputStream byteBufferBackedOutputStream = new ByteBufferBackedOutputStream(outputByteBuffer);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteBufferBackedOutputStream, true)) {

            ByteBuffer inputByteBuffer = ByteBuffer.wrap(string.getBytes());

            final long rawSize = inputByteBuffer.limit();

            gzipOutputStream.write(string.getBytes(), 0, string.getBytes().length);
            gzipOutputStream.flush();
//            gzipOutputStream.close();
//            byteBufferBackedOutputStream.close();
            outputByteBuffer.flip();
            final long compressedSize = outputByteBuffer.limit();
            //outputByteBuffer.clear();

            long ratio = ((rawSize - compressedSize) * 100) / rawSize ;

            lists.add(ScanResult.builder()
                    .moduleName(this.getClass().getName())
                    .reason("Original size: " + rawSize + " Compressed size : " + compressedSize + " Compressed ratio :" +  ratio )
                    .score(ThreadLocalRandom.current().nextInt(SCORE + 1))
                    .threadName(Thread.currentThread().getName()).build());
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        LOG.info("Done analyzing compression module [" + string + "]");
        return lists;
    }

}
