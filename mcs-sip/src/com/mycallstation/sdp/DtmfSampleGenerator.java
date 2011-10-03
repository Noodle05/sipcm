/**
 * 
 */
package com.mycallstation.sdp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.stereotype.Component;

/**
 * @author wgao
 * 
 */
@Component("dtmfGenerator")
public class DtmfSampleGenerator {
	private final Map<Character, byte[]> samples;
	private static final Map<Character, String> sampleNames;
	static {
		sampleNames = new HashMap<Character, String>(16);
		sampleNames.put('0', "/audio/zero.au");
		sampleNames.put('1', "/audio/one.au");
		sampleNames.put('2', "/audio/two.au");
		sampleNames.put('3', "/audio/three.au");
		sampleNames.put('4', "/audio/four.au");
		sampleNames.put('5', "/audio/five.au");
		sampleNames.put('6', "/audio/six.au");
		sampleNames.put('7', "/audio/seven.au");
		sampleNames.put('8', "/audio/eight.au");
		sampleNames.put('9', "/audio/nine.au");
		sampleNames.put('*', "/audio/star.au");
		sampleNames.put('#', "/audio/hash.au");
		sampleNames.put('A', "/audio/A.au");
		sampleNames.put('B', "/audio/B.au");
		sampleNames.put('C', "/audio/C.au");
		sampleNames.put('D', "/audio/D.au");
	}

	public DtmfSampleGenerator() {
		samples = new HashMap<Character, byte[]>(16);
	}

	@PostConstruct
	public void init() {
		byte[] bytes = new byte[512];
		for (Entry<Character, String> entry : sampleNames.entrySet()) {
			ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
			InputStream is = DtmfSampleGenerator.class
					.getResourceAsStream(entry.getValue());
			int readBytes;
			try {
				while ((readBytes = is.read(bytes)) > 0) {
					os.write(bytes, 0, readBytes);
				}
				byte[] byteData = os.toByteArray();
				samples.put(entry.getKey(), byteData);
			} catch (Exception e) {
				throw new BeanInitializationException(
						"Cannot load sample audio clips.", e);
			} finally {
				try {
					is.close();
					os.close();
				} catch (IOException e) {
					// ignore it.
				}
			}
		}
	}

	public Clip getDtmfClipForButton(char c)
			throws UnsupportedAudioFileException, IOException,
			LineUnavailableException {
		byte[] byteData = samples.get(c);
		if (byteData == null) {
			return null;
		}
		ByteArrayInputStream is = new ByteArrayInputStream(byteData);
		AudioInputStream as = AudioSystem.getAudioInputStream(is);
		Clip clip = AudioSystem.getClip();
		clip.open(as);
		return clip;
	}
}
