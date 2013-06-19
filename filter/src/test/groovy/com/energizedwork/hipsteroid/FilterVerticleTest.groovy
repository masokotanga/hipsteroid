package com.energizedwork.hipsteroid

import java.util.zip.CRC32
import org.junit.Test
import org.vertx.java.core.*
import org.vertx.java.core.buffer.Buffer
import org.vertx.java.core.eventbus.Message
import static org.vertx.testtools.VertxAssert.*

class FilterVerticleTest extends ModuleDeployingTestVerticle {

	static final File INPUT_FILE = loadResource("manhattan.jpg")
	static final Map<String, File> FILTERED_FILES = [
	        gotham: loadResource("manhattan-gotham.jpg")
	].asImmutable()
	static final Map<String, File> THUMB_FILES = [
	        gotham: loadResource("manhattan-gotham.thumb.jpg")
	].asImmutable()

	@Test
    public void filtersAnImage() {
		vertx.fileSystem().readFile(INPUT_FILE.absolutePath, { AsyncResult<Buffer> input ->
			vertx.eventBus().send("hipsteroid.filter.gotham.full", input.result(), { Message<Buffer> reply ->
				assertChecksumsEqual FILTERED_FILES.gotham, reply
				testComplete()
			} as Handler)
		} as Handler)
	}

	@Test
    public void createsAnImageThumbnail() {
		vertx.fileSystem().readFile(INPUT_FILE.absolutePath, { AsyncResult<Buffer> input ->
			vertx.eventBus().send("hipsteroid.filter.gotham.thumb", input.result(), { Message<Buffer> reply ->
				assertChecksumsEqual THUMB_FILES.gotham, reply
				testComplete()
			} as Handler)
		} as Handler)
	}

	static void assertChecksumsEqual(File expected, Message<Buffer> actual) {
		assertEquals "checksums do not match", getChecksum(expected), checksum(actual.body())
	}

	private static File loadResource(String name) {
		new File(FilterVerticleTest.getResource("/$name").toURI())
	}

	private static long checksum(Buffer buffer) {
		getChecksum buffer.bytes
	}

	private static long getChecksum(File file) {
		getChecksum file.bytes
	}

	private static long getChecksum(byte[] bytes) {
		def checksum = new CRC32()
		checksum.update(bytes)
		return checksum.value
	}

}
