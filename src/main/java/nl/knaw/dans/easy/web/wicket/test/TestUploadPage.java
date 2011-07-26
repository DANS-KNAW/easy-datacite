package nl.knaw.dans.easy.web.wicket.test;

import java.io.File;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUploadPage extends AbstractEasyNavPage {

	private static final Logger LOG = LoggerFactory.getLogger(TestUploadPage.class);


	public TestUploadPage() {
		add(new EasyUpload("uploadPanel") {
			/**
			 *
			 */
			private static final long serialVersionUID = 0L;

			@Override
			public void onReceivedFiles(Map<String, String> clientParams, String basePath, List<File> files) {
				LOG.info("Received files: "+ files.toString());
				LOG.info("Received clientParams: "+ clientParams.toString());
			}

		});
	}
}
