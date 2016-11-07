package org.swisspush.reststorage.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.swisspush.reststorage.RedisStorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Util class to compress and decompress resources using the gzip algorithm
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class GZIPUtil {

    private static Logger log = LoggerFactory.getLogger(RedisStorage.class);

    /**
     * Compress the uncompressed data with the gzip algorithm. When the compression is done, the resultHandler is called
     * with the compressed data as result.
     *
     * @param vertx vertx
     * @param uncompressedData the data to compress
     * @param uncompressedData the data to compress
     * @param uncompressedData the data to compress
     * @param resultHandler the resultHandler is called when the compression is done
     */
    public static void compressResource(Vertx vertx, byte[] uncompressedData, Handler<AsyncResult<byte[]>> resultHandler) {
        vertx.executeBlocking(future -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try (GZIPOutputStream os = new GZIPOutputStream(baos)) {
                os.write(uncompressedData);
            } catch (IOException ioe) {
                log.error("Unable to compress resource: " + ioe.getMessage());
                future.fail(ioe);
                // Error, exit
                return;
            }
            // Success
            future.complete(baos.toByteArray());
        }, resultHandler);
    }

    /**
     * Decompress the compressed (gzip) data. When the decompression is done, the resultHandler is called
     * with the decompressed data as result.
     * @param vertx vertx
     * @param compressedData the data to decompress
     * @param resultHandler the resultHandler is called when the compression is done
     */
    public static void decompressResource(Vertx vertx, byte[] compressedData, Handler<AsyncResult<byte[]>> resultHandler) {
        vertx.executeBlocking(future -> {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
                 GZIPInputStream gzipInputStream = new GZIPInputStream(bis)) {

                int bytes_read;

                while ((bytes_read = gzipInputStream.read(buffer)) > 0) {
                    baos.write(buffer, 0, bytes_read);
                }

                gzipInputStream.close();
                baos.close();

            } catch (IOException ioe) {
                log.error("Unable to decompress resource: " + ioe.getMessage());
                future.fail(ioe);
                // Error, exit
                return;
            }
            // Success
            future.complete(baos.toByteArray());
        }, resultHandler);
    }
}
