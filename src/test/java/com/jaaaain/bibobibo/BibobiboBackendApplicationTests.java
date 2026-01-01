package com.jaaaain.bibobibo;

import com.jaaaain.bibobibo.infrastructure.FfmpegClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BibobiboBackendApplicationTests {

	@Autowired
	private FfmpegClient ffmpegClient;

	@Test
	void contextLoads() {
		System.out.println(ffmpegClient.analyzeVideo("https://jaain.oss-cn-hangzhou.aliyuncs.com/20251113/video/17630455483224df6e5ff48db42259be9124329be579c.mp4"));
	}

}
