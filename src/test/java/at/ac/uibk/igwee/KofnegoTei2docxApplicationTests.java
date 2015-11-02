package at.ac.uibk.igwee;

import at.ac.uibk.igwee.controller.ConversionConfig;
import at.ac.uibk.igwee.controller.MainConverter;
import at.ac.uibk.igwee.controller.ProcessingCommand;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class KofnegoTei2docxApplicationTests {

	@Autowired
	MainConverter mainConverter;

	@Test
	public void contextLoads() {

		Assert.assertNotNull(mainConverter);
	}

	@Test
	public void convertOne() throws Exception {

		List<ProcessingCommand> pss = new ArrayList<>();
		ProcessingCommand pc1 = new ProcessingCommand("commentToRef", null);
		ProcessingCommand pc2 = new ProcessingCommand("addRN2P", null);
		pss.add(pc1);
		pss.add(pc2);

		ConversionConfig cc = new ConversionConfig(pss);

		InputStream result = mainConverter.convert(new File("./src/test/resources/testdocuments/testdocument.docx"), cc);
		System.out.println();
		IOUtils.copy(result, System.out);
		System.out.println();
	}

}
